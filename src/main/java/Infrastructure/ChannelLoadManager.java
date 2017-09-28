package Infrastructure;

import org.jetlang.channels.MemoryChannel;

import java.util.LinkedList;
import java.util.Queue;

public class ChannelLoadManager<T> {

    public static final String PUBLISH_WHEN_EMPTY_ERROR_MESSAGE = "Cannot publish to empty ChannelLoadManager";

    private final Queue<MemoryChannel<T>> queueForChannels;

    public ChannelLoadManager() {
        queueForChannels = new LinkedList<>();
    }

    public void addChannel(MemoryChannel<T> channel) {
        queueForChannels.add(channel);
    }

    public int getNumberChannels() {
        return queueForChannels.size();
    }

    public synchronized void publish(T item) {
        if (getNumberChannels() > 0) {
            publishToChannelNotUsedForLongestTime(item);
        } else {
            throw new RuntimeException(PUBLISH_WHEN_EMPTY_ERROR_MESSAGE);
        }
    }

    private void publishToChannelNotUsedForLongestTime(T item) {
        MemoryChannel<T> channelNotBeenUsedForLongestTime = queueForChannels.remove();
        channelNotBeenUsedForLongestTime.publish(item);
        queueForChannels.add(channelNotBeenUsedForLongestTime);
    }
}
