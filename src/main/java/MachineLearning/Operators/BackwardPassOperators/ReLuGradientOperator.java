package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Weights.NoWeights;
import MachineLearning.Operators.OperatorResult;

public class ReLuGradientOperator extends GradientOperator {

    public ReLuGradientOperator(int inputSize) {
        super(inputSize, inputSize);
    }

    @Override
    protected ErrorAndWeightGradientPair calculateErrorsAndGradientAssumingCorrectSize(
            OperatorResult errorsFromNextLayer,
            OperatorResult activationsFromPreviousLayer
    ) {

        return new ErrorAndWeightGradientPair(
                zeroErrorIfActivationNegative(errorsFromNextLayer, activationsFromPreviousLayer),
                NoWeights.getWeightsWithNoWeights()
        );
    }

    private OperatorResult zeroErrorIfActivationNegative(OperatorResult errors, OperatorResult activations) {
        OperatorResult result = new OperatorResult(inputSize);
        for (int i = 0; i < inputSize; i++) {
            if (activations.get(i) < 0) {
                result.add(i, 0f);
            } else {
                result.add(i, errors.get(i));
            }
        }
        return result;
    }
}
