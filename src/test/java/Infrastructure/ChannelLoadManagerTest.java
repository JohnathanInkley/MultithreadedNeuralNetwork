package Infrastructure;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class ChannelLoadManagerTest {

    private ChannelLoadManager<Integer> underTest;
    private MemoryChannel<Integer> channel1;
    private MemoryChannel<Integer> channel2;

    @Before
    public void setUpLoadManager() {
        underTest = new ChannelLoadManager<>();
        channel1 = new MemoryChannel<>();
        channel2 = new MemoryChannel<>();
        underTest.addChannel(channel1);
        underTest.addChannel(channel2);
    }

    @Test
    public void shouldTakeChannelsAndStoreThem() {
        assertEquals(2, underTest.getNumberChannels());
    }

    @Test
    public void shouldTakeObjectsAndDistributeToChannelsInRoundRobinFashion() throws InterruptedException {
        ThreadFiber fiber1 = new ThreadFiber();
        ThreadFiber fiber2 = new ThreadFiber();
        fiber1.start();
        fiber2.start();

        CountDownLatch latch = new CountDownLatch(4);

        channel1.subscribe(fiber1, (x) -> {
            if (x == 1 || x== 3) {
                latch.countDown();
            }
        });
        channel2.subscribe(fiber2, (x) -> {
            if (x == 2 || x== 4) {
                latch.countDown();
            }
        });

        for (int i = 1; i <= 4; i++) {
            underTest.publish(i);
        }

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldThrowExceptionIfPublishAndNoChannelsAdded() {
        try {
            new ChannelLoadManager<Integer>().publish(0);
            fail();
        } catch (Exception e) {
            assertEquals(ChannelLoadManager.PUBLISH_WHEN_EMPTY_ERROR_MESSAGE, e.getMessage());
        }
    }

}