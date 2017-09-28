package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.TaskMetadata;
import MachineLearning.EndOfBatchMessage;
import MachineLearning.ExampleProcessingResult;
import MachineLearning.Weights.WeightsArray;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ResultsHandlerTest {

    private ResultsHandler underTest;
    private MemoryChannel<Task> inputChannel;
    private MemoryChannel<EndOfBatchMessage> outputChannel;
    private Task exampleTask;
    private WeightsArray exampleGradients;

    @Before
    public void setUp() {
        underTest = new ResultsHandler();
        inputChannel = underTest.getInputChannel();
        outputChannel = new MemoryChannel<>();
        underTest.setOutputChannel(outputChannel);

        exampleTask = Mockito.mock(Task.class);
        exampleGradients = new WeightsArray(1);
        Mockito.when(exampleTask.getExampleProcessingResult()).thenReturn(new ExampleProcessingResult(true, exampleGradients));
    }

    @Test
    public void shouldSendResultWhenAllTasksAreCollected() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> latch.countDown());

        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(5, true));
        for (int i = 0; i < 5; i++) {
            inputChannel.publish(exampleTask);
        }

        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldNotSendResultBeforeAllTasksAreCollected() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> latch.countDown());

        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(5, true));
        for (int i = 0; i < 4; i++) {
            inputChannel.publish(exampleTask);
        }

        assertFalse(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldSendResultsAfterTwoBatchesCollected() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> latch.countDown());

        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(5, true));
        for (int i = 0; i < 10; i++) {
            inputChannel.publish(exampleTask);
        }

        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldStoreResultsWhenReceivingProcessedTasks() throws InterruptedException {
        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(2, true));

        CountDownLatch latch = new CountDownLatch(1);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> {
            latch.countDown();
            assertEquals(exampleGradients, msg.getGradients().get(0));
            assertEquals(exampleGradients, msg.getGradients().get(1));
            assertEquals(true, msg.getClassifications().get(0));
            assertEquals(2, msg.getGradients().size());
        });

        inputChannel.publish(exampleTask);
        inputChannel.publish(exampleTask);

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldPassOnWhenLastJobInEpoch() throws InterruptedException {
        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(2, true));

        CountDownLatch latch = new CountDownLatch(1);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> {
            latch.countDown();
            assertEquals(true, msg.isEndOfEpoch());
        });

        inputChannel.publish(exampleTask);
        inputChannel.publish(exampleTask);

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldPassOnWhenNotLastJobInEpoch() throws InterruptedException {
        Mockito.when(exampleTask.getMetadata()).thenReturn(new TaskMetadata(2, false));

        CountDownLatch latch = new CountDownLatch(1);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (msg) -> {
            latch.countDown();
            assertEquals(false, msg.isEndOfEpoch());
        });

        inputChannel.publish(exampleTask);
        inputChannel.publish(exampleTask);

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

}