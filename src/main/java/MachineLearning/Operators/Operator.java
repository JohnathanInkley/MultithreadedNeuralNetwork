package MachineLearning.Operators;

import MachineLearning.Weights.Weights;

public abstract class Operator {

    public static final String INCORRECT_INPUT_SIZE_MESSAGE = "Input size does not match size passed in constructor";

    protected final int inputSize;
    protected final int outputSize;

    protected Weights weights;

    protected Operator(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }

    protected void checkInputsCorrectlySized(OperatorResult input) {
        if (input.size() != inputSize) {
            throw new RuntimeException(INCORRECT_INPUT_SIZE_MESSAGE);
        }
    }

    public void setWeights(Weights weights) {
        this.weights = weights;
    }

}
