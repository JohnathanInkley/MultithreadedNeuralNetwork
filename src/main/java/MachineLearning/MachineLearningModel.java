package MachineLearning;

import MachineLearning.Operators.BackwardPassOperators.ErrorAndWeightGradientPair;
import MachineLearning.ErrorFunction.ErrorFunction;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;
import MachineLearning.Weights.WeightsArray;

import java.util.ArrayList;

public class MachineLearningModel {

    private final ArrayList<OperatorGradientPair> operatorGradientPairs;
    private ErrorFunction errorFunction;
    private WeightsArray weightsArray;

    public MachineLearningModel() {
        operatorGradientPairs = new ArrayList<>();
    }

    public void addOperatorPair(int positionInArray, OperatorGradientPair operatorGradientPair) {
        operatorGradientPairs.add(positionInArray, operatorGradientPair);
    }

    public void setErrorFunction(ErrorFunction errorFunction) {
        this.errorFunction = errorFunction;
    }

    public void setWeightsArray(WeightsArray weightsArray) {
        this.weightsArray = weightsArray;
        for (int i = 0; i < operatorGradientPairs.size(); i++) {
            operatorGradientPairs.get(i).setWeights(weightsArray.get(i));
        }
    }

    public ExampleProcessingResult processExample(OperatorResult example, ClassificationTarget target) {
        ArrayList<OperatorResult> activations = passExampleThroughOperators(example);
        return new ExampleProcessingResult(
                isClassifiedCorrectly(target, activations),
                calculateGradientArray(target, activations)
        );
    }

    public ArrayList<OperatorResult> passExampleThroughOperators(OperatorResult example) {
        ArrayList<OperatorResult> result = new ArrayList<>();
        result.add(0, example);
        for (int i = 0; i < operatorGradientPairs.size(); i++) {
            result.add(i + 1, operatorGradientPairs.get(i).passDataThrough(result.get(i)));
        }
        return result;
    }

    public boolean isClassifiedCorrectly(ClassificationTarget target, ArrayList<OperatorResult> activations) {
        return errorFunction.classifiedCorrectly(activations.get(operatorGradientPairs.size()), target);
    }

    public WeightsArray calculateGradientArray(ClassificationTarget target, ArrayList<OperatorResult> activations) {
        OperatorResult errors = errorFunction.calculateErrorGradient(activations.get(operatorGradientPairs.size()), target);
        WeightsArray gradientArray = new WeightsArray(weightsArray.size());
        for (int i = operatorGradientPairs.size() - 1; i >= 0; i--) {
            ErrorAndWeightGradientPair errorsAndGradients =
                    operatorGradientPairs.get(i).calculateErrorsAndGradients(errors, activations.get(i));
            errors = errorsAndGradients.getErrors();
            gradientArray.set(i, errorsAndGradients.getWeightGradients());
        }
        return gradientArray;
    }


    public ArrayList<WeightDimensions> getArrayOfWeightDimensions() {
        ArrayList<WeightDimensions> result = new ArrayList<>();
        for (OperatorGradientPair pair : operatorGradientPairs) {
            result.add(pair.getDimensions());
        }
        return result;
    }
}
