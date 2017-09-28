package Infrastructure.TaskCreation;

import MachineLearning.ClassificationTarget;
import MachineLearning.ExampleProcessingResult;
import MachineLearning.MachineLearningModel;
import MachineLearning.Operators.OperatorResult;

import java.util.ArrayList;

public class Task {

    private static final Task exampleTask = new Task(new OperatorResult(1), new ClassificationTarget(0));
    private static final Task exampleValidationTask = new ValidationTask(new OperatorResult(1), new ClassificationTarget(0));
    private static final String BAD_EXAMPLE_MESSAGE = "Example task should be either TaskCreation.exampleTask or TaskCreation.exampleValidationTask";

    private TaskMetadata metadata;
    protected final OperatorResult exampleData;
    protected final ClassificationTarget target;
    protected MachineLearningModel machineLearningModel = new MachineLearningModel();
    protected ExampleProcessingResult exampleProcessingResult;

    public static Task getNewTask(Task exampleTask, OperatorResult dataExample, ClassificationTarget dataLabel) {
        if (exampleTask == Task.exampleValidationTask) {
            return new ValidationTask(dataExample, dataLabel);
        } else if (exampleTask == Task.exampleTask) {
            return new Task(dataExample, dataLabel);
        } else {
            throw new AssertionError(BAD_EXAMPLE_MESSAGE);
        }
    }

    protected Task(OperatorResult exampleData, ClassificationTarget target) {
        this.exampleData = exampleData;
        this.target = target;
    }

    public void setMetaData(TaskMetadata currentBatchMetaData) {
        metadata = currentBatchMetaData;
    }

    public TaskMetadata getMetadata() {
        return metadata;
    }

    public static Task getExampleTask() {
        return exampleTask;
    }

    public static Task getExampleValidationTask() {
        return exampleValidationTask;
    }

    public ClassificationTarget getTarget() {
        return target;
    }

    public Task getExampleOfThisTask() {
        return Task.getExampleTask();
    }

    public void setMachineLearningModel(MachineLearningModel machineLearningModel) {
        this.machineLearningModel = machineLearningModel;
    }

    public void process() {
        ArrayList<OperatorResult> activations = machineLearningModel.passExampleThroughOperators(exampleData);
        exampleProcessingResult = new ExampleProcessingResult(
                machineLearningModel.isClassifiedCorrectly(target, activations),
                machineLearningModel.calculateGradientArray(target, activations)
        );
    }

    public ExampleProcessingResult getExampleProcessingResult() {
        return exampleProcessingResult;
    }
}
