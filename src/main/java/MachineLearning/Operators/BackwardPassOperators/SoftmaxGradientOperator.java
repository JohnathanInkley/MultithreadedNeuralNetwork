package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Weights.NoWeights;
import MachineLearning.Operators.OperatorResult;

public class SoftmaxGradientOperator extends GradientOperator {

    private final SoftmaxOperator softmaxOperator;

    public SoftmaxGradientOperator(int inputSize) {
        super(inputSize, inputSize);
        softmaxOperator = new SoftmaxOperator(inputSize);
    }

    @Override
    protected ErrorAndWeightGradientPair calculateErrorsAndGradientAssumingCorrectSize(
            OperatorResult errorsFromNextLayer,
            OperatorResult activationsFromPreviousLayer
    ) {
        OperatorResult softmaxOfActivations = softmaxOperator.compute(activationsFromPreviousLayer);
        float sum = sumOfActivationsTimesPassedBackErrors(softmaxOfActivations, errorsFromNextLayer);
        OperatorResult errorsToPassBack = new OperatorResult(inputSize);
        for (int i = 0; i < inputSize; i++) {
            errorsToPassBack.add(i, softmaxOfActivations.get(i)*(errorsFromNextLayer.get(i) - sum));
        }
        return new ErrorAndWeightGradientPair(errorsToPassBack, NoWeights.getWeightsWithNoWeights());
    }

    private float sumOfActivationsTimesPassedBackErrors(
            OperatorResult softmaxOfActivations,
            OperatorResult errorsFromNextLayer
    ) {
        float result = 0f;
        for (int i = 0; i < inputSize; i++) {
            result += softmaxOfActivations.get(i) * errorsFromNextLayer.get(i);
        }
        return result;
    }
}
