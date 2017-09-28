package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.Operator;
import MachineLearning.Operators.OperatorResult;

public abstract class GradientOperator extends Operator {

    protected GradientOperator(int inputSize, int outputSize) {
        super(inputSize, outputSize);
    }

    public ErrorAndWeightGradientPair calculateErrorsAndGradient(
            OperatorResult errorsFromNextLayer,
            OperatorResult activationsFromPreviousLayer
    ) {
        checkInputsCorrectlySized(errorsFromNextLayer);
        checkActivationsCorrectlySized(activationsFromPreviousLayer);
        return calculateErrorsAndGradientAssumingCorrectSize(errorsFromNextLayer, activationsFromPreviousLayer);
    }

    private void checkActivationsCorrectlySized(OperatorResult activationsFromPreviousLayer) {
        if (activationsFromPreviousLayer.size() != outputSize) {
            throw new RuntimeException(INCORRECT_INPUT_SIZE_MESSAGE);
        }
    }

    protected abstract ErrorAndWeightGradientPair calculateErrorsAndGradientAssumingCorrectSize(
            OperatorResult errorsFromNextLayer,
            OperatorResult activationsFromPreviousLayer
    );

}
