package MachineLearning.Operators.ForwardPassOperators;

import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.BackwardPassOperators.ReLuGradientOperator;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;

public class ReLuOperator extends ForwardPassOperator {


    public ReLuOperator(int inputSize) {
        super(inputSize, inputSize);
    }

    @Override
    public GradientOperator createGradientOperator() {
        return new ReLuGradientOperator(inputSize);
    }

    @Override
    protected OperatorResult computeResultAssumingCorrectlySized(OperatorResult input) {
        if (input.size() != inputSize) {
            throw new RuntimeException();
        } else {
            return makeAllElementsOfArrayZeroIfNegative(input);
        }
    }

    @Override
    public WeightDimensions getDimensions() {
        return new WeightDimensions(0);
    }

    private OperatorResult makeAllElementsOfArrayZeroIfNegative(OperatorResult input) {
        OperatorResult result = new OperatorResult(inputSize);
        for (int i = 0; i < inputSize; i++) {
            result.add(i, makeZeroIfNegative(input.get(i)));
        }
        return result;
    }

    private float makeZeroIfNegative(float aFloat) {
        return Math.max(0, aFloat);
    }
}
