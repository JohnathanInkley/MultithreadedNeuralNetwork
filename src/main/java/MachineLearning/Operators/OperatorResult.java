package MachineLearning.Operators;

import MachineLearning.Weights.Weights;
import org.jblas.FloatMatrix;

public class OperatorResult {

    private FloatMatrix dataAsMatrix;
    private int currentSize = 0;

    public OperatorResult(FloatMatrix dataAsMatrix) {
        this.dataAsMatrix = dataAsMatrix;
    }

    public OperatorResult(float[] inputsFloatArray) {
        dataAsMatrix = new FloatMatrix(inputsFloatArray.length, 1, inputsFloatArray);
    }

    public OperatorResult(int numElementsNeeded) {
        dataAsMatrix = FloatMatrix.zeros(numElementsNeeded, 1);
    }

    public void add(float element) {
        dataAsMatrix.put(currentSize, element);
        currentSize++;
    }

    public void add(int position, Float element) {
        dataAsMatrix.put(position, element);
        currentSize++;
    }

    public Float get(int position) {
        return dataAsMatrix.get(position);
    }

    public FloatMatrix getDataAsMatrix() {
        return dataAsMatrix;
    }

    public Integer size() {
        return dataAsMatrix.rows;
    }

    public Weights multiplyToGetMatrix(OperatorResult otherResult) {
        return new Weights(dataAsMatrix.mmul(otherResult.dataAsMatrix.transpose()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperatorResult that = (OperatorResult) o;
        return dataAsMatrix.equals(that.dataAsMatrix);
    }

    @Override
    public int hashCode() {
        return dataAsMatrix.hashCode();
    }

    @Override
    public String toString() {
        return dataAsMatrix.toString();
    }
}
