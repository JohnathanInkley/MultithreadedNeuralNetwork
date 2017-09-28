package MachineLearning.LearningRateUpdating;

public class LearningRateScaleAfterFixedNumberEpochs extends LearningRateUpdater {

    public static final String NEGATIVE_NUMBER_OF_CONSTANT_EPOCHS = "Learning rate should be constant for a positive number of epochs";
    public static final String LEARNING_RATE_SCALING_OUT_OF_BOUNDS_MESSAGE = "Learning rate scaling should be between 0 and 1";

    private final int epochsConstantFor;
    private final float multiplyOldLearningRateBy;
    private int epochCount;

    public LearningRateScaleAfterFixedNumberEpochs(float learningRate, int epochsConstantFor, float multiplyOldLearningRateBy) {
        super(learningRate);
        validateInputs(epochsConstantFor, multiplyOldLearningRateBy);
        this.epochsConstantFor = epochsConstantFor;
        this.multiplyOldLearningRateBy = multiplyOldLearningRateBy;
        epochCount = 1;
    }

    private void validateInputs(int epochsConstantFor, float multiplyOldLearningRateBy) {
        if (epochsConstantFor < 0) {
            throw new RuntimeException(NEGATIVE_NUMBER_OF_CONSTANT_EPOCHS);
        } else if (multiplyOldLearningRateBy > 1 || multiplyOldLearningRateBy <= 0) {
            throw new RuntimeException(LEARNING_RATE_SCALING_OUT_OF_BOUNDS_MESSAGE);
        }
    }

    @Override
    public void updateLearningRate() {
        epochCount++;
        if (epochCount > epochsConstantFor) {
            learningRate *= multiplyOldLearningRateBy;
        }
    }
}
