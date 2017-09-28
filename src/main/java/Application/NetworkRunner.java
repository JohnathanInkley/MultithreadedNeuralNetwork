package Application;

import MachineLearning.ErrorFunction.CrossEntropyError;
import MachineLearning.LearningRateUpdating.LearningRateScaleAfterFixedNumberEpochs;
import MachineLearning.Weights.WeightUpdaterWithMaxNormRegularisation;

public class NetworkRunner {
    public static void main(String[] args) {
        NeuralNetwork network = new NeuralNetwork();

        network.addInnerProductOperatorWithSoftmax(28*28, 10);
        network.addErrorFunction(new CrossEntropyError());

        network.setNumberOfTasksInABatch(100);
        network.setTrainingData(
                "src/main/resources/mnistTrainData.csv",
                "src/main/resources/mnistTrainLabels.csv"
        );
        network.setValidationData(
                "src/main/resources/mnistTestData.csv",
                "src/main/resources/mnistTestLabels.csv"
        );

        network.setNumberOfWorkers(4);

        network.setNumberOfEpochs(20);
        network.setWeightUpdater(new WeightUpdaterWithMaxNormRegularisation(6.5));
        network.setLearningRateUpdater(new LearningRateScaleAfterFixedNumberEpochs(0.1f, 10, 0.9f));
        network.setFilePathsToWriteResultsTo(
                "src/main/mnistTrainingResults.csv",
                "src/main/mnistValidationResults.csv"
        );

        network.run();
    }
}
