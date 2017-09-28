package MachineLearning.Weights;

public class NoWeights extends Weights {

    private static final NoWeights example = new NoWeights();

    public static NoWeights getWeightsWithNoWeights() {
        return example;
    }

    private NoWeights() {
        super(0, 0);
    }

    @Override
    public void addOtherWeightScaled(Weights weights, float scale) {
        // Do nothing
    }

}
