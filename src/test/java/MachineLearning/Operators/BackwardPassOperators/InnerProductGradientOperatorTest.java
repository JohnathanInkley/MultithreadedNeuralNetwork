package MachineLearning.Operators.BackwardPassOperators;

import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.Weights;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class InnerProductGradientOperatorTest {

    private InnerProductGradientOperator underTest;
    private OperatorResult passedBackErrors;
    private OperatorResult inputActivations;

    @Before
    public void setUp() {
        underTest = new InnerProductGradientOperator(3,2);

        passedBackErrors = new OperatorResult(3);
        passedBackErrors.add(1f);
        passedBackErrors.add(2f);
        passedBackErrors.add(3f);

        inputActivations = new OperatorResult(2);
        inputActivations.add(4f);
        inputActivations.add(5f);

        Weights weights = new Weights(3, 2);
        for (int i = 0; i < 6; i++) {
            weights.setValue(i % 3, i % 2, (float) i);
        }
        underTest.setWeights(weights);
    }


    @Test
    public void shouldCalculateGradientsCorrectly() {
        Weights exepctedGradients = new Weights(3, 2);
        exepctedGradients.setValue(0, 0, 4f);
        exepctedGradients.setValue(0, 1, 5f);
        exepctedGradients.setValue(1, 0, 8f);
        exepctedGradients.setValue(1, 1, 10f);
        exepctedGradients.setValue(2, 0, 12f);
        exepctedGradients.setValue(2, 1, 15f);

        assertEquals(exepctedGradients, underTest.calculateErrorsAndGradient(passedBackErrors, inputActivations).getWeightGradients());
    }

    @Test
    public void shouldCalculateErrorsCorrectly() {
        OperatorResult expectedErrors = new OperatorResult(2);
        expectedErrors.add(1*0f + 2*4f + 3*2f);
        expectedErrors.add(1*3f + 2*1f + 3*5f);

        assertEquals(expectedErrors, underTest.calculateErrorsAndGradient(passedBackErrors, inputActivations).getErrors());
    }

    @Test
    public void shouldThrowExceptionIfPassedBackErrorsIncorrectlySized() {
        OperatorResult badErrors = new OperatorResult(1);
        badErrors.add(0f);

        try {
            underTest.calculateErrorsAndGradient(badErrors, inputActivations);
            fail();
        } catch (Exception e) {
            assertEquals(InnerProductGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfInputActivationsIncorrectlySized() {
        OperatorResult badActivations = new OperatorResult(1);
        badActivations.add(0f);

        try {
            underTest.calculateErrorsAndGradient(passedBackErrors, badActivations);
            fail();
        } catch (Exception e) {
            assertEquals(InnerProductGradientOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

}