package MachineLearning;

public class ClassificationTarget {

    private final int targetAsPositionInArray;

    public ClassificationTarget(int targetAsPositionInArray) {
        this.targetAsPositionInArray = targetAsPositionInArray;
    }

    public int getTargetAsPositionInArray() {
        return targetAsPositionInArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassificationTarget target = (ClassificationTarget) o;

        return targetAsPositionInArray == target.targetAsPositionInArray;
    }

    @Override
    public int hashCode() {
        return targetAsPositionInArray;
    }
}
