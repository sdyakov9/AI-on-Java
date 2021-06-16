package engine.optimization;

import engine.components.Context;
import engine.components.Neuron;

import java.util.List;

public interface Optimizer {
   default void train(final Context context,
                      final List<Neuron> inputNeurons,
                      final List<Neuron> outputNeurons,
                      final double[][] input,
                      final double[][] expect) {

    }

    void train(final Context context,
               final List<Neuron> inputNeurons,
               final List<Neuron> outputNeurons,
               final double[][] input,
               final double[][] expect,
               final double[][] test,
               final double[][] testAnswers);
}
