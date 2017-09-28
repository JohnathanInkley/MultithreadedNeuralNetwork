package Infrastructure;

import Infrastructure.TaskCreation.Task;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;

public class TaskProcessingWorker {

    private final MemoryChannel<Task> inputTaskChannel;
    ThreadFiber fiber;

    private MemoryChannel<Task> processedTaskChannel;

    public TaskProcessingWorker() {
        inputTaskChannel = new MemoryChannel<>();
        fiber = new ThreadFiber();
        subscribeToInputChannel();
    }

    private void subscribeToInputChannel() {
        fiber.start();
        inputTaskChannel.subscribe(fiber, this::processTask);
    }

    public MemoryChannel<Task> getInputTaskChannel() {
        return inputTaskChannel;
    }

    private void processTask(Task task) {
        task.process();
        processedTaskChannel.publish(task);
    }

    public void setOutputChannel(MemoryChannel<Task> processedTaskChannel) {
        this.processedTaskChannel = processedTaskChannel;
    }
}
