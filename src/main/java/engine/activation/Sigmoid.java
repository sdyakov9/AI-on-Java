package engine.activation;

import java.security.Signature;

public class Sigmoid implements ActivationFunction{
    private final boolean limit;

    public Sigmoid(final boolean limit) {
        this.limit = limit;
    }

    public Sigmoid() {
        this(false);
    }

    @Override
    public Double forward(Double x) {
        return 1. / (1. + Math.exp(- x));
    }

    @Override
    public Double backward(final Double error) {
        if (limit && error > 1.) {
            return forward(1.) * (1 - forward(1.));
        } else if(limit && error < -1.) {
            return forward(-1.) * (1 - forward(-1.));
        }
        return forward(error) * (1 - forward(error));
    }
}
