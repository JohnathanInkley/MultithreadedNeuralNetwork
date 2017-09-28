package Infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ResultFileLogger implements ResultLogger {

    private static final String epochLabel = "Epoch: ";
    private static final String classificationLabel = ", Classification rate: ";
    private static final String percentageLabel = "%\n";

    private final Path trainingResultsFilePath;
    private final Path validationResultsFilePath;

    public ResultFileLogger(String trainingResultsFileLocation, String validationResultsFileLocation) {
        try {
            trainingResultsFilePath = Paths.get(trainingResultsFileLocation);
            validationResultsFilePath = Paths.get(validationResultsFileLocation);
            Files.write(trainingResultsFilePath, "".getBytes());
            Files.write(validationResultsFilePath, "".getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Files could not be created");
        }
    }

    public void writeTrainingResult(int epochNumber, ArrayList<Boolean> results) {
        writeResult(epochNumber, results, trainingResultsFilePath);
    }


    public void writeValidationResult(int epochNumber, ArrayList<Boolean> results) {
        writeResult(epochNumber, results, validationResultsFilePath);
    }

    private void writeResult(int epochNumber, ArrayList<Boolean> results, Path filePath) {
        try {
            String classificationResult = String.format("%.2f", getClassificationRate(results));
            System.out.println(filePath.toString() + ": " + epochNumber + " and classification rate is " + classificationResult + "%");
            String line = epochLabel + epochNumber + classificationLabel + classificationResult + percentageLabel;
            Files.write(filePath, line.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new RuntimeException(filePath + "could not be written to");
        }
    }

    private double getClassificationRate(ArrayList<Boolean> results) {
        long numberTrue = results.stream().filter(result -> result == true).count();
        double totalNumberOfResults = (double) results.size();
        return 100*numberTrue/totalNumberOfResults;
    }

}
