package MachineLearning;

import MachineLearning.Weights.WeightDimensions;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class WeightDimensionsTest {

    private WeightDimensions underTest;

    @Before
    public void setUp() {
        underTest = new WeightDimensions(2,3,4);
    }

    @Test
    public void shouldGetDimensions() {
        assertEquals(2, underTest.getDimension(0));
        assertEquals(3, underTest.getDimension(1));
        assertEquals(4, underTest.getDimension(2));
    }

    @Test
    public void shouldThrowExceptionIfryToNegative() {
        try {
            underTest.getDimension(-1);
            fail();
        } catch (Exception e) {
            assertEquals(WeightDimensions.DIMENSION_OUT_OF_BOUNDS_MESSAGE + 2, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfryToGetOneOutOfRange() {
        try {
            underTest.getDimension(3);
            fail();
        } catch (Exception e) {
            assertEquals(WeightDimensions.DIMENSION_OUT_OF_BOUNDS_MESSAGE + 2, e.getMessage());
        }
    }
}