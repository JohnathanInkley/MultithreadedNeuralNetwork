package Infrastructure.TaskCreation;

import java.util.ArrayList;

public class TaskBatchProducer {

    static final String NO_TRAINING_DATA_LOADED_MESSAGE = "Training data not loaded";
    static final String NO_VALIDATION_DATA_LOADED_MESSAGE = "Validation data not loaded";

    private final TaskCreator validationTaskCreator;
    private final TaskCreator trainingTaskCreator;

    private boolean validationMode;
    private final Integer trainingBatchSize;
    private Integer numberTasksProducedSoFar;
    private Integer numberOfTasksForNewBatch;

    public TaskBatchProducer(Integer trainingBatchSize) {
        validationTaskCreator = new TaskCreator(Task.getExampleValidationTask());
        trainingTaskCreator = new TaskCreator(Task.getExampleTask());
        validationMode = false;
        this.trainingBatchSize = trainingBatchSize;
        resetNumberTasksProducedSoFar();
    }

    private void resetNumberTasksProducedSoFar() {
        numberTasksProducedSoFar = 0;
    }

    public void loadTrainingData(String examplesFilePath, String labelsFilePath) {
        trainingTaskCreator.readInData(examplesFilePath, labelsFilePath);
    }

    public void loadValidationData(String examplesFilePath, String labelsFilePath) {
        validationTaskCreator.readInData(examplesFilePath, labelsFilePath);
    }

    public ArrayList<Task> getNextBatchOfTasks() {
        checkDataLoaded();
        if (validationMode) {
            return generateValidationTasks();
        } else {
            return generateTrainingTasks();
        }
    }

    private void checkDataLoaded() {
        if (trainingTaskCreator.getNumberExamplesInFile().equals(0)) {
            throw new RuntimeException(NO_TRAINING_DATA_LOADED_MESSAGE);
        } else if (validationTaskCreator.getNumberExamplesInFile().equals(0)) {
            throw new RuntimeException(NO_VALIDATION_DATA_LOADED_MESSAGE);
        }
    }

    private ArrayList<Task> generateValidationTasks() {
        ArrayList<Task> result = generateTaskArray(validationTaskCreator, validationTaskCreator.getNumberExamplesInFile(), true);
        validationMode = false;
        return result;
    }

    private ArrayList<Task> generateTaskArray(TaskCreator taskCreator, Integer numberOfTasksToMake, boolean lastJobInEpoch) {
        TaskMetadata currentBatchMetaData = new TaskMetadata(numberOfTasksToMake, lastJobInEpoch);
        ArrayList<Task> result = new ArrayList<>(numberOfTasksToMake);
        for (int i = 0; i < numberOfTasksToMake; i++) {
            Task task = taskCreator.getNextTask();
            task.setMetaData(currentBatchMetaData);
            result.add(task);
        }
        return result;
    }

    private ArrayList<Task> generateTrainingTasks() {
        calculateNumberOfTasksForNextBatch();
        updateTaskProducedSoFarCount();
        if (getNumberTrainingTasksLeftInFile().equals(0)) {
            changeToValidationMode();
            return generateTaskArray(trainingTaskCreator, numberOfTasksForNewBatch, true);
        } else {
            return generateTaskArray(trainingTaskCreator, numberOfTasksForNewBatch, false);
        }
    }

    private void calculateNumberOfTasksForNextBatch() {
        numberOfTasksForNewBatch = Math.min(trainingBatchSize, getNumberTrainingTasksLeftInFile());
    }

    private Integer getNumberTrainingTasksLeftInFile() {
        return trainingTaskCreator.getNumberExamplesInFile() - numberTasksProducedSoFar;
    }

    private void updateTaskProducedSoFarCount() {
        numberTasksProducedSoFar += numberOfTasksForNewBatch;
    }

    private void changeToValidationMode() {
        validationMode = true;
        resetNumberTasksProducedSoFar();
    }

}
