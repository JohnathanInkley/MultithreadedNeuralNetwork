package Infrastructure.TaskCreation;

public class TaskMetadata {
    private final Integer tasksInCurrentBatch;
    public boolean lastBatchInEpoch;

    public TaskMetadata(Integer tasksInCurrentBatch) {
        this.tasksInCurrentBatch = tasksInCurrentBatch;
    }

    public TaskMetadata(Integer tasksInCurrentBatch, boolean lastBatchInEpoch) {
        this.tasksInCurrentBatch = tasksInCurrentBatch;
        this.lastBatchInEpoch = lastBatchInEpoch;
    }

    public boolean haveReachedEndOfBatch(Integer count) {
        return tasksInCurrentBatch.equals(count);
    }

    public boolean isLastBatchInEpoch() {
        return lastBatchInEpoch;
    }
}
