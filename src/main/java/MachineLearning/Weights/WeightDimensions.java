package MachineLearning.Weights;

import java.util.ArrayList;

public class WeightDimensions {

    public static final String DIMENSION_OUT_OF_BOUNDS_MESSAGE = "Dimension must be in range 0 to ";

    private final ArrayList<Integer> dimensions;

    public WeightDimensions(int... dimensions) {
        this.dimensions = new ArrayList<>();
        for (int i = 0; i < dimensions.length; i++) {
            this.dimensions.add(i, dimensions[i]);
        }
    }

    public int numberOfDimensions() {
        return dimensions.size();
    }

    public int getDimension(int dimension) {
        if (dimension < dimensions.size() && dimension >= 0) {
            return dimensions.get(dimension);
        } else {
            throw new RuntimeException(DIMENSION_OUT_OF_BOUNDS_MESSAGE + (dimensions.size()-1));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightDimensions that = (WeightDimensions) o;

        return dimensions != null ? dimensions.equals(that.dimensions) : that.dimensions == null;
    }

    @Override
    public int hashCode() {
        return dimensions != null ? dimensions.hashCode() : 0;
    }
}
