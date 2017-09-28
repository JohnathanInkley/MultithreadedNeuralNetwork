package MachineLearning;

import MachineLearning.ErrorFunction.CrossEntropyError;
import MachineLearning.Operators.ForwardPassOperators.InnerProductOperator;
import MachineLearning.Operators.ForwardPassOperators.SoftmaxOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.NoWeights;
import MachineLearning.Weights.WeightDimensions;
import MachineLearning.Weights.Weights;
import MachineLearning.Weights.WeightsArray;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MachineLearningModelTest {

    private MachineLearningModel underTest;
    private OperatorResult example;

    @Before
    public void setUp() {
        underTest = new MachineLearningModel();

        OperatorGradientPair innerProductPair = new OperatorGradientPair(new InnerProductOperator(2, 3));
        OperatorGradientPair softmaxPair = new OperatorGradientPair(new SoftmaxOperator(3));
        underTest.addOperatorPair(0, innerProductPair);
        underTest.addOperatorPair(1, softmaxPair);
        underTest.setErrorFunction(new CrossEntropyError());

        WeightsArray weightsArray = new WeightsArray(2);
        Weights weights = new Weights(3, 2);
        for (int i = 0; i < 6; i++) {
            weights.setValue(i % 3, i % 2, (float) i);
        }
        weightsArray.set(0, weights);
        weightsArray.set(1, NoWeights.getWeightsWithNoWeights());
        underTest.setWeightsArray(weightsArray);

        example = new OperatorResult(2);
        example.add(0, 0.3f);
        example.add(1, 0.5f);
    }

    @Test
    public void shouldTakeOperatorPairsAndPassDataThroughThem() {
        ClassificationTarget wrongTarget = new ClassificationTarget(1);
        ClassificationTarget rightTarget = new ClassificationTarget(2);

        assertFalse(underTest.processExample(example, wrongTarget).exampleWasClassifiedCorrectly());
        assertTrue(underTest.processExample(example, rightTarget).exampleWasClassifiedCorrectly());
    }

    @Test
    public void shouldCalculateGradientsCorrectly() {
        ClassificationTarget target = new ClassificationTarget(2);

        Weights expectedGradients = new Weights(3,2);
        expectedGradients.setValue(0,0, 0.04181514f);
        expectedGradients.setValue(0,1, 0.06969190f);
        expectedGradients.setValue(1,0, 0.05107313f);
        expectedGradients.setValue(1,1, 0.08512188f);
        expectedGradients.setValue(2,0, -0.092888296f);
        expectedGradients.setValue(2,1, -0.15481383f);

        assertEquals(expectedGradients, underTest.processExample(example, target).getGradientArray().get(0));
    }

    @Test
    public void shouldReturnArrayOfWeightDimensions() {
        assertEquals(new WeightDimensions(3, 2), underTest.getArrayOfWeightDimensions().get(0));
        assertEquals(new WeightDimensions(0), underTest.getArrayOfWeightDimensions().get(1));

    }
}