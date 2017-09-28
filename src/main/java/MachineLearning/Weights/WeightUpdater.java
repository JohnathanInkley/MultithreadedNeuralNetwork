package MachineLearning.Weights;

import java.util.ArrayList;

public abstract class WeightUpdater {

    protected WeightsArray weightsArray;

    public abstract void setDimensionArray(ArrayList<WeightDimensions> dimensionsArrayList);

    public abstract void initialiseWeights();

    public abstract void updateWeightsWithGradients(ArrayList<WeightsArray> gradientsFromEachTask, float learningRate);

    public WeightsArray getWeightsArray() {
        return weightsArray;
    }


}
