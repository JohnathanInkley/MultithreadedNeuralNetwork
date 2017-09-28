package Infrastructure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ResultFileLoggerTest {

    private static final String trainingResultsLocation = "src/test/java/Infrastructure/resources/trainingResults.dat";
    private static final String validationResultsLocation = "src/test/java/Infrastructure/resources/validationResults.dat";

    @Before
    public void deleteOldFiles() {
        try {
            if (Files.isRegularFile(Paths.get(trainingResultsLocation))) {
                Files.delete(Paths.get(trainingResultsLocation));
            }
            if (Files.isRegularFile(Paths.get(validationResultsLocation))) {
                Files.delete(Paths.get(validationResultsLocation));
            }
        } catch (Exception e) {
            throw new RuntimeException("Results files could not be deleted, check paths and try again");
        }
    }

    @After
    public void deleteFilesCreatedInTest() {
        deleteOldFiles();
    }

    @Test
    public void shouldCreateResultsFilesOnCreation() throws IOException {
        ResultFileLogger underTest = new ResultFileLogger(trainingResultsLocation, validationResultsLocation);

        assertTrue(Files.isRegularFile(Paths.get(trainingResultsLocation)));
        assertTrue(Files.isRegularFile(Paths.get(validationResultsLocation)));
    }

    @Test
    public void shouldAcceptTrainingResultsAndWriteToFile() throws IOException {
        ResultFileLogger underTest = new ResultFileLogger(trainingResultsLocation, validationResultsLocation);
        ArrayList<Boolean> results = new ArrayList<>();
        results.add(true);
        results.add(true);
        results.add(false);
        underTest.writeTrainingResult(1, results);
        results.add(false);
        underTest.writeTrainingResult(2, results);

        assertEquals("Epoch: 1, Classification rate: 66.67%", Files.readAllLines(Paths.get(trainingResultsLocation)).get(0));
        assertEquals("Epoch: 2, Classification rate: 50.00%", Files.readAllLines(Paths.get(trainingResultsLocation)).get(1));
    }

    @Test
    public void shouldAcceptValidationResultsAndWriteToFile() throws IOException {
        ResultFileLogger underTest = new ResultFileLogger(trainingResultsLocation, validationResultsLocation);
        ArrayList<Boolean> results = new ArrayList<>();
        results.add(true);
        results.add(true);
        results.add(false);
        underTest.writeValidationResult(1, results);
        results.add(false);
        underTest.writeValidationResult(2, results);

        assertEquals("Epoch: 1, Classification rate: 66.67%", Files.readAllLines(Paths.get(validationResultsLocation)).get(0));
        assertEquals("Epoch: 2, Classification rate: 50.00%", Files.readAllLines(Paths.get(validationResultsLocation)).get(1));
    }


}