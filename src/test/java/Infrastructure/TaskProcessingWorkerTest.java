package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.DummyTask;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class TaskProcessingWorkerTest {

    @Test
    public void shouldReturnMemoryChannelAsTaskTrayWhenAsked() {
        TaskProcessingWorker underTest = new TaskProcessingWorker();
        assertTrue(underTest.getInputTaskChannel() != null);
    }

    @Test
    public void shouldTakeTaskAndPushResultsToOutputChannel() throws InterruptedException {
        TaskProcessingWorker underTest = new TaskProcessingWorker();
        MemoryChannel<Task> updateChannel = new MemoryChannel<>();
        underTest.setOutputChannel(updateChannel);

        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        CountDownLatch latch = new CountDownLatch(1);
        updateChannel.subscribe(fiber, (update) -> {
            latch.countDown();
        });

        underTest.getInputTaskChannel().publish(new DummyTask(null, null));
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

}