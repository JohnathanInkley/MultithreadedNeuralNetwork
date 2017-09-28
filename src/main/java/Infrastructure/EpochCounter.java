package Infrastructure;

import MachineLearning.EndOfBatchMessage;
import MachineLearning.LearningRateUpdating.LearningRateUpdater;
import MachineLearning.Weights.WeightUpdater;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;

public class EpochCounter {

    private final ThreadFiber fiber;
    private Channel<EndOfBatchMessage> inputChannel;
    private Channel<UpdateMessage> outputChannel;

    private WeightUpdater weightUpdater;
    private LearningRateUpdater learningRateUpdater;
    private ResultLogger resultLogger;

    private boolean finishedExecution;
    private int numberTrainingValidationEpochPairsToRunFor;
    private int currentEpochCount;
    ArrayList<Boolean> classificationsFromEpoch;


    public EpochCounter(int numberTrainingValidationEpochPairsToRunFor) {
        setUpEpochCount(numberTrainingValidationEpochPairsToRunFor);
        inputChannel = new MemoryChannel<>();
        fiber = new ThreadFiber();
        fiber.start();
        subscribeToInputChannel();
    }

    private synchronized void setUpEpochCount(int numberTrainingValidationEpochPairsToRunFor) {
        this.numberTrainingValidationEpochPairsToRunFor = numberTrainingValidationEpochPairsToRunFor;
        currentEpochCount = 0;
        classificationsFromEpoch = new ArrayList<>();
        finishedExecution = false;
    }

    private void subscribeToInputChannel() {
        inputChannel.subscribe(fiber, this::processEndOfBatchMessage);
    }


    public void startExecution() {
        publishNewBatchMessageToOutputChannel();
    }

    private void processEndOfBatchMessage(EndOfBatchMessage msg) {
        giveWeightUpdaterArrayOfGradients(msg);
        addBatchArrayOfClassificationsToEpochArray(msg);
        if (msg.isEndOfEpoch()) {
            processEndOfEpochRequirements();
        }
        if (allEpochsCompleted()) {
            signalOperationFinished();
        } else {
            publishNewBatchMessageToOutputChannel();
        }
    }


    private void giveWeightUpdaterArrayOfGradients(EndOfBatchMessage msg) {
        weightUpdater.updateWeightsWithGradients(msg.getGradients(), learningRateUpdater.getLearningRate());
    }

    private void addBatchArrayOfClassificationsToEpochArray(EndOfBatchMessage msg) {
        classificationsFromEpoch.addAll(msg.getClassifications());
    }

    private void processEndOfEpochRequirements() {
        currentEpochCount++;
        learningRateUpdater.updateLearningRate();
        writeResultsToLogger();
    }

    private void writeResultsToLogger() {
        if (currentEpochCount % 2 == 1) {
            resultLogger.writeTrainingResult(currentEpochCount/2 + 1, new ArrayList<>(classificationsFromEpoch));
        } else {
            resultLogger.writeValidationResult(currentEpochCount/2, new ArrayList<>(classificationsFromEpoch));
        }
        classificationsFromEpoch.clear();
    }

    private boolean allEpochsCompleted() {
        return currentEpochCount >= 2*numberTrainingValidationEpochPairsToRunFor;
    }

    private synchronized void signalOperationFinished() {
        finishedExecution = true;
        notifyAll();
    }

    private void publishNewBatchMessageToOutputChannel() {
        outputChannel.publish(new UpdateMessage(weightUpdater.getWeightsArray()));
    }

    public Channel<EndOfBatchMessage> getInputChannel() {
        return inputChannel;
    }

    public void setOutputChannel(MemoryChannel<UpdateMessage> outputChannel) {
        this.outputChannel = outputChannel;
    }

    public void setWeightUpdater(WeightUpdater weightUpdater) {
        this.weightUpdater = weightUpdater;
    }

    public void setLearningRateUpdater(LearningRateUpdater learningRateUpdater) {
        this.learningRateUpdater = learningRateUpdater;
    }

    public void setLogger(ResultLogger resultLogger) {
        this.resultLogger = resultLogger;
    }

    public synchronized void waitForShutdown() throws InterruptedException {
        while (!finishedExecution) {
            wait();
        }
    }

    public synchronized boolean isExecutionFinished() {
        return finishedExecution;
    }

}
