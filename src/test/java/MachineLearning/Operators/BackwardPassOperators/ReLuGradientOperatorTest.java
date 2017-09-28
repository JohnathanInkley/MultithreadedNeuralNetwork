package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.OperatorResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class ReLuGradientOperatorTest {

    private ReLuGradientOperator underTest;
    private OperatorResult backPassedErrors;
    private OperatorResult activations;

    @Before
    public void setUp() {
        underTest = new ReLuGradientOperator(2);

        backPassedErrors = new OperatorResult(2);
        backPassedErrors.add(1f);
        backPassedErrors.add(2f);

        activations = new OperatorResult(2);
        activations.add(0.5f);
        activations.add(-0.5f);
    }

    @Test
    public void shouldPassBackErrorIfActivationPositive() {
        assertEquals(1f, underTest.calculateErrorsAndGradient(backPassedErrors, activations).getErrors().get(0));
    }

    @Test
    public void shouldPassBackZeroIfActivationNegative() {
        assertEquals(0f, underTest.calculateErrorsAndGradient(backPassedErrors, activations).getErrors().get(1));
    }

    @Test
    public void shouldThrowExceptionIfInputActivationsIncorrectlySized() {
        OperatorResult badActivations = new OperatorResult(1);
        badActivations.add(0.5f);

        try {
            underTest.calculateErrorsAndGradient(backPassedErrors, badActivations);
            fail();
        } catch (Exception e) {
            assertEquals(ReLuGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfInputErrorsIncorrectlySized() {
        OperatorResult badErrors = new OperatorResult(1);
        badErrors.add(0f);

        try {
            underTest.calculateErrorsAndGradient(badErrors, activations);
            fail();
        } catch (Exception e) {
            assertEquals(ReLuGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

}