package engine.components;

import com.google.common.util.concurrent.AtomicDouble;
import engine.activation.ActivationFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;

public class ConnectedNeuron implements Neuron {
    private final static int VECTOR_COUNT = 1;
    private final static int VECTOR_ROW = 0;

    private final Context context;
    private final ActivationFunction activationFunction;
    private final Map<Neuron, Integer> indexesOfNeurons = new HashMap<>();
    private RealMatrix backwardConnect = new Array2DRowRealMatrix();
    private RealMatrix inputConnect = new Array2DRowRealMatrix();
    private final Set<Neuron> forwardConnect = new HashSet<>();
    private final String name;
    private final AtomicDouble bias;
    private volatile int signalReceived;
    private volatile double forwardResult;
    private volatile double inputSignalSum;
    private volatile double inputAverage;
    private volatile double forwardActivationFunction;

    public ConnectedNeuron(ActivationFunction activationFunction, final String name, final double bias, final Context context) {
        this.bias = new AtomicDouble(bias);
        this.name = name;
        this.context = context;
        this.activationFunction = activationFunction;
    }

    @Override
    public void forwardSignalReceived(Neuron from, Double value) {
        signalReceived++;
        inputConnect.setEntry(VECTOR_ROW, getNeuronIndex(from), value);
        inputSignalSum += value;

        if (backwardConnect.getColumnDimension() == signalReceived) {
            forwardActivationFunction = backwardConnect.multiply(inputConnect.transpose()).getEntry(0, 0) + bias.get();

            final double signalToSend = activationFunction.forward(forwardActivationFunction);
            forwardResult = signalToSend;

            forwardConnect.forEach(conn -> conn.forwardSignalReceived(ConnectedNeuron.this, signalToSend));

            inputAverage = inputSignalSum / (double) signalReceived;
            inputSignalSum = 0.;
            signalReceived = 0;
        }
    }

    @Override
    public void backwardSignalReceived(final Double value) {
        if (!forwardCalc()) {
            throw new RuntimeException("Forward calculation is not yet completed.");
        }

        final double derivative = activationFunction.backward(forwardActivationFunction);
        final double dz = derivative * value;

        if (value == 0) {
            return;
        }

        if (derivative == 0) {
            return;
        }

        final double dzLearnRate = dz * context.getLearningRate();
        backwardConnect = backwardConnect.add(inputConnect.scalarMultiply(dzLearnRate));
        bias.addAndGet(inputAverage * dz * context.getLearningRate());

        indexesOfNeurons.entrySet().stream().forEach(neuronIndex -> neuronIndex.getKey()
                .backwardSignalReceived(backwardConnect.getEntry(VECTOR_ROW, neuronIndex.getValue()) * dz));
    }

    @Override
    public void addForwardConnection(Neuron neuron) {
        forwardConnect.add(neuron);
    }

    @Override
    public void addBackwardConnection(Neuron neuron, Double weight) {
        int nextNeuron = indexesOfNeurons.size();
        indexesOfNeurons.put(neuron, nextNeuron);

        backwardConnect = addToTensor(backwardConnect, nextNeuron, weight);
        inputConnect = addToTensor(inputConnect, nextNeuron, Double.NaN);
    }

    private RealMatrix addToTensor(final RealMatrix tensor, final int columnIndex, final double value) {
        RealMatrix newTensor = new Array2DRowRealMatrix(VECTOR_COUNT, columnIndex + 1);
        if (tensor.getColumnDimension() != 0) {
            newTensor.setSubMatrix(tensor.getData(), 0, 0);
        }

        newTensor.setEntry(VECTOR_ROW, columnIndex, value);
        return newTensor;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return super.toString();
    }

    @Override
    public double getResult() {
        return forwardResult;
    }

    private boolean forwardCalc() {
        return signalReceived == 0;
    }

    private int getNeuronIndex(final Neuron neuron) {
        return indexesOfNeurons.get(neuron);
    }

    private static boolean brokenValue(final double value) {
        if (value == Double.MAX_VALUE) {
            return true;
        }
        if (value == Double.MIN_VALUE) {
            return true;
        }
        if (Double.isNaN(value)) {
            return true;
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return true;
        }
        if (value == Double.POSITIVE_INFINITY) {
            return true;
        }
        return false;
    }

public static class Builder {
    private double bias = new Random().nextDouble();
    private String name;
    private ActivationFunction activationFunction;
    private Context context;

    public Builder name(final String name) {
        this.name = name;
        return this;
    }

    public Builder context(final Context context) {
        this.context = context;
        return this;
    }

    public Builder bias(final double bias) {
        this.bias = bias;
        return this;
    }

    public Builder activationFunction(final ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
        return this;
    }

    public ConnectedNeuron build() {
        if (activationFunction == null) {
            throw new RuntimeException("ActivationFunction need to be set in a ConnectedNeuron");
        }
        if (context == null) {
            throw new RuntimeException("Context need to be set in a ConnectedNeuron");
        }

        return new ConnectedNeuron(activationFunction, name, bias, context);
    }

}
}
