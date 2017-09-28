package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.TaskCreator;
import MachineLearning.ClassificationTarget;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class TaskCreatorTest {

    private TaskCreator underTest;

    @Before
    public void setUp() {
        underTest = new TaskCreator(Task.getExampleTask());
        underTest.readInData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
    }

    @Test
    public void shouldProduceTasksAndLoopRoundAtEndOfData() {
        assertEquals(new ClassificationTarget(1), underTest.getNextTask().getTarget());
        assertEquals(new ClassificationTarget(2), underTest.getNextTask().getTarget());
        assertEquals(new ClassificationTarget(1), underTest.getNextTask().getTarget());
        assertEquals(new ClassificationTarget(2), underTest.getNextTask().getTarget());
    }

    @Test
    public void shouldGiveCorrectNumberOfTasksInFile() {
        assertEquals(new Integer(2), underTest.getNumberExamplesInFile());
    }

    @Test
    public void shouldGiveZeroSizeIfNoFileReadIn() {
        TaskCreator badUnderTest = new TaskCreator(Task.getExampleTask());
        assertEquals(new Integer(0), badUnderTest.getNumberExamplesInFile());
    }

    @Test
    public void shouldThrowExceptionIfTaskWhenNoFileReadIn() {
        TaskCreator badUnderTest = new TaskCreator(Task.getExampleTask());
        try {
            badUnderTest.getNextTask();
            fail();
        } catch (Exception e) {
            assertEquals(TaskCreator.NO_FILE_LOADED_MESSAGE, e.getMessage());
        }
    }

}