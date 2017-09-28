package Infrastructure;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class TaskChannelMultiplexerTest {

    @Test
    public void shouldSubscribeToInputChannelAndPassToChannelLoadManager() throws InterruptedException {
        TaskChannelMultiplexer<Integer> underTest = new TaskChannelMultiplexer<>();

        MemoryChannel<Integer> inputChannel = underTest.getInputChannel();

        CountDownLatch latch = new CountDownLatch(1);
        underTest.setChannelLoadManager(getChannelLoadManager(latch, 3));

        inputChannel.publish(3);
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    private ChannelLoadManager<Integer> getChannelLoadManager(CountDownLatch latch, Integer countdownWhenReceived) {
        MemoryChannel<Integer> channel = new MemoryChannel<>();
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        channel.subscribe(fiber, (x) -> {
            if (x == countdownWhenReceived) {
                latch.countDown();
            }
        });
        ChannelLoadManager<Integer> loadManager = new ChannelLoadManager<>();
        loadManager.addChannel(channel);
        return loadManager;
    }

}