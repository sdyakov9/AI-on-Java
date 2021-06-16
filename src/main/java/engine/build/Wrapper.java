package engine.build;

import engine.components.Context;
import engine.components.Neuron;

import java.io.Serializable;
import java.util.List;

public class Wrapper implements Serializable{
    private final Context context;
    private final List<Neuron> inputNeurons;
    private final List<Neuron> outputNeuron;

    public Wrapper(Context context, List<Neuron> inputNeurons, List<Neuron> outputNeuron) {
        this.context = context;
        this.inputNeurons = inputNeurons;
        this.outputNeuron = outputNeuron;
    }

    public Context getContext() {
        return context;
    }

    public List<Neuron> getInputNeurons() {
        return inputNeurons;
    }

    public List<Neuron> getOutputNeuron() {
        return outputNeuron;
    }

    public static class Builder{
        private Context context;
        private List<Neuron> inputNeurons; // a.k.a input layer
        private List<Neuron> outputNeuron; // a.k.a output layer

        public Builder context(final Context context) {
            this.context = context;
            return this;
        }

        public Builder inputNeurons (List<Neuron> inputNeurons) {
            this.inputNeurons = inputNeurons;
            return this;
        }

        public Builder outputNeurons (List<Neuron> outputNeuron) {
            this.outputNeuron = outputNeuron;
            return this;
        }

        public Wrapper build() {
            return new Wrapper(this.context, this.inputNeurons, this.outputNeuron);
        }
    }
}
