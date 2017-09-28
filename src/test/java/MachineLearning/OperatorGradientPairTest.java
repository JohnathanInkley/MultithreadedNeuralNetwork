package MachineLearning;

import MachineLearning.Operators.BackwardPassOperators.GradientOperator;
import MachineLearning.Operators.ForwardPassOperators.ForwardPassOperator;
import MachineLearning.Operators.OperatorGradientPair;
import MachineLearning.Operators.OperatorResult;
import MachineLearning.Weights.WeightDimensions;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class OperatorGradientPairTest {

    @Test
    public void shouldTakeForwardPassOperatorAndGenerateBackwardPassOperator() throws InterruptedException {
        CountDownLatch hasOperatorBeenCreated = new CountDownLatch(1);

        OperatorGradientPair underTest = new OperatorGradientPair(
                new ForwardPassOperator(2, 2) {
                    @Override
                    public GradientOperator createGradientOperator() {
                        hasOperatorBeenCreated.countDown();
                        return null;
                    }

                    @Override
                    protected OperatorResult computeResultAssumingCorrectlySized(OperatorResult input) {
                        return null;
                    }

                    @Override
                    public WeightDimensions getDimensions() {
                        return null;
                    }
                }
        );

        assertTrue(hasOperatorBeenCreated.await(10, TimeUnit.MILLISECONDS));
    }
}