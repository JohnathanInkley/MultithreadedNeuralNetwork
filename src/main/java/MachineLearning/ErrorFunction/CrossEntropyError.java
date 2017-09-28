package MachineLearning.ErrorFunction;

import MachineLearning.ClassificationTarget;
import MachineLearning.Operators.OperatorResult;

public class CrossEntropyError implements ErrorFunction {

    public static final String TARGET_NOT_IN_RANGE_MESSAGE = "Target as array position exceeds input dimensions";
    public static final String INPUT_IS_ZERO_MESSAGE = "One of inputs is zero in input: ";

    @Override
    public Float calculateError(OperatorResult inputs, ClassificationTarget target) {
        checkInputsValid(inputs, target);
        return - (float) Math.log(inputs.get(target.getTargetAsPositionInArray()));
    }

    private void checkInputsValid(OperatorResult inputs, ClassificationTarget target) {
        if (target.getTargetAsPositionInArray() >= inputs.size()) {
            throw new RuntimeException(TARGET_NOT_IN_RANGE_MESSAGE);
        } else if (oneOfInputsIsZero(inputs)) {
            throw new RuntimeException(INPUT_IS_ZERO_MESSAGE + inputs);
        }
    }

    private boolean oneOfInputsIsZero(OperatorResult inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            if (inputs.get(i) <= 0f) {
                return true;
            }
        }
        return false;
    }


    @Override
    public OperatorResult calculateErrorGradient(OperatorResult inputs, ClassificationTarget target) {
        checkInputsValid(inputs, target);
        return getGradientAssumingInputsValid(inputs, target);
    }

    private OperatorResult getGradientAssumingInputsValid(OperatorResult inputs, ClassificationTarget target) {
        OperatorResult result = new OperatorResult(inputs.size());
        for (int i = 0 ; i < inputs.size(); i++) {
            if (target.getTargetAsPositionInArray() == i) {
                result.add(i, -1/inputs.get(i));
            } else {
                result.add(i, 0f);
            }
        }
        return result;
    }

    @Override
    public boolean classifiedCorrectly(OperatorResult inputs, ClassificationTarget target) {
        for (int i = 0; i < inputs.size(); i++) {
            if (indexDifferentToTarget(target, i) && probabilityOfTargetNotLargerThanProbabilityOfIndex(inputs, target, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexDifferentToTarget(ClassificationTarget target, int i) {
        return i != target.getTargetAsPositionInArray();
    }

    private boolean probabilityOfTargetNotLargerThanProbabilityOfIndex(OperatorResult inputs, ClassificationTarget target, int index) {
        return inputs.get(index) >= inputs.get(target.getTargetAsPositionInArray());
    }

}
