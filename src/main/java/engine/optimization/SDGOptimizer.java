package engine.optimization;

import engine.components.Context;
import engine.components.Neuron;
import engine.components.helpful.loss.Loss;

import java.util.List;
import java.util.stream.IntStream;

public class SDGOptimizer implements Optimizer {
    private final Loss loss;
    private final int maxEpoch;
    private final OptimizerProgress lossListener;
    private final double learningAdjust;
    private double currentLoss = Double.MAX_VALUE;

    public SDGOptimizer(final Loss loss, final int maxEpoch, final OptimizerProgress lossListener, final double learningAdjust) {
        this.loss = loss;
        this.maxEpoch = maxEpoch;
        this.lossListener = lossListener;
        this.learningAdjust = learningAdjust;
    }

    public SDGOptimizer(final Loss loss, final int maxEpoch) {
        this(loss, maxEpoch, null, 1.0);
    }

    @Override
    public void train(Context context,
                      List<Neuron> inputNeurons,
                      List<Neuron> outputNeurons,
                      double[][] input,
                      double[][] expect,
                      double[][] testInput,
                      double[][] testExpect) {
        IntStream.range(0, maxEpoch).forEach(epoch -> {
            if (lossListener != null || learningAdjust != 1.) {
                final double loss = calculateLoss(inputNeurons, outputNeurons, input, expect);

                if (lossListener != null) {
                    lossListener.onProgress(loss, epoch, maxEpoch);
                }

                if (learningAdjust != 1. && currentLoss < loss) {
                    System.out.printf("Loss have increased from %f to %f \n", currentLoss, loss);
                    System.out.printf("Learning rate changed from %f to %f\n", context.getLearningRate(),
                            context.getLearningRate() * learningAdjust);

                    context.setLearningRate(context.getLearningRate() * learningAdjust);
                }

                currentLoss = loss;

                IntStream.range(1, input.length).forEach(index -> {
                    trainIteration(inputNeurons, outputNeurons, input[index], expect[index]);
                });
            }
        });
    }

    private void trainIteration(final List<Neuron> inputNeuron,
                                final List<Neuron> outputNeuron,
                                double[] input,
                                double[] expect) {
        IntStream.range(1, input.length).forEach( example -> {
                inputNeuron.get(example).forwardSignalReceived(null, input[example]);
        });

        IntStream.range(1, outputNeuron.size()).forEach(iter -> {
            final double actual = outputNeuron.get(iter).getResult();
            final double expectSol = expect[iter];
            outputNeuron.get(iter).backwardSignalReceived(loss.derivative(actual, expectSol));
        });
    }

    private double calculateLoss(final List<Neuron> inputNeurons,
                                 final List<Neuron> outputNeurons,
                                 double[][] input,
                                 double[][] expect) {
        return IntStream.range(1, input.length).mapToDouble(index -> {
           IntStream.range(0, input[index].length).forEach(iter -> {
               double data = input[index][iter];
               inputNeurons.get(iter).forwardSignalReceived(null, data); //send input data to neuron
           });

           return IntStream.range(0, expect[index].length).mapToDouble(iter -> //get and calculate error with expected data
               loss.error(outputNeurons.get(iter).getResult(), expect[index][iter])
           ).sum();
        }).average().getAsDouble();
    }
}
