package MachineLearning;

import MachineLearning.Operators.ForwardPassOperators.ReLuOperator;
import MachineLearning.Operators.OperatorResult;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class ReLuOperatorTest {

    @Test
    public void shouldReturnZeroIfInputLessThanZeroAndInputOtherwise() {
        OperatorResult input = new OperatorResult(2);
        input.add(0, -1f);
        input.add(1, 2f);

        ReLuOperator underTest = new ReLuOperator(2);

        assertEquals(0f, underTest.compute(input).get(0));
        assertEquals(2f, underTest.compute(input).get(1));
    }

    @Test
    public void shouldThrowExceptionIfInputIncorrectlySized() {
        OperatorResult input = new OperatorResult(1);
        input.add(0, -1f);

        ReLuOperator underTest = new ReLuOperator(2);

        try {
            underTest.compute(input);
            fail();
        } catch (Exception e) {
           assertEquals(ReLuOperator.INCORRECT_INPUT_SIZE_MESSAGE, e.getMessage());
        }

    }



}