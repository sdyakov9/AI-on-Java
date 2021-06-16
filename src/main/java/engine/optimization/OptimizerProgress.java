package engine.optimization;

public interface OptimizerProgress {
    void onProgress(final double loss, final int epoch, final int maxEpoch);
}
