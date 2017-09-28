package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.OperatorResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class SoftmaxGradientOperatorTest {

    private SoftmaxGradientOperator underTest;
    private OperatorResult previousErrors;
    private OperatorResult activations;

    @Before
    public void setUp() {
        previousErrors = new OperatorResult(2);
        previousErrors.add(2f);
        previousErrors.add(3f);

        activations = new OperatorResult(2);
        activations.add(4f);
        activations.add(5f);

        underTest = new SoftmaxGradientOperator(2);
    }

    @Test
    public void shouldCalculateErrorCorrectly() {
        OperatorResult expectedErrors = new OperatorResult(2);
        expectedErrors.add(0,-0.19661193f);
        expectedErrors.add(1, 0.19661193f);

        assertEquals(expectedErrors, underTest.calculateErrorsAndGradient(previousErrors, activations).getErrors());
    }

    @Test
    public void shouldThrowExceptionIfInputErrorsIncorrectlySized() {
        OperatorResult badErrors = new OperatorResult(1);
        badErrors.add(0.1f);

        try {
            underTest.calculateErrorsAndGradient(badErrors, activations);
            fail();
        } catch (Exception e) {
            assertEquals(SoftmaxGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfInputActivationsIncorrectlySized() {
        OperatorResult badActivations = new OperatorResult(1);
        badActivations.add(0.1f);

        try {
            underTest.calculateErrorsAndGradient(previousErrors, badActivations);
            fail();
        } catch (Exception e) {
            assertEquals(SoftmaxGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

}