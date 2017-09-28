package Infrastructure;

import MachineLearning.Operators.OperatorResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputDataReader {

    public static final String FILE_NOT_FOUND_MESSAGE = "Could not find file: ";
    public static final String MALFORMED_FILE_MESSAGE = "Input file malformed";

    private static final String INPUT_DATA_SEPARATOR = ",";

    public ArrayList<OperatorResult> readExamples(String filePath) {
        try {
            List<String> examplesAsStrings = Files.readAllLines(Paths.get(filePath));
            return generateExamplesAsFloatArraysFromCsvExamples(examplesAsStrings);
        } catch (IOException e) {
            throw new RuntimeException(FILE_NOT_FOUND_MESSAGE + filePath);
        }
    }

    private ArrayList<OperatorResult> generateExamplesAsFloatArraysFromCsvExamples(List<String> examplesAsStrings) {
        ArrayList<OperatorResult> result = new ArrayList<>(examplesAsStrings.size());
        for (String csvExample : examplesAsStrings) {
            result.add(getFloatArrayFromCsvExample(csvExample));
        }
        return result;
    }

    private OperatorResult getFloatArrayFromCsvExample(String csvExample) {
        try {
            String[] exampleAsArrayOfStrings = csvExample.split(INPUT_DATA_SEPARATOR);
            float[] exampleAsFloatArray = new float[exampleAsArrayOfStrings.length];
            for (int i = 0; i < exampleAsArrayOfStrings.length; i++) {
                exampleAsFloatArray[i] = Float.parseFloat(exampleAsArrayOfStrings[i]);
            }
            return new OperatorResult(exampleAsFloatArray);
        } catch (Exception e) {
            throw new RuntimeException(MALFORMED_FILE_MESSAGE);
        }
    }

    public ArrayList<Integer> readLabels(String filePath) {
        try {
            List<String> labelsAsStrings = Files.readAllLines(Paths.get(filePath));
            return labelsAsStrings.stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new RuntimeException(FILE_NOT_FOUND_MESSAGE + filePath);
        } catch (Exception e) {
            throw new RuntimeException(MALFORMED_FILE_MESSAGE);
        }

    }
}
