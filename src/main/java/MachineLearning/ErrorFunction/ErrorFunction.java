package MachineLearning.ErrorFunction;

import MachineLearning.ClassificationTarget;
import MachineLearning.Operators.OperatorResult;

public interface ErrorFunction {

    Float calculateError(OperatorResult inputs, ClassificationTarget target);

    boolean classifiedCorrectly(OperatorResult inputs, ClassificationTarget target);

    OperatorResult calculateErrorGradient(OperatorResult predictedProbabilities, ClassificationTarget target);
}
