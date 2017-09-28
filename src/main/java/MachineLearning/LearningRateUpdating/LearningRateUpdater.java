package MachineLearning.LearningRateUpdating;

public abstract class LearningRateUpdater {

    public static final String NEGATIVE_LEARNING_RATE_MESSAGE = "Initial learning rate must be positive";

    protected float learningRate;

    protected LearningRateUpdater(float learningRate) {
        if (learningRate <= 0) {
            throw new RuntimeException(NEGATIVE_LEARNING_RATE_MESSAGE);
        }
        this.learningRate = learningRate;
    }

    public abstract void updateLearningRate();

    public float getLearningRate() {
        return learningRate;
    }
}
