package Infrastructure.TaskCreation;

import MachineLearning.ClassificationTarget;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class TaskBatchProducerTest {

    @Test
    public void shouldProduceTrainingTasks() {
        TaskBatchProducer underTest = new TaskBatchProducer(1);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.loadValidationData(
                "src/test/java/Infrastructure/resources/DummyValidationData.csv",
                "src/test/java/Infrastructure/resources/DummyValidationLabels.csv"
        );

        assertEquals(Task.getExampleTask(), underTest.getNextBatchOfTasks().get(0).getExampleOfThisTask());
        assertEquals(new ClassificationTarget(2), underTest.getNextBatchOfTasks().get(0).getTarget());
    }

    @Test
    public void shouldProduceValidationTasksAfterTrainingTasks() {
        TaskBatchProducer underTest = new TaskBatchProducer(2);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.loadValidationData(
                "src/test/java/Infrastructure/resources/DummyValidationData.csv",
                "src/test/java/Infrastructure/resources/DummyValidationLabels.csv"
        );

        ArrayList<Task> unusedTrainingBatch = underTest.getNextBatchOfTasks();
        ArrayList<Task> validationBatch = underTest.getNextBatchOfTasks();
        assertEquals(new ClassificationTarget(5), validationBatch.get(0).getTarget());
        assertEquals(new ClassificationTarget(6), validationBatch.get(1).getTarget());
        assertEquals(new ClassificationTarget(7), validationBatch.get(2).getTarget());
        assertEquals(Task.getExampleValidationTask(), validationBatch.get(0).getExampleOfThisTask());
    }

    @Test
    public void shouldThrowExceptionIfNoTrainingDataLoaded() {
        TaskBatchProducer underTest = new TaskBatchProducer(2);
        try {
            underTest.getNextBatchOfTasks();
            fail();
        } catch (Exception e) {
            assertEquals(TaskBatchProducer.NO_TRAINING_DATA_LOADED_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfNoValidationDataLoaded() {
        TaskBatchProducer underTest = new TaskBatchProducer(2);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        try {
            underTest.getNextBatchOfTasks();
            fail();
        } catch (Exception e) {
            assertEquals(TaskBatchProducer.NO_VALIDATION_DATA_LOADED_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void shouldSwitchBackToTrainingTasksAfterValidationTasks() {
        TaskBatchProducer underTest = new TaskBatchProducer(2);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.loadValidationData(
                "src/test/java/Infrastructure/resources/DummyValidationData.csv",
                "src/test/java/Infrastructure/resources/DummyValidationLabels.csv"
        );

        ArrayList<Task> unusedTrainingBatch = underTest.getNextBatchOfTasks();
        ArrayList<Task> unusedValidationBatch = underTest.getNextBatchOfTasks();

        ArrayList<Task> backToTrainingBatch = underTest.getNextBatchOfTasks();
        assertEquals(new ClassificationTarget(1), backToTrainingBatch.get(0).getTarget());
    }

    @Test
    public void tasksShouldHaveCorrectMetadata() {
        TaskBatchProducer underTest = new TaskBatchProducer(2);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.loadValidationData(
                "src/test/java/Infrastructure/resources/DummyValidationData.csv",
                "src/test/java/Infrastructure/resources/DummyValidationLabels.csv"
        );

        assertTrue(underTest.getNextBatchOfTasks().get(0).getMetadata().haveReachedEndOfBatch(2));
        assertTrue(underTest.getNextBatchOfTasks().get(0).getMetadata().haveReachedEndOfBatch(3));
    }
}