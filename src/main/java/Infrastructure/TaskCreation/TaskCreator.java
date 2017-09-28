package Infrastructure.TaskCreation;

import Infrastructure.InputDataReader;
import MachineLearning.ClassificationTarget;
import MachineLearning.Operators.OperatorResult;

import java.util.ArrayList;

public class TaskCreator {

    public static final String NO_FILE_LOADED_MESSAGE = "No file loaded for the TaskCreator";

    private final Task taskTypeExample;
    private InputDataReader dataReader;
    private ArrayList<OperatorResult> examples;
    private ArrayList<Integer> labels;
    private int positionInData;

    public TaskCreator(Task taskTypeExample) {
        this.taskTypeExample = taskTypeExample;
        resetPositionInData();
    }

    private void resetPositionInData() {
        positionInData = -1;
    }

    public void readInData(String exampleFilePath, String labelFilePath) {
        dataReader = new InputDataReader();
        examples = dataReader.readExamples(exampleFilePath);
        labels = dataReader.readLabels(labelFilePath);
    }

    public Task getNextTask() {
        if (dataReader == null) {
            throw new RuntimeException(NO_FILE_LOADED_MESSAGE);
        } else {
            return getNextTaskFromDataReaderByUpdatingPosition();
        }
    }

    private Task getNextTaskFromDataReaderByUpdatingPosition() {
        if (!hasNextTask()) {
            resetPositionInData();
        }
        positionInData++;
        return getTaskAtCurrentPosition();
    }

    private boolean hasNextTask() {
        return positionInData + 1 < getNumberExamplesInFile();
    }

    public Task getTaskAtCurrentPosition() {
        return Task.getNewTask(taskTypeExample, getDataExample(), new ClassificationTarget(getDataLabel()));
    }

    private OperatorResult getDataExample() {
        return examples.get(positionInData);
    }

    private Integer getDataLabel() {
        return labels.get(positionInData);
    }

    public Integer getNumberExamplesInFile() {
        if (dataReader == null) {
            return 0;
        } else {
            return examples.size();
        }
    }
}
