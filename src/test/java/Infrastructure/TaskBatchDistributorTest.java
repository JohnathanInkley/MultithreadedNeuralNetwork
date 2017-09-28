package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.ValidationTask;
import MachineLearning.ClassificationTarget;
import MachineLearning.MachineLearningModel;
import MachineLearning.Weights.EmptyWeightsArray;
import MachineLearning.Weights.WeightsArray;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class TaskBatchDistributorTest {

    public TaskBatchDistributor underTest;

    @Before
    public void setUp() {
        underTest = new TaskBatchDistributor(2);
        underTest.loadTrainingData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.loadValidationData(
                "src/test/java/Infrastructure/resources/DummyInputData.csv",
                "src/test/java/Infrastructure/resources/DummyInputLabels.csv"
        );
        underTest.setMachineLearningModel(new MachineLearningModel());
    }

    @Test
    public void shouldCreateTrainingTasksAndPushToChannel() throws InterruptedException {
        MemoryChannel<UpdateMessage> instructionChannel = underTest.getInputChannel();

        CountDownLatch latch = new CountDownLatch(2);
        underTest.setOutputTaskChannel(setUpTaskOutputChannel(latch, false));

        instructionChannel.publish(new UpdateMessage(new EmptyWeightsArray()));
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldCreateValidationTasksAndPublishToChannel() throws InterruptedException {
        MemoryChannel<UpdateMessage> instructionChannel = underTest.getInputChannel();

        CountDownLatch latch = new CountDownLatch(2);
        underTest.setOutputTaskChannel(setUpTaskOutputChannel(latch, true));

        instructionChannel.publish(new UpdateMessage(new WeightsArray(1)));
        instructionChannel.publish(new UpdateMessage(new WeightsArray(1)));
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    public MemoryChannel<Task> setUpTaskOutputChannel(CountDownLatch latch, boolean validationMode) {
        MemoryChannel<Task> taskChannel = new MemoryChannel<>();
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        taskChannel.subscribe(fiber, (task) -> {
           if (task.getTarget().equals(new ClassificationTarget(1))
                   || task.getTarget().equals(new ClassificationTarget(2))) {
               if (typeOfTaskMatchesMode(validationMode, task)) {
                   latch.countDown();
               }
           }
        });
        return taskChannel;
    }

    public boolean typeOfTaskMatchesMode(boolean validationMode, Task task) {
        return (task instanceof ValidationTask) == validationMode;
    }
}