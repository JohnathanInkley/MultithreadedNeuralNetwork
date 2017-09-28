package Infrastructure.TaskCreation;

import MachineLearning.*;
import MachineLearning.ErrorFunction.CrossEntropyError;
import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.NoWeights;
import MachineLearning.Weights.Weights;
import MachineLearning.Weights.WeightsArray;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TaskTest {

    private MachineLearningModel model;
    private OperatorResult example;
    private Weights expectedGradients;

    @Before
    public void setUp() {
        model = new MachineLearningModel();
        OperatorGradientPair innerProductPair = new OperatorGradientPair(new InnerProductOperator(2, 3));
        OperatorGradientPair softmaxPair = new OperatorGradientPair(new SoftmaxOperator(3));
        model.addOperatorPair(0, innerProductPair);
        model.addOperatorPair(1, softmaxPair);
        model.setErrorFunction(new CrossEntropyError());
        model.setWeightsArray(generateWeightsArray());
        generateExample();
        generateExpectedGradients();
    }

    @Test
    public void TaskShouldStoreMachineLearningResultAfterProcessing() {
        Task underTest = new Task(example, new ClassificationTarget(2));
        underTest.setMachineLearningModel(model);
        underTest.process();
        ExampleProcessingResult result = underTest.getExampleProcessingResult();
        assertTrue(result.exampleWasClassifiedCorrectly());
        assertEquals(expectedGradients, result.getGradientArray().get(0));

    }

    private void generateExample() {
        example = new OperatorResult(2);
        example.add(0, 0.3f);
        example.add(1, 0.5f);
    }

    private WeightsArray generateWeightsArray() {
        WeightsArray weightsArray = new WeightsArray(2);
        Weights weights = new Weights(3, 2);
        for (int i = 0; i < 6; i++) {
            weights.setValue(i % 3, i % 2, (float) i);
        }
        weightsArray.set(0, weights);
        weightsArray.set(1, NoWeights.getWeightsWithNoWeights());
        return weightsArray;
    }

    private void generateExpectedGradients() {
        expectedGradients = new Weights(3,2);
        expectedGradients.setValue(0,0, 0.04181514f);
        expectedGradients.setValue(0,1, 0.06969190f);
        expectedGradients.setValue(1,0, 0.05107313f);
        expectedGradients.setValue(1,1, 0.08512188f);
        expectedGradients.setValue(2,0, -0.092888296f);
        expectedGradients.setValue(2,1, -0.15481383f);
    }


}