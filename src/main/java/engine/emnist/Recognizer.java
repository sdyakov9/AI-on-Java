package engine.emnist;

import engine.build.Wrapper;
import engine.components.Neuron;
import engine.components.helpful.Normalization;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

public class Recognizer {
    public static double[] read(Wrapper wrapper, BufferedImage sourceImage) {
        List<Neuron> input = wrapper.getInputNeurons();
        List<Neuron> output = wrapper.getOutputNeuron();

        double[] arrayImage = Normalization.normalizeImageData(sourceImage);

        IntStream.range(0, arrayImage.length).forEach(iter -> {
            input.get(iter).forwardSignalReceived(null, arrayImage[iter]);
        });

        double[] result = new double[27];

        for (int i = 1; i < 27; i++) {
            result[i] = output.get(i).getResult();
        }

        return result;
    }
}
