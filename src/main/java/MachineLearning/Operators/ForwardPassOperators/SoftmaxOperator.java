package MachineLearning.Operators.ForwardPassOperators;

import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.BackwardPassOperators.SoftmaxGradientOperator;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;

public class SoftmaxOperator extends ForwardPassOperator {

    public SoftmaxOperator(int inputSize) {
        super(inputSize, inputSize);
    }

    @Override
    public GradientOperator createGradientOperator() {
        return new SoftmaxGradientOperator(inputSize);
    }

    @Override
    protected OperatorResult computeResultAssumingCorrectlySized(OperatorResult input) {
        Float sum = getSumOfExponentials(input);
        return divideExponentialOfInputsBySum(input, sum);
    }

    @Override
    public WeightDimensions getDimensions() {
        return new WeightDimensions(0);
    }

    private Float getSumOfExponentials(OperatorResult input) {
        Float sum = 0f;
        for (int i = 0; i < inputSize; i++) {
            sum += (float) Math.exp(input.get(i));
        }
        return sum;
    }

    private OperatorResult divideExponentialOfInputsBySum(OperatorResult input, Float sum) {
        OperatorResult result = new OperatorResult(inputSize);
        for (int i = 0; i < inputSize; i++) {
            result.add((float) Math.exp(input.get(i))/sum);
        }
        return result;
    }
}
