package Infrastructure;

import MachineLearning.EndOfBatchMessage;
import MachineLearning.ExampleProcessingResult;
import MachineLearning.LearningRateUpdating.LearningRateUpdater;
import MachineLearning.Weights.EmptyWeightsArray;
import MachineLearning.Weights.WeightDimensions;
import MachineLearning.Weights.WeightUpdater;
import MachineLearning.Weights.WeightsArray;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.ThreadFiber;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EpochCounterTest {

    private EpochCounter underTest;
    private Channel<EndOfBatchMessage> inputChannel;
    private MemoryChannel<UpdateMessage> outputChannel;
    private WeightsArray exampleGradients;
    private CountDownLatch latch;

    private TestLearningRateUpdater testLRUpdater;
    private TestWeightUpdater testWeightUpdater;
    private TestLogger testLogger;


    @Before
    public void setUp() {
        underTest = new EpochCounter(5);
        inputChannel = underTest.getInputChannel();
        outputChannel = new MemoryChannel<>();
        underTest.setOutputChannel(outputChannel);

        exampleGradients = new WeightsArray(1);

        testWeightUpdater = new TestWeightUpdater();
        underTest.setWeightUpdater(testWeightUpdater);
        testLRUpdater = new TestLearningRateUpdater(0.8f);
        underTest.setLearningRateUpdater(testLRUpdater);
        testLogger = new TestLogger();
        underTest.setLogger(testLogger);
    }

    @Test
    public void shouldTakeGradientsAndPassToWeightUpdaterCorrectly() throws InterruptedException {
        ArrayList<ExampleProcessingResult> results = new ArrayList<>();
        results.add(new ExampleProcessingResult(true, exampleGradients));
        results.add(new ExampleProcessingResult(false, exampleGradients));

        latch = getCountDownLatch(1);
        inputChannel.publish(new EndOfBatchMessage(true, results));

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        assertEquals(exampleGradients, testWeightUpdater.gradients.get(0));
        assertEquals(exampleGradients, testWeightUpdater.gradients.get(1));
        assertEquals(0.8f, testWeightUpdater.learningRate, 0.00001);
        assertEquals(2, testWeightUpdater.gradients.size());
    }

    @Test
    public void ifNotEndOfEpochShouldNotUpdateLearningRate() throws InterruptedException {
        assertEquals(0.8f, testLRUpdater.getLearningRate(), 0.00001f);

        latch = getCountDownLatch(1);

        inputChannel.publish(new EndOfBatchMessage(false, new ArrayList<>()));

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        assertEquals(0.8f, testLRUpdater.getLearningRate(), 0.00001f);
    }

    @Test
    public void ifEndOfEpochShouldUpdateLearningRate() throws InterruptedException {
        assertEquals(0.8f, testLRUpdater.getLearningRate(), 0.00001f);

        latch = getCountDownLatch(1);

        inputChannel.publish(new EndOfBatchMessage(true, new ArrayList<>()));

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        assertEquals(0.4f, testLRUpdater.getLearningRate(), 0.00001f);
    }


    @Test
    public void shouldKeepExecutingForGivenNumberOfEpochs() throws InterruptedException {
        latch = getCountDownLatch(14); // 5 training and 4 validation messages

        final EndOfBatchMessage endOfBatchButNotEpochMessage = new EndOfBatchMessage(false, new ArrayList<>());
        final EndOfBatchMessage endOfBatchAndEpochMessage = new EndOfBatchMessage(true, new ArrayList<>());

        for (int i = 0; i < 5; i++) {
            inputChannel.publish(endOfBatchButNotEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
        }

        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldNotSendOutMessageWhenLastEpochDone() throws InterruptedException {
        latch = getCountDownLatch(15);

        final EndOfBatchMessage endOfBatchButNotEpochMessage = new EndOfBatchMessage(false, new ArrayList<>());
        final EndOfBatchMessage endOfBatchAndEpochMessage = new EndOfBatchMessage(true, new ArrayList<>());

        for (int i = 0; i < 5; i++) {
            inputChannel.publish(endOfBatchButNotEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
        }

        assertFalse(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 1000)
    public void shouldSignalShutdownWhenAllEpochsComplete() throws InterruptedException {
        final EndOfBatchMessage endOfBatchButNotEpochMessage = new EndOfBatchMessage(false, new ArrayList<>());
        final EndOfBatchMessage endOfBatchAndEpochMessage = new EndOfBatchMessage(true, new ArrayList<>());

        for (int i = 0; i < 5; i++) {
            inputChannel.publish(endOfBatchButNotEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
            inputChannel.publish(endOfBatchAndEpochMessage);
        }

        underTest.waitForShutdown();
    }


    @Test
    public void shouldBeAbleToStartProgramExecution() throws InterruptedException {
        latch = getCountDownLatch(1);

        underTest.startExecution();

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }


    @Test
    public void shouldGiveTrainingEpochResultsToLogger() throws InterruptedException {
        latch = getCountDownLatch(2);

        assertEquals(0, testLogger.epochNumber);

        ArrayList<ExampleProcessingResult> results = new ArrayList<>();
        results.add(new ExampleProcessingResult(true, new EmptyWeightsArray()));
        results.add(new ExampleProcessingResult(false, new EmptyWeightsArray()));
        results.add(new ExampleProcessingResult(false, new EmptyWeightsArray()));

        underTest.startExecution();
        inputChannel.publish(new EndOfBatchMessage(false, results));
        inputChannel.publish(new EndOfBatchMessage(true, results));

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));

        assertEquals(1, testLogger.epochNumber);
        assertEquals(2, testLogger.getNumCorrect());
        assertEquals(6, testLogger.results.size());
        assertTrue(testLogger.trainingMode);
    }

    @Test
    public void shouldGiveValidationResultsToLogger() throws InterruptedException {
        latch = getCountDownLatch(6);

        ArrayList<ExampleProcessingResult> results = new ArrayList<>();
        results.add(new ExampleProcessingResult(true, new EmptyWeightsArray()));
        results.add(new ExampleProcessingResult(false, new EmptyWeightsArray()));
        results.add(new ExampleProcessingResult(false, new EmptyWeightsArray()));

        underTest.startExecution();
        //epoch 1
        inputChannel.publish(new EndOfBatchMessage(false, results));
        inputChannel.publish(new EndOfBatchMessage(true, results));
        inputChannel.publish(new EndOfBatchMessage(true, results));
        //epoch 2
        inputChannel.publish(new EndOfBatchMessage(false, results));
        inputChannel.publish(new EndOfBatchMessage(true, results));
        inputChannel.publish(new EndOfBatchMessage(true, results));

        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));

        assertEquals(2, testLogger.epochNumber);
        assertEquals(1, testLogger.getNumCorrect());
        assertEquals(3, testLogger.results.size());
        assertFalse(testLogger.trainingMode);

    }

    private CountDownLatch getCountDownLatch(int numberToCountDown) {
        CountDownLatch latch = new CountDownLatch(numberToCountDown);
        ThreadFiber fiber = new ThreadFiber();
        fiber.start();
        outputChannel.subscribe(fiber, (x) -> latch.countDown());
        return latch;
    }

    class TestWeightUpdater extends WeightUpdater {
        public volatile ArrayList<WeightsArray> gradients;
        public volatile Float learningRate;

        @Override
        public void setDimensionArray (ArrayList <WeightDimensions> dimensionsArrayList) {
        }

        @Override
        public void initialiseWeights () {
        }

        @Override
        public void updateWeightsWithGradients (ArrayList <WeightsArray> gradientsFromEachTask, float learningRate) {
            gradients = gradientsFromEachTask;
            this.learningRate = learningRate;
        }
    }

    class TestLearningRateUpdater extends LearningRateUpdater {
        protected TestLearningRateUpdater(float learningRate) {
            super(learningRate);
        }

        @Override
        public void updateLearningRate() {
            learningRate = learningRate/2;
        }
    }

    class TestLogger implements ResultLogger {
        public volatile int epochNumber = 0;
        public volatile ArrayList<Boolean> results;
        public volatile boolean trainingMode;

        @Override
        public synchronized void writeTrainingResult(int epochNumber, ArrayList<Boolean> results) {
            this.epochNumber = epochNumber;
            this.results = results;
            trainingMode = true;
        }

        @Override
        public synchronized void writeValidationResult(int epochNumber, ArrayList<Boolean> results) {
            writeTrainingResult(epochNumber, results);
            trainingMode = false;
        }

        public synchronized int getNumCorrect() {
            return (int) results.stream().filter(result -> result).count();
        }
    }

}