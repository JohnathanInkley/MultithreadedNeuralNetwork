package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.Weights;

public class ErrorAndWeightGradientPair {

    private OperatorResult errors;
    private Weights weightGradients;

    public ErrorAndWeightGradientPair(OperatorResult errors, Weights weightGradients) {
        this.errors = errors;
        this.weightGradients = weightGradients;
    }

    public OperatorResult getErrors() {
        return errors;
    }

    public Weights getWeightGradients() {
        return weightGradients;
    }
}
