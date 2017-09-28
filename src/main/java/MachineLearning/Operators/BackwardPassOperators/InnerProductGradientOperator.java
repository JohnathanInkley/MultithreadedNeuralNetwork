package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.Weights;

public class InnerProductGradientOperator extends GradientOperator {

    public InnerProductGradientOperator(int inputSize, int outputSize) {
        super(inputSize, outputSize);
    }

    @Override
    protected ErrorAndWeightGradientPair calculateErrorsAndGradientAssumingCorrectSize(
            OperatorResult errorsFromNextLayer,
            OperatorResult activationsFromPreviousLayer
    ) {
        return new ErrorAndWeightGradientPair(
                getErrorsToPassBack(errorsFromNextLayer),
                getGradients(errorsFromNextLayer, activationsFromPreviousLayer)
        );
    }

    private OperatorResult getErrorsToPassBack(OperatorResult errorsFromNextLayer) {
        return weights.transposeThenMultiply(errorsFromNextLayer);
    }

    private Weights getGradients(OperatorResult errorsFromNextLayer, OperatorResult inputsFromPreviousLayer) {
        return errorsFromNextLayer.multiplyToGetMatrix(inputsFromPreviousLayer);
    }

}
