package MachineLearning.Weights;

import java.util.ArrayList;

public class WeightsArray {

    private final ArrayList<Weights> array;

    public WeightsArray(int size) {
        array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(null);
        }
    }

    public void set(int index, Weights weights) {
        array.set(index, weights);
    }

    public Weights get(int index) {
        return array.get(index);
    }

    public int size() {
        return array.size();
    }
}
