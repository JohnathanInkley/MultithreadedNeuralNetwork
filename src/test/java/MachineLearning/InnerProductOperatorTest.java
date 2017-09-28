package MachineLearning;

import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.Weights;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class InnerProductOperatorTest {

    @Test
    public void shouldTakeWeightsAndMultiplyInputCorrectly() {
        InnerProductOperator underTest = new InnerProductOperator(2,3);

        Weights weights = new Weights(3, 2);
        for (int i = 0; i < 6; i++) {
            weights.setValue(i % 3, i % 2, (float) i);
        }

        underTest.setWeights(weights);

        float[] inputsFloatArray = new float[] {1f, 2f};
        OperatorResult inputs = new OperatorResult(inputsFloatArray);
        OperatorResult result = underTest.compute(inputs);

        // [0 3, 4 1, 2 5] * [1 2]

        assertEquals(6f, result.get(0));
        assertEquals(6f, result.get(1));
        assertEquals(12f, result.get(2));
    }

    @Test
    public void shouldThrowExceptionIfInputIsWrongSize() {
        InnerProductOperator underTest = new InnerProductOperator(2,3);

        Weights weights = new Weights(3, 2);
        for (int i = 0; i < 6; i++) {
            weights.setValue(i % 3, i % 2, (float) i);
        }
        underTest.setWeights(weights);

        float[] inputsArray = new float[]{1f, 1f, 1f};
        OperatorResult inputs = new OperatorResult(inputsArray);

        try {
            underTest.compute(inputs);
            fail();
        } catch (Exception e) {
            assertEquals(InnerProductOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfNoWeightsSet() {
        InnerProductOperator underTest = new InnerProductOperator(2,3);

        float[] inputsArray = new float[]{1f, 1f};
        OperatorResult inputs = new OperatorResult(inputsArray);

        try {
            underTest.compute(inputs);
            fail();
        } catch (Exception e) {
            assertEquals(InnerProductOperator.WEIGHTS_NOT_SET_MESSAGE, e.getMessage());
        }
    }

}