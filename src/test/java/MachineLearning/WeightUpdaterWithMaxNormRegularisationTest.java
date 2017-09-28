package MachineLearning;

import MachineLearning.Weights.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class WeightUpdaterWithMaxNormRegularisationTest {

    private ArrayList<WeightDimensions> dimensionsArrayList;

    @Before
    public void setUp() {
        dimensionsArrayList = new ArrayList<>();
        dimensionsArrayList.add(new WeightDimensions(2,2));
        dimensionsArrayList.add(new WeightDimensions(0));
    }

    @Test
    public void shouldInititaliseWeightsAndEnsureMaxNormHeld() {
        WeightUpdaterWithMaxNormRegularisation underTest = new WeightUpdaterWithMaxNormRegularisation(0.01);
        underTest.setDimensionArray(dimensionsArrayList);
        underTest.initialiseWeights();
        final Weights weights = underTest.getWeightsArray().get(0);
        float sum = weights.getValue(0,0)*weights.getValue(0,0)
                + weights.getValue(0,1)*weights.getValue(0,1)
                + weights.getValue(1,0)*weights.getValue(1,0)
                + weights.getValue(1,1)*weights.getValue(1,1);
        assertTrue(0.01 > sum);
    }

    @Test
    public void shouldTakeGradientsAndLearningRateAndUpdateWeights() {
        WeightUpdaterWithMaxNormRegularisation underTest = new WeightUpdaterWithMaxNormRegularisation(1);
        underTest.setDimensionArray(dimensionsArrayList);
        underTest.initialiseWeights();

        final Weights weights = underTest.getWeightsArray().get(0);
        weights.setValue(0,0,1f);
        weights.setValue(0,1,2f);
        weights.setValue(1,0,3f);
        weights.setValue(1,1,4f);

        ArrayList<WeightsArray> arrayOfGradientArrays = new ArrayList<>();

        WeightsArray firstGradientArray = new WeightsArray(2);
        Weights firstGradients = new Weights(2,2);
        firstGradients.setValue(0,0, 0.1f);
        firstGradients.setValue(0,1, 0.2f);
        firstGradients.setValue(1,0, 0.3f);
        firstGradients.setValue(1,1, 0.4f);
        firstGradientArray.set(0, firstGradients);
        firstGradientArray.set(1, NoWeights.getWeightsWithNoWeights());
        arrayOfGradientArrays.add(firstGradientArray);

        WeightsArray secondGradientArray = new WeightsArray(2);
        Weights secondGradients = new Weights(2,2);
        secondGradients.setValue(0,0, 0.5f);
        secondGradients.setValue(0,1, 0.6f);
        secondGradients.setValue(1,0, 0.7f);
        secondGradients.setValue(1,1, 0.8f);
        secondGradientArray.set(0, secondGradients);
        secondGradientArray.set(1, NoWeights.getWeightsWithNoWeights());
        arrayOfGradientArrays.add(secondGradientArray);

        underTest.updateWeightsWithGradients(arrayOfGradientArrays, -1f);

        final Weights updatedWeights = underTest.getWeightsArray().get(0);

        // 1.3^2 + 2.4^2 + 3.5^2 + 4.6^2 = 40.86 sum of squares of new weights, root is 6.3921827258
        // Scale by this to get
        assertEquals(0.2033734f, updatedWeights.getValue(0,0));
        assertEquals(0.37545863f, updatedWeights.getValue(0,1));
        assertEquals(0.54754376f, updatedWeights.getValue(1,0));
        assertEquals(0.7196290f, updatedWeights.getValue(1,1));
    }

    @Test
    public void addingEmptyWeightsArrayResultsInNoChangeToWeights() {
        WeightUpdaterWithMaxNormRegularisation underTest = new WeightUpdaterWithMaxNormRegularisation(100);
        underTest.setDimensionArray(dimensionsArrayList);
        underTest.initialiseWeights();

        final Weights weights = underTest.getWeightsArray().get(0);
        weights.setValue(0,0,1f);
        weights.setValue(0,1,2f);
        weights.setValue(1,0,3f);
        weights.setValue(1,1,4f);

        ArrayList<WeightsArray> arrayOfGradientArrays = new ArrayList<>();
        arrayOfGradientArrays.add(new EmptyWeightsArray());
        arrayOfGradientArrays.add(new EmptyWeightsArray());
        arrayOfGradientArrays.add(new EmptyWeightsArray());

        underTest.updateWeightsWithGradients(arrayOfGradientArrays, 1f);

        final Weights updatedWeights = underTest.getWeightsArray().get(0);
        assertEquals(1f, updatedWeights.getValue(0,0));
        assertEquals(2f, updatedWeights.getValue(0,1));
        assertEquals(3f, updatedWeights.getValue(1,0));
        assertEquals(4f, updatedWeights.getValue(1,1));
    }

}