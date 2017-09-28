import Infrastructure.*;
import Infrastructure.TaskCreation.Task;
import MachineLearning.ErrorFunction.CrossEntropyError;
import MachineLearning.ErrorFunction.ErrorFunction;
import MachineLearning.LearningRateUpdating.LearningRateScaleAfterFixedNumberEpochs;
import MachineLearning.LearningRateUpdating.LearningRateUpdater;
import MachineLearning.MachineLearningModel;
import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Weights.WeightUpdater;
import MachineLearning.Weights.WeightUpdaterWithMaxNormRegularisation;

public class MNistNeuralNetwork {

    private static final int howManyWorkers = 4;
    private static final int numberEpochs = 100;
    private static final double maxNormConstant = 6.5;
    private static final float initialLearningRate = 0.1f;
    private static final int learningRateConstantForNumEpochs = 10;
    private static final float learningRateScalingFactor = 0.9f;

    private static final int batchSize = 50;

    private static final String trainingData = "src/main/resources/mnistTrainData.csv";
    private static final String trainingLabels = "src/main/resources/mnistTrainLabels.csv";
    private static final String validationData = "src/main/resources/mnistTestData.csv";
    private static final String validationLabels = "src/main/resources/mnistTestLabels.csv";

    private static final String trainingResults = "src/main/resources/MnistTrainingResults.csv";
    private static final String validationResults = "src/main/resources/MnistValidationResults.csv";



    public static void main(String[] args) throws InterruptedException {
        MachineLearningModel machineLearningModel = setUpMachineLearningModel();
        TaskBatchDistributor taskBatchDistributor = setUpTaskBatchDistributor(machineLearningModel);

        ChannelLoadManager loadManager = new ChannelLoadManager();
        TaskChannelMultiplexer<Task> channelMultiplexer = new TaskChannelMultiplexer<>();
        channelMultiplexer.setChannelLoadManager(loadManager);
        taskBatchDistributor.setOutputTaskChannel(channelMultiplexer.getInputChannel());
        ResultsHandler resultsHandler = new ResultsHandler();

        createWorkers(loadManager, resultsHandler, howManyWorkers);

        EpochCounter epochCounter = setUpEpochCounter(machineLearningModel, taskBatchDistributor, resultsHandler);

        epochCounter.startExecution();
        epochCounter.waitForShutdown();
    }

    private static MachineLearningModel setUpMachineLearningModel() {
        MachineLearningModel machineLearningModel = new MachineLearningModel();
        OperatorGradientPair innerProduct = new OperatorGradientPair(new InnerProductOperator(28*28, 10));
        //OperatorGradientPair reLu = new OperatorGradientPair(new ReLuOperator(200));
        //OperatorGradientPair innerProduct1 = new OperatorGradientPair(new InnerProductOperator(200, 10));
        OperatorGradientPair softmax = new OperatorGradientPair(new SoftmaxOperator(10));
        ErrorFunction errorFunction = new CrossEntropyError();
        machineLearningModel.addOperatorPair(0, innerProduct);
        //machineLearningModel.addOperatorPair(1, reLu);
        //machineLearningModel.addOperatorPair(2, innerProduct1);
        machineLearningModel.addOperatorPair(1, softmax);
        machineLearningModel.setErrorFunction(errorFunction);
        return machineLearningModel;
    }

    private static TaskBatchDistributor setUpTaskBatchDistributor(MachineLearningModel machineLearningModel) {
        TaskBatchDistributor taskBatchDistributor = new TaskBatchDistributor(batchSize);
        taskBatchDistributor.loadTrainingData(trainingData, trainingLabels);
        taskBatchDistributor.loadValidationData(validationData, validationLabels);
        taskBatchDistributor.setMachineLearningModel(machineLearningModel);
        return taskBatchDistributor;
    }

    private static void createWorkers(ChannelLoadManager loadManager, ResultsHandler resultsHandler, int numWorkers) {
        for (int i = 0; i < numWorkers; i++) {
            TaskProcessingWorker taskProcessingWorker = new TaskProcessingWorker();
            loadManager.addChannel(taskProcessingWorker.getInputTaskChannel());
            taskProcessingWorker.setOutputChannel(resultsHandler.getInputChannel());
        }
    }

    private static EpochCounter setUpEpochCounter(MachineLearningModel machineLearningModel, TaskBatchDistributor taskBatchDistributor, ResultsHandler resultsHandler) {
        EpochCounter epochCounter = new EpochCounter(numberEpochs);
        resultsHandler.setOutputChannel(epochCounter.getInputChannel());
        setUpLearningRateUpdater(epochCounter);
        setUpWeightUpdater(machineLearningModel, epochCounter);
        setUpLogger(epochCounter);
        epochCounter.setOutputChannel(taskBatchDistributor.getInputChannel());
        return epochCounter;
    }

    private static void setUpLearningRateUpdater(EpochCounter epochCounter) {
        LearningRateUpdater learningRateUpdater = new LearningRateScaleAfterFixedNumberEpochs(
                initialLearningRate,
                learningRateConstantForNumEpochs,
                learningRateScalingFactor
        );
        epochCounter.setLearningRateUpdater(learningRateUpdater);
    }

    private static void setUpWeightUpdater(MachineLearningModel machineLearningModel, EpochCounter epochCounter) {
        WeightUpdater weightUpdater = new WeightUpdaterWithMaxNormRegularisation(maxNormConstant);
        weightUpdater.setDimensionArray(machineLearningModel.getArrayOfWeightDimensions());
        weightUpdater.initialiseWeights();
        epochCounter.setWeightUpdater(weightUpdater);
    }

    private static void setUpLogger(EpochCounter epochCounter) {
        ResultLogger logger = new ResultFileLogger(trainingResults,validationResults);
        epochCounter.setLogger(logger);
    }
}
