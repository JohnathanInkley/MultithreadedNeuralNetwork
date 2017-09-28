package MachineLearning.Weights;

import MachineLearning.Operators.OperatorResult;
import org.jblas.FloatMatrix;

public class Weights {

    private FloatMatrix matrix;

    public Weights(int rows, int cols) {
        matrix = FloatMatrix.zeros(rows, cols);
    }

    public Weights(FloatMatrix weightMatrix) {
        matrix = weightMatrix;
    }

    public int getNumberOfRows() {
        return matrix.rows;
    }

    public int getNumberOfColumns() {
        return matrix.columns;
    }

    public void setValue(int row, int col, Float value) {
        matrix.put(row, col, value);
    }

    public Float getValue(int row, int col) {
        return matrix.get(row, col);
    }

    public OperatorResult multiply(OperatorResult input) {
        return new OperatorResult(matrix.mmul(input.getDataAsMatrix()));
    }

    public OperatorResult transposeThenMultiply(OperatorResult input) {
        return new OperatorResult(input.getDataAsMatrix().transpose().mmul(matrix).transpose());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weights weights = (Weights) o;

        return matrix.equals(weights.matrix);
    }

    @Override
    public int hashCode() {
        return matrix.hashCode();
    }

    @Override
    public String toString() {
        return matrix.toString();
    }

    public void addOtherWeightScaled(Weights weights, float scale) {
        matrix = matrix.add(weights.matrix.mul(scale));
    }

    public void addOtherWeight(Weights weights) {
        addOtherWeightScaled(weights, 1f);
    }
}
