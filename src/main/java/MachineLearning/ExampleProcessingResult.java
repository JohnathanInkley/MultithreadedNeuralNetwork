package MachineLearning;

import MachineLearning.Weights.WeightsArray;

public class ExampleProcessingResult {
    private final boolean classifiedCorrectly;
    private final WeightsArray gradientArray;

    public ExampleProcessingResult(boolean classifiedCorrectly, WeightsArray gradientArray) {
        this.classifiedCorrectly = classifiedCorrectly;
        this.gradientArray = gradientArray;
    }

    public boolean exampleWasClassifiedCorrectly() {
        return classifiedCorrectly;
    }

    public WeightsArray getGradientArray() {
        return gradientArray;
    }
}
