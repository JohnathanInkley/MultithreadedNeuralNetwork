package MachineLearning.ErrorFunction;

import MachineLearning.ClassificationTarget;
import MachineLearning.Operators.OperatorResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

public class CrossEntropyErrorTest {

    private CrossEntropyError underTest;
    private OperatorResult predictedProbabilities;
    private ClassificationTarget goodTarget;

    @Before
    public void setUp() {
        predictedProbabilities = new OperatorResult(3);
        predictedProbabilities.add(0, 0.2f);
        predictedProbabilities.add(1, 0.3f);
        predictedProbabilities.add(2, 0.5f);

        goodTarget = new ClassificationTarget(1);

        underTest = new CrossEntropyError();
    }

    @Test
    public void shouldCalculateErrorCorrectly() {
        assertEquals(1.2039728f, underTest.calculateError(predictedProbabilities, goodTarget));
    }

    @Test
    public void shouldThrowExceptionForErrorsIfTargetBiggerThanArray() {
        ClassificationTarget badTarget = new ClassificationTarget(3);
        try {
            underTest.calculateError(predictedProbabilities, badTarget);
            fail();
        } catch (Exception e) {
            assertEquals(CrossEntropyError.TARGET_NOT_IN_RANGE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionForErrorsIfInputIsZero() {
        OperatorResult badProbabilities = new OperatorResult(2);
        badProbabilities.add(0f);
        badProbabilities.add(0.3f);

        try {
            underTest.calculateError(badProbabilities, goodTarget);
            fail();
        } catch (Exception e) {
            assertEquals(CrossEntropyError.INPUT_IS_ZERO_MESSAGE + badProbabilities, e.getMessage());
        }
    }

    @Test
    public void shouldCalculateGradientToPassBack() {
        assertEquals(0f, underTest.calculateErrorGradient(predictedProbabilities, goodTarget).get(0));
        assertEquals(-1/0.3f, underTest.calculateErrorGradient(predictedProbabilities, goodTarget).get(1));
        assertEquals(0f, underTest.calculateErrorGradient(predictedProbabilities, goodTarget).get(2));
    }

    @Test
    public void shouldThrowExceptionForGradientIfTargetBiggerThanArray() {
        ClassificationTarget badTarget = new ClassificationTarget(3);
        try {
            underTest.calculateErrorGradient(predictedProbabilities, badTarget);
            fail();
        } catch (Exception e) {
            assertEquals(CrossEntropyError.TARGET_NOT_IN_RANGE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionForGradientIfInputIsZero() {
        OperatorResult badProbabilities = new OperatorResult(2);
        badProbabilities.add(0f);
        badProbabilities.add(0.3f);

        try {
            underTest.calculateErrorGradient(badProbabilities, goodTarget);
            fail();
        } catch (Exception e) {
            assertEquals(CrossEntropyError.INPUT_IS_ZERO_MESSAGE + badProbabilities, e.getMessage());
        }
    }

    @Test
    public void shouldCalculateWhetherTargetIsMaxProbability() {
        assertFalse(underTest.classifiedCorrectly(predictedProbabilities, new ClassificationTarget(0)));
        assertFalse(underTest.classifiedCorrectly(predictedProbabilities, new ClassificationTarget(1)));
        assertTrue(underTest.classifiedCorrectly(predictedProbabilities, new ClassificationTarget(2)));
    }

}