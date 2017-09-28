package Infrastructure;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;

public class TaskChannelMultiplexer<T> {

    private final ThreadFiber fiber;
    private MemoryChannel<T> inputChannel;
    private ChannelLoadManager<T> loadManager;

    public TaskChannelMultiplexer() {
        inputChannel = new MemoryChannel<>();
        fiber = new ThreadFiber();
        subscribeToInputChannel();
    }

    public void subscribeToInputChannel() {
        fiber.start();
        inputChannel.subscribe(fiber, (item) -> loadManager.publish(item));
    }

    public void setChannelLoadManager(ChannelLoadManager<T> loadManager) {
        this.loadManager = loadManager;
    }

    public MemoryChannel<T> getInputChannel() {
        return inputChannel;
    }
}
