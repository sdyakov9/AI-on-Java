package engine.components.helpful.loss;

public class LogLoss implements Loss {

    @Override
    public double error(double now, double expect) {
        return expect * Math.log(now);
    }

    @Override
    public double derivative(double now, double expect) {
        return (-Math.log(1-now)) / now;
    }
}
