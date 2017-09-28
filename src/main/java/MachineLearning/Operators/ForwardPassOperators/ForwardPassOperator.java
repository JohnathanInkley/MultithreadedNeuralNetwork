package MachineLearning.Operators.ForwardPassOperators;

import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.Operator;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;

public abstract class ForwardPassOperator extends Operator {

    protected ForwardPassOperator(int inputSize, int outputSize) {
        super(inputSize, outputSize);
    }

    public abstract GradientOperator createGradientOperator();

    public OperatorResult compute(OperatorResult input) {
        checkInputsCorrectlySized(input);
        return computeResultAssumingCorrectlySized(input);
    }

    protected abstract OperatorResult computeResultAssumingCorrectlySized(OperatorResult input);

    public abstract WeightDimensions getDimensions();
}
