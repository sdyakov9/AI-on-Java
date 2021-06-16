package engine.components;

import java.util.HashSet;
import java.util.Set;

public class InputNeuron implements Neuron {

    private Set<Neuron> connections = new HashSet<>();

    private final String name;

    public InputNeuron(String name) {
        this.name = name;
    }

    public InputNeuron() {
        this(null);
    }

    @Override
    public void forwardSignalReceived(Neuron from, Double value) {
        connections.forEach(n -> n.forwardSignalReceived(this, value));
    }

    @Override
    public void backwardSignalReceived(Double value) {
        // it's input neuron....
    }

    @Override
    public void addForwardConnection(Neuron neuron) {
        connections.add(neuron);
    }

    @Override
    public void addBackwardConnection(Neuron neuron, Double value) {
        throw new RuntimeException("addBackwardConnection function should not be called on InputNeuron");
    }

    @Override
    public double getResult() {
        return 0;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return super.toString();
    }
}
