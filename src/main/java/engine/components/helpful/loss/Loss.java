package engine.components.helpful.loss;

public interface Loss {
    double error(final double now, final double expect);
    double derivative (final double now, final double expect);
}
