package Infrastructure;

import java.util.ArrayList;

public interface ResultLogger {

    void writeTrainingResult(int epochNumber, ArrayList<Boolean> results);

    void writeValidationResult(int epochNumber, ArrayList<Boolean> results);

}
