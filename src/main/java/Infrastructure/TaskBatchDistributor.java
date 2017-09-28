package Infrastructure;

import Infrastructure.TaskCreation.Task;
import Infrastructure.TaskCreation.TaskBatchProducer;
import MachineLearning.MachineLearningModel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;

public class TaskBatchDistributor {

    private final ThreadFiber fiber;
    private final TaskBatchProducer taskBatchProducer;
    private final MemoryChannel<UpdateMessage> inputChannel;
    private MemoryChannel<Task> outputTaskChannel;
    private MachineLearningModel machineLearningModel;

    public TaskBatchDistributor(Integer numberTasksInTrainingBatch) {
        taskBatchProducer = new TaskBatchProducer(numberTasksInTrainingBatch);
        fiber = new ThreadFiber();
        inputChannel = new MemoryChannel<>();
        subscribeToInputChannel();
    }

    private void subscribeToInputChannel() {
        fiber.start();
        inputChannel.subscribe(fiber, this::processMessage);
    }

    private void processMessage(UpdateMessage message) {
        ArrayList<Task> nextBatchOfTasks = taskBatchProducer.getNextBatchOfTasks();
        machineLearningModel.setWeightsArray(message.getWeightsToUseForNewBatch());
        for (Task task : nextBatchOfTasks) {
            task.setMachineLearningModel(machineLearningModel);
            outputTaskChannel.publish(task);
        }
    }

    public MemoryChannel<UpdateMessage> getInputChannel() {
        return inputChannel;
    }

    public void loadTrainingData(String examplesFilePath, String labelsFilePath) {
        taskBatchProducer.loadTrainingData(examplesFilePath, labelsFilePath);
    }

    public void loadValidationData(String examplesFilePath, String labelsFilePath) {
        taskBatchProducer.loadValidationData(examplesFilePath, labelsFilePath);
    }

    public void setOutputTaskChannel(MemoryChannel<Task> outputTaskChannel) {
        this.outputTaskChannel = outputTaskChannel;
    }

    public void setMachineLearningModel(MachineLearningModel machineLearningModel) {
        this.machineLearningModel = machineLearningModel;
    }

}
