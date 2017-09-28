package MachineLearning;

import MachineLearning.Weights.WeightsArray;

import java.util.ArrayList;

public class EndOfBatchMessage {

    private final boolean endOfEpoch;
    private final ArrayList<ExampleProcessingResult> arrayOfResults;

    public EndOfBatchMessage(boolean endOfEpoch, ArrayList<ExampleProcessingResult> arrayOfResults) {
        this.endOfEpoch = endOfEpoch;
        this.arrayOfResults = arrayOfResults;
    }

    public boolean isEndOfEpoch() {
        return endOfEpoch;
    }

    public ArrayList<WeightsArray> getGradients() {
        ArrayList<WeightsArray> results = new ArrayList<>();
        for (ExampleProcessingResult result : arrayOfResults) {
            results.add(result.getGradientArray());
        }
        return results;
    }

    public ArrayList<Boolean> getClassifications() {
        ArrayList<Boolean> results = new ArrayList<>();
        for (ExampleProcessingResult result : arrayOfResults) {
            results.add(result.exampleWasClassifiedCorrectly());
        }
        return results;
    }
}
