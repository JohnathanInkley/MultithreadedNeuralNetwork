package MachineLearning;

import MachineLearning.LearningRateUpdating.LearningRateScaleAfterFixedNumberEpochs;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class LearningRateScaleAfterFixedNumberEpochsTest {

    @Test
    public void learningRateShouldBeConstantForSpecifiedNumberOfUpdates() {
        LearningRateScaleAfterFixedNumberEpochs underTest = new LearningRateScaleAfterFixedNumberEpochs(0.1f, 3, 0.9f);
        assertEquals(0.1f, underTest.getLearningRate());
        underTest.updateLearningRate();
        assertEquals(0.1f, underTest.getLearningRate());
        underTest.updateLearningRate();
        assertEquals(0.1f, underTest.getLearningRate());
    }

    @Test
    public void shouldMultipleLearningRateByFactorAfterSpecifiedNumberOfUpdates() {
        LearningRateScaleAfterFixedNumberEpochs underTest = new LearningRateScaleAfterFixedNumberEpochs(0.1f, 3, 0.9f);
        underTest.updateLearningRate();
        underTest.updateLearningRate();
        underTest.updateLearningRate();
        assertEquals(0.09f, underTest.getLearningRate(), 0.0000001);
    }

    @Test
    public void shouldThrowExceptionIfInitialLearningRateIsNegative() {
        try {
            new LearningRateScaleAfterFixedNumberEpochs(-0.1f, 1, 0.1f);
            fail();
        } catch (Exception e) {
            assertEquals(LearningRateScaleAfterFixedNumberEpochs.NEGATIVE_LEARNING_RATE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfEpochsConstantForNegative() {
        try {
            new LearningRateScaleAfterFixedNumberEpochs(0.1f, -1, 0.1f);
            fail();
        } catch (Exception e) {
            assertEquals(LearningRateScaleAfterFixedNumberEpochs.NEGATIVE_NUMBER_OF_CONSTANT_EPOCHS, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfLearningRateScalingGreaterThanOne() {
        try {
            new LearningRateScaleAfterFixedNumberEpochs(0.1f, 1, 1.1f);
            fail();
        } catch (Exception e) {
            assertEquals(LearningRateScaleAfterFixedNumberEpochs.LEARNING_RATE_SCALING_OUT_OF_BOUNDS_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfLearningRateScalingNegative() {
        try {
            new LearningRateScaleAfterFixedNumberEpochs(0.1f, 1, -0.1f);
            fail();
        } catch (Exception e) {
            assertEquals(LearningRateScaleAfterFixedNumberEpochs.LEARNING_RATE_SCALING_OUT_OF_BOUNDS_MESSAGE, e.getMessage());
        }
    }
}