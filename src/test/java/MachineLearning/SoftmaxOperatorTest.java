package MachineLearning;

import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorResult;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class SoftmaxOperatorTest {

    @Test
    public void shouldTakeInputsAndProduceSoftmaxOfOutputs() {
        OperatorResult input = new OperatorResult(2);
        input.add(1f);
        input.add(2f);

        SoftmaxOperator underTest = new SoftmaxOperator(2);

        assertEquals(0.2689414f, underTest.compute(input).get(0));
        assertEquals(0.7310586f, underTest.compute(input).get(1));
    }

    @Test
    public void shouldThrowExceptionIfIncorrectNumberOfInputs() {
        OperatorResult input = new OperatorResult(1);
        input.add(1f);
        SoftmaxOperator underTest = new SoftmaxOperator(2);

        try {
            underTest.compute(input);
            fail();
        } catch (Exception e) {
            assertEquals(SoftmaxOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }
    }

}