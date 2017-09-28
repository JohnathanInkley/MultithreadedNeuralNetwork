package Infrastructure;

import MachineLearning.Weights.WeightsArray;

public class UpdateMessage {

    private final WeightsArray weightsToUseForNewBatch;

    public UpdateMessage(WeightsArray weightsToUseForNewBatch) {
        this.weightsToUseForNewBatch = weightsToUseForNewBatch;
    }

    public WeightsArray getWeightsToUseForNewBatch() {
        return weightsToUseForNewBatch;
    }
}
