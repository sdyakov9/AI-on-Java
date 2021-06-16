package engine.components.helpful;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Normalization {
    private Normalization() {}

    public static double[][] normalize(final double[][] input) {
        final double max = Arrays.stream(input).flatMapToDouble(Arrays::stream).max().getAsDouble();
        final double min = Arrays.stream(input).flatMapToDouble(Arrays::stream).min().getAsDouble();
        final double mid = min + (max - min) / 2;

        final double[][] out = new double[input.length][];
        IntStream.range(0, input.length).forEach(iter -> {
            out[iter] = Arrays.stream(input[iter]).map(val -> (val - mid) / mid).toArray();
        });

        return out;
    }

    public static double[] normalizeImageData(final BufferedImage image) {
        double[][] image2DArr = new double[image.getWidth()][image.getHeight()];

        IntStream.range(0, image.getWidth()).forEach(width -> {
            IntStream.range(0, image.getHeight()).forEach(height -> {
                image2DArr[width][height] = image.getRGB(width, height);
            });
        });

        IntStream.range(0, image.getWidth()).forEach(width -> {
            IntStream.range(0, image.getWidth()).forEach(height -> {
                image2DArr[width][height] /= 255;
            });
        });

        return Arrays.stream(image2DArr).flatMapToDouble(Arrays::stream).toArray();
    }
}
