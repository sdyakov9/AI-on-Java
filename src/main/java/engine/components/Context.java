package engine.components;

import java.io.Serializable;

public class Context implements Serializable {
    private double learningRate;

    public Context(final double learningRate) {
        this.learningRate = learningRate;
    }

    public Context() {
        this(.2);
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
