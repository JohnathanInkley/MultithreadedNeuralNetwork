package Infrastructure.TaskCreation;

import MachineLearning.ClassificationTarget;
import MachineLearning.Operators.OperatorResult;

public class DummyTask extends Task {

    public DummyTask(OperatorResult exampleData, ClassificationTarget target) {
        super(exampleData, target);
    }

    @Override
    public void process() {

    }
}
