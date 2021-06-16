package engine.components.helpful.loss;

public class QuadraticLoss implements Loss {

    @Override
    public double error(final double now, final double expect) {
        return Math.pow(expect - now, 2);
    }

    @Override
    public double derivative(final double now, final double expect) {
        return 2 * (expect - now);
    }
}
