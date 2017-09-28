package Infrastructure;

import MachineLearning.Operators.OperatorResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class InputDataReaderTest {

    private static final InputDataReader underTest = new InputDataReader();

    @Test
    public void shouldProduceArrayListOfExamplesFromFile() {
        ArrayList<OperatorResult> inputExamples
                = underTest.readExamples("src/test/java/Infrastructure/resources/DummyInputData.csv");
        OperatorResult example1 = new OperatorResult(2);
        example1.add(0, 0.0f);
        example1.add(1, 0.1f);
        OperatorResult example2 = new OperatorResult(2);
        example2.add(0, 1.0f);
        example2.add(1, 0.0f);

        assertEquals(example1, inputExamples.get(0));
        assertEquals(example2, inputExamples.get(1));
    }

    @Test
    public void shouldThrowExceptionIfExampleFileNotFound() {
        try {
            underTest.readExamples("BAD_FILE_PATH");
            fail();
        } catch (Exception e) {
            assertEquals(InputDataReader.FILE_NOT_FOUND_MESSAGE + "BAD_FILE_PATH", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfExampleFileMalformed() {
        try {
            underTest.readExamples("src/test/java/Infrastructure/resources/MalformedInputData.csv");
            fail();
        } catch (Exception e) {
            assertEquals(InputDataReader.MALFORMED_FILE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldProduceArrayListOfLabelsFromFile() {
        List<Integer> labels
                = underTest.readLabels("src/test/java/Infrastructure/resources/DummyInputLabels.csv");
        assertEquals(Arrays.asList(1, 2), labels);
    }

    @Test
    public void shouldThrowExceptionIfLabelsFileNotFound() {
        try {
            underTest.readLabels("BAD_FILE_PATH");
        } catch (Exception e) {
            assertEquals(InputDataReader.FILE_NOT_FOUND_MESSAGE + "BAD_FILE_PATH", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfLabelsFileMalformed() {
        try {
            underTest.readExamples("src/test/java/Infrastructure/resources/MalformedInputLabels.csv");
            fail();
        } catch (Exception e) {
            assertEquals(InputDataReader.MALFORMED_FILE_MESSAGE, e.getMessage());
        }
    }
}