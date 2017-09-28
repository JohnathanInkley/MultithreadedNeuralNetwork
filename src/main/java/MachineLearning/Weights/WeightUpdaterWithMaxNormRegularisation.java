package MachineLearning.Weights;

import java.util.ArrayList;

public class WeightUpdaterWithMaxNormRegularisation extends WeightUpdater {

    private final double maxNorm;
    private ArrayList<WeightDimensions> dimensionsArrayList;

    public WeightUpdaterWithMaxNormRegularisation(double maxNorm) {
        this.maxNorm = maxNorm;
    }

    @Override
    public void setDimensionArray(ArrayList<WeightDimensions> dimensionsArrayList) {
        this.dimensionsArrayList = dimensionsArrayList;
        weightsArray = new WeightsArray(dimensionsArrayList.size());
    }

    @Override
    public void initialiseWeights() {
        generateRandomWeights();
        regularizeWeights();
    }

    private void generateRandomWeights() {
        for (int i = 0; i < dimensionsArrayList.size(); i++) {
            generateRandomWeightsFromDimensions(i);
        }
    }

    private void generateRandomWeightsFromDimensions(int i) {
        final WeightDimensions currentWeightDimensions = dimensionsArrayList.get(i);
        if (currentWeightDimensions.getDimension(0) == 0) {
            weightsArray.set(i, NoWeights.getWeightsWithNoWeights());
        } else {
            weightsArray.set(i,
                    generateWeightsBetweenZeroAndOneRandomly(
                            currentWeightDimensions.getDimension(0),
                            currentWeightDimensions.getDimension(1)
                    )
            );
        }
    }

    private Weights generateWeightsBetweenZeroAndOneRandomly(int rows, int columns) {
        Weights result = new Weights(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.setValue(i, j, (float) Math.random());
            }
        }
        return result;
    }

    private void regularizeWeights() {
        for (int i = 0; i < weightsArray.size(); i++) {
            regularizeSingleWeight(weightsArray.get(i));
        }
    }

    private void regularizeSingleWeight(Weights weights) {
        Double sum = getRootOfSumOfSquareOfWeights(weights);
        if (sum > maxNorm) {
            scaleWeightsBy(weights, maxNorm/sum);
        }
    }

    private double getRootOfSumOfSquareOfWeights(Weights weights) {
        float sumOfSquares = 0f;
        for (int i = 0; i < weights.getNumberOfRows(); i++) {
            for (int j = 0; j < weights.getNumberOfColumns(); j++) {
                sumOfSquares += weights.getValue(i, j) * weights.getValue(i, j);
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    private void scaleWeightsBy(Weights weights, double scale) {
        for (int i = 0; i < weights.getNumberOfRows(); i++) {
            for (int j = 0; j < weights.getNumberOfColumns(); j++) {
                weights.setValue(i, j, (float) (weights.getValue(i, j) * scale));
            }
        }
    }

    @Override
    public void updateWeightsWithGradients(ArrayList<WeightsArray> gradientsFromEachTask, float learningRate) {
        WeightsArray totalGradient = calculateSumOfGradientArray(gradientsFromEachTask);
        for (int i = 0; i < totalGradient.size(); i++) {
            weightsArray.get(i).addOtherWeightScaled(totalGradient.get(i), -1*learningRate/gradientsFromEachTask.size());
        }
        regularizeWeights();
    }

    private WeightsArray calculateSumOfGradientArray(ArrayList<WeightsArray> gradientsFromEachTask) {
        WeightsArray result = gradientsFromEachTask.get(0);
        for (int i = 1; i < gradientsFromEachTask.size(); i++) {
            addWeightArrayToTotal(gradientsFromEachTask.get(i), result);
        }
        return result;
    }

    private void addWeightArrayToTotal(WeightsArray weightsArray, WeightsArray result) {
        for (int i = 0; i < weightsArray.size(); i++) {
            result.get(i).addOtherWeight(weightsArray.get(i));
        }
    }
}
