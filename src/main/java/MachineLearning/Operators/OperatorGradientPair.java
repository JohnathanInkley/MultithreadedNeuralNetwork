package MachineLearning.Operators;

import MachineLearning.Operators.BackwardPassOperators.ErrorAndWeightGradientPair;
import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.ForwardPassOperators.ForwardPassOperator;
import MachineLearning.Weights.WeightDimensions;
import MachineLearning.Weights.Weights;

public class OperatorGradientPair {
    private final ForwardPassOperator forwardPassOperator;
    private final GradientOperator gradientOperator;

    public OperatorGradientPair(ForwardPassOperator forwardPassOperator) {
        this.forwardPassOperator = forwardPassOperator;
        this.gradientOperator = forwardPassOperator.createGradientOperator();
    }

    public OperatorResult passDataThrough(OperatorResult data) {
        return forwardPassOperator.compute(data);
    }

    public void setWeights(Weights weights) {
        forwardPassOperator.setWeights(weights);
        gradientOperator.setWeights(weights);
    }

    public ErrorAndWeightGradientPair calculateErrorsAndGradients(OperatorResult errorsFromNextLayer, OperatorResult activations) {
        return gradientOperator.calculateErrorsAndGradient(errorsFromNextLayer, activations);
    }

    public WeightDimensions getDimensions() {
        return forwardPassOperator.getDimensions();
    }
}
