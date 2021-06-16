package engine.components;

import java.io.Serializable;

public interface Neuron extends Serializable {
    void forwardSignalReceived(Neuron from, Double value);

    void backwardSignalReceived(Double value);

    default void connect(Neuron neuron, Double value) {
        this.addForwardConnection(neuron);
        neuron.addBackwardConnection(this, value);
    }

    void addForwardConnection(Neuron neuron);

    void addBackwardConnection(Neuron neuron, Double value);

    double getResult();
}
