package Application;

import Infrastructure.*;
import Infrastructure.TaskCreation.Task;
import MachineLearning.ErrorFunction.ErrorFunction;
import MachineLearning.LearningRateUpdating.LearningRateUpdater;
import MachineLearning.MachineLearningModel;
import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.ForwardPassOperators.ReLuOperator;
import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Weights.WeightUpdater;

public class NeuralNetwork {
    private MachineLearningModel machineLearningModel = new MachineLearningModel();
    private int numberOfOperators = 0;

    private TaskBatchDistributor taskBatchDistributor;
    private ChannelLoadManager loadManager = new ChannelLoadManager();
    private TaskChannelMultiplexer<Task> channelMultiplexer = new TaskChannelMultiplexer<>();
    private ResultsHandler resultsHandler = new ResultsHandler();
    private EpochCounter epochCounter;

    public void addInnerProductOperatorWithReLu(int inputSize, int outputSize) {
        addInnerProduct(inputSize, outputSize);
        addReLu(outputSize);
    }

    private void addInnerProduct(int inputSize, int outputSize) {
        OperatorGradientPair innerProduct = new OperatorGradientPair(new InnerProductOperator(inputSize, outputSize));
        machineLearningModel.addOperatorPair(numberOfOperators, innerProduct);
        numberOfOperators++;
    }

    private void addReLu(int outputSize) {
        OperatorGradientPair reLu = new OperatorGradientPair(new ReLuOperator(outputSize));
        machineLearningModel.addOperatorPair(numberOfOperators, reLu);
        numberOfOperators++;
    }

    public void addInnerProductOperatorWithSoftmax(int inputSize, int outputSize) {
        addInnerProduct(inputSize, outputSize);
        addSoftmax(outputSize);
    }

    private void addSoftmax(int outputSize) {
        OperatorGradientPair softmax = new OperatorGradientPair(new SoftmaxOperator(outputSize));
        machineLearningModel.addOperatorPair(numberOfOperators, softmax);
        numberOfOperators++;
    }

    public void addErrorFunction(ErrorFunction errorFunction) {
        machineLearningModel.setErrorFunction(errorFunction);
    }

    public void setNumberOfTasksInABatch(int batchSize) {
        taskBatchDistributor = new TaskBatchDistributor(batchSize);
        taskBatchDistributor.setMachineLearningModel(machineLearningModel);
        channelMultiplexer.setChannelLoadManager(loadManager);
        taskBatchDistributor.setOutputTaskChannel(channelMultiplexer.getInputChannel());
    }

    public void setTrainingData(String trainingExamples, String trainingLabels) {
        taskBatchDistributor.loadTrainingData(trainingExamples, trainingLabels);
    }

    public void setValidationData(String validationExamples, String validationLabels) {
        taskBatchDistributor.loadValidationData(validationExamples, validationLabels);
    }

    public void setNumberOfWorkers(int numberOfWorkers) {
        for (int i = 0; i < numberOfWorkers; i++) {
            TaskProcessingWorker taskProcessingWorker = new TaskProcessingWorker();
            loadManager.addChannel(taskProcessingWorker.getInputTaskChannel());
            taskProcessingWorker.setOutputChannel(resultsHandler.getInputChannel());
        }
    }

    public void setNumberOfEpochs(int numberOfEpochs) {
        epochCounter = new EpochCounter(numberOfEpochs);
        resultsHandler.setOutputChannel(epochCounter.getInputChannel());
        epochCounter.setOutputChannel(taskBatchDistributor.getInputChannel());
    }

    public void setLearningRateUpdater(LearningRateUpdater learningRateUpdater) {
        epochCounter.setLearningRateUpdater(learningRateUpdater);
    }

    public void setWeightUpdater(WeightUpdater weightUpdater) {
        weightUpdater.setDimensionArray(machineLearningModel.getArrayOfWeightDimensions());
        weightUpdater.initialiseWeights();
        epochCounter.setWeightUpdater(weightUpdater);
    }

    public void setFilePathsToWriteResultsTo(String trainingResults, String validationResults) {
        epochCounter.setLogger(new ResultFileLogger(trainingResults,validationResults));
    }

    public void run() {
        try {
            epochCounter.startExecution();
            epochCounter.waitForShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
