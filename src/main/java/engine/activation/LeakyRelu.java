package engine.activation;

public class LeakyRelu implements ActivationFunction{
    private final double y;

    public LeakyRelu(final double y) {
        this.y = y;
    }

    public LeakyRelu() {
        this(0.0001);
    }

    @Override
    public Double forward(final Double x) {
        return x > 0 ? x : y * x;
    }

    @Override
    public Double backward(final Double error) {
        return error > 0. ? 1. : y;
    }
}
