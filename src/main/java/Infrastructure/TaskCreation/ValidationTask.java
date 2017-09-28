package Infrastructure.TaskCreation;

import MachineLearning.ClassificationTarget;
import MachineLearning.ExampleProcessingResult;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.EmptyWeightsArray;

import java.util.ArrayList;

public class ValidationTask extends Task {

    public ValidationTask(OperatorResult exampleData, ClassificationTarget target) {
       super(exampleData, target);
    }

    public Task getExampleOfThisTask() {
        return Task.getExampleValidationTask();
    }

    public void process() {
        ArrayList<OperatorResult> activations = machineLearningModel.passExampleThroughOperators(exampleData);
        exampleProcessingResult = new ExampleProcessingResult(
                machineLearningModel.isClassifiedCorrectly(target, activations),
                new EmptyWeightsArray()
        );
    }

}
