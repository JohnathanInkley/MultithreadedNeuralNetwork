import Infrastructure.*;
import Infrastructure.TaskCreation.Task;
import MachineLearning.ErrorFunction.CrossEntropyError;
import MachineLearning.ErrorFunction.ErrorFunction;
import MachineLearning.LearningRateUpdating.LearningRateScaleAfterFixedNumberEpochs;
import MachineLearning.LearningRateUpdating.LearningRateUpdater;
import MachineLearning.MachineLearningModel;
import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.ForwardPassOperators.ReLuOperator;
import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Weights.WeightUpdater;
import MachineLearning.Weights.WeightUpdaterWithMaxNormRegularisation;

public class BalanceNeuralNetwork {

    public static void main(String[] args) throws InterruptedException {
        MachineLearningModel machineLearningModel = new MachineLearningModel();
        OperatorGradientPair innerProduct = new OperatorGradientPair(new InnerProductOperator(4, 1000));
        OperatorGradientPair reLu = new OperatorGradientPair(new ReLuOperator(1000));
        OperatorGradientPair innerProduct1 = new OperatorGradientPair(new InnerProductOperator(1000, 3));
        OperatorGradientPair softmax = new OperatorGradientPair(new SoftmaxOperator(3));
        ErrorFunction errorFunction = new CrossEntropyError();
        machineLearningModel.addOperatorPair(0, innerProduct);
        machineLearningModel.addOperatorPair(1, reLu);
        machineLearningModel.addOperatorPair(2, innerProduct1);
        machineLearningModel.addOperatorPair(3, softmax);
        machineLearningModel.setErrorFunction(errorFunction);

        TaskBatchDistributor taskBatchDistributor = new TaskBatchDistributor(100);
        taskBatchDistributor.loadTrainingData("src/main/resources/balanceExamples.csv", "src/main/resources/balanceLabels.csv");
        taskBatchDistributor.loadValidationData("src/main/resources/balanceExamples.csv", "src/main/resources/balanceLabels.csv");
        taskBatchDistributor.setMachineLearningModel(machineLearningModel);

        ChannelLoadManager loadManager = new ChannelLoadManager();
        TaskChannelMultiplexer<Task> channelMultiplexer = new TaskChannelMultiplexer<>();
        channelMultiplexer.setChannelLoadManager(loadManager);
        taskBatchDistributor.setOutputTaskChannel(channelMultiplexer.getInputChannel());
        ResultsHandler resultsHandler = new ResultsHandler();

        for (int i = 0; i < 3; i++) {
            TaskProcessingWorker taskProcessingWorker = new TaskProcessingWorker();
            loadManager.addChannel(taskProcessingWorker.getInputTaskChannel());
            taskProcessingWorker.setOutputChannel(resultsHandler.getInputChannel());
        }

        EpochCounter epochCounter = new EpochCounter(100);
        resultsHandler.setOutputChannel(epochCounter.getInputChannel());
        LearningRateUpdater learningRateUpdater = new LearningRateScaleAfterFixedNumberEpochs(0.1f, 10, 0.9f);
        epochCounter.setLearningRateUpdater(learningRateUpdater);
        WeightUpdater weightUpdater = new WeightUpdaterWithMaxNormRegularisation(5.5);
        weightUpdater.setDimensionArray(machineLearningModel.getArrayOfWeightDimensions());
        weightUpdater.initialiseWeights();
        epochCounter.setWeightUpdater(weightUpdater);
        epochCounter.setOutputChannel(taskBatchDistributor.getInputChannel());
        ResultLogger logger = new ResultFileLogger("src/main/resources/balanceTrainingResults.csv","src/main/resources/balanceValidationResults.csv");
        epochCounter.setLogger(logger);

        epochCounter.startExecution();
        epochCounter.waitForShutdown();
    }
}
