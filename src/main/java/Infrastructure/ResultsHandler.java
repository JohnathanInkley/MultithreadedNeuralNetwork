package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.TaskMetadata;
import MachineLearning.EndOfBatchMessage;
import MachineLearning.ExampleProcessingResult;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;

public class ResultsHandler {

    private final MemoryChannel<Task> inputChannel;
    private Channel<EndOfBatchMessage> outputChannel;
    private final ThreadFiber fiber;

    private Integer numberOfUpdatesReceivedFromBatch;
    private final ArrayList<ExampleProcessingResult> receivedResults;

    public ResultsHandler() {
        resetCountForBatch();
        inputChannel = new MemoryChannel<>();
        fiber = new ThreadFiber();
        subscribeToChannel();
        receivedResults = new ArrayList<>();
    }

    public void resetCountForBatch() {
        numberOfUpdatesReceivedFromBatch = 0;
    }

    private void subscribeToChannel() {
        fiber.start();
        inputChannel.subscribe(fiber, this::processCompletedTask);
    }

    private void processCompletedTask(Task completedTask) {
        numberOfUpdatesReceivedFromBatch++;
        storeResultsFromTask(completedTask);
        if (completedTask.getMetadata().haveReachedEndOfBatch(numberOfUpdatesReceivedFromBatch)) {
            performEndOfBatchThings(completedTask.getMetadata());
        }
    }

    private void storeResultsFromTask(Task completedTask) {
        receivedResults.add(completedTask.getExampleProcessingResult());
    }

    private void performEndOfBatchThings(TaskMetadata batchMetadata) {
        resetCountForBatch();
        sendEndOfBatchUpdate(batchMetadata);
        receivedResults.clear();
    }

    private void sendEndOfBatchUpdate(TaskMetadata batchMetadata) {
        outputChannel.publish(new EndOfBatchMessage(
                batchMetadata.isLastBatchInEpoch(),
                new ArrayList<>(receivedResults)
        ));
    }

    public MemoryChannel<Task> getInputChannel() {
        return inputChannel;
    }

    public void setOutputChannel(Channel<EndOfBatchMessage> outputChannel) {
        this.outputChannel = outputChannel;
    }

}
