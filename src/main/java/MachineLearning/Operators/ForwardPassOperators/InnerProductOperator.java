package MachineLearning.Operators.ForwardPassOperators;

import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.BackwardPassOperators.InnerProductGradientOperator;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;

public class InnerProductOperator extends ForwardPassOperator {

    public static final String WEIGHTS_NOT_SET_MESSAGE = "Weights not been set for InnerProductOperator";

    public InnerProductOperator(int inputSize, int outputSize) {
        super(inputSize, outputSize);
    }

    @Override
    public GradientOperator createGradientOperator() {
        return new InnerProductGradientOperator(outputSize, inputSize);
    }

    @Override
    protected OperatorResult computeResultAssumingCorrectlySized(OperatorResult input) {
        if (weights == null) {
            throw new RuntimeException(WEIGHTS_NOT_SET_MESSAGE);
        } else {
            return weights.multiply(input);
        }
    }

    @Override
    public WeightDimensions getDimensions() {
        return new WeightDimensions(outputSize, inputSize);
    }
}
