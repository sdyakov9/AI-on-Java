package engine.emnist;

import engine.activation.LeakyRelu;
import engine.activation.Sigmoid;
import engine.build.Save;
import engine.build.Wrapper;
import engine.components.ConnectedNeuron;
import engine.components.Context;
import engine.components.InputNeuron;
import engine.components.Neuron;
import engine.components.helpful.Normalization;
import engine.components.helpful.loss.Loss;
import engine.components.helpful.loss.QuadraticLoss;
import engine.optimization.Optimizer;
import engine.optimization.OptimizerProgress;
import engine.optimization.SDGOptimizer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Trainer {
    private static List<Neuron> createLayer(final Supplier<Neuron> neuronSup, final int layerSize) {
        return IntStream.range(0, layerSize).mapToObj(iter -> neuronSup.get()).collect(Collectors.toList());
    }

    private static Wrapper createNetwork() {
        final Random random = new Random();
        final double learningRate = 0.0005;
        final Context context = new Context(learningRate);

        //Create network
        List<Neuron> inputLayer = createLayer(InputNeuron::new, 784);
        List<Neuron> hiddenLayer = createLayer(() -> new ConnectedNeuron.Builder().activationFunction(new LeakyRelu()).context(context).build(), 27);
        List<Neuron> outputLayer = createLayer(() -> new ConnectedNeuron.Builder().activationFunction(new Sigmoid(true)).context(context).build(), 27);

        inputLayer.stream().forEach(inputNeuron -> {
            hiddenLayer.stream().forEach(hiddenNeuron -> {
                inputNeuron.connect(hiddenNeuron, (random.nextDouble() * 2. - 1.) * Math.sqrt(2. / 784.));
            });
        });

        hiddenLayer.stream().forEach(hiddenNeuron -> outputLayer.stream().forEach(outputNeuron -> {
            hiddenNeuron.connect(outputNeuron, (random.nextDouble() * 2. - 1.) / 27.);
        }));

        return new Wrapper.Builder().inputNeurons(inputLayer).outputNeurons(outputLayer).context(context).build();
    }

    public static void downloadAndTrain() {
        final Wrapper wrapper = createNetwork();
        downloadAndTrain(wrapper);
    }

    public static void downloadAndTrain(final Wrapper wrapper) {
        Download.downloadEmnist();

        System.out.println("Loading training data...");
        final double[][] trainImg = loadImages(Download.EMNIST_TRAIN_SET_FILE.toString());
        final double[][] trainLabels = loadLabels(Download.EMNIST_TRAIN_LABELS_FILE.toString());
        System.out.println("Done!");

        System.out.println("Loading test data...");
        final double[][] testImg = loadImages(Download.EMNIST_TEST_SET_FILE.toString());
        final double[][] testLabels = loadLabels(Download.EMNIST_TEST_LABELS_FILE.toString());
        System.out.println("Done!");

        trainNN(wrapper, trainImg, trainLabels, testImg, testLabels);
    }

    private static void trainNN(final Wrapper wrapper, final double[][] trainImages, final double[][] trainLabels,
                                final double[][] testImages, final double[][] testLabels) {
        List<Neuron> inputLayer = wrapper.getInputNeurons();
        List<Neuron> outputLayer = wrapper.getOutputNeuron();

        Context context = wrapper.getContext();
        final Loss loss = new QuadraticLoss();

        @SuppressWarnings("Convert2Lambda")
        final Optimizer optimizer = new SDGOptimizer(loss, 500, new OptimizerProgress() {
            @Override
            public void onProgress(final double loss, final int epoch, final int maxEpoch) {
                final double newLoss = calculateError(inputLayer, outputLayer, testImages, testLabels);
                System.out.printf("[LOSS: %5f, Correct loss: %10f, Epoch: %d of %d]\n", loss, newLoss, epoch, maxEpoch);
            }
        }, 1.1);
        optimizer.train(context, inputLayer, outputLayer, trainImages, trainLabels, testImages, testLabels);

        final Wrapper finalModel = new Wrapper.Builder().inputNeurons(inputLayer).outputNeurons(outputLayer).build();
        Save.toFile(finalModel, Download.TMP_DIR_PATH + "\\tmp\\emnist_result.dj");
    }

    private static double[][] loadImages(final String path) {
        final List<int[][]> trainImagesSource = Reader.getImages(path);
        final double[][] trainImages = new double[trainImagesSource.size() / 5][];
        IntStream.range(0, trainImagesSource.size() / 5).forEach(iter -> {
            trainImages[iter] = convertImages(trainImagesSource.get(iter));
        });
        return Normalization.normalize(trainImages);
    }

    private static double[][] loadLabels(final String path) {
        final int[] trainLabelsSource = Reader.getLabels(path);
        final double[][] trainLabels = new double[trainLabelsSource.length][];
        IntStream.range(1, trainLabels.length).forEach(iter -> {
            trainLabels[iter] = convertLabel(trainLabelsSource[iter]);
        });
        return trainLabels;
    }

    public static double[] convertImages(final int[][] img) {
        return Arrays.stream(img).flatMapToInt(Arrays::stream).mapToDouble(pixel -> pixel).toArray(); // lambda row -> Arrays.stream(row)
    }

    public static double[] convertLabel(final int label) {
        final double[] labels = new double[27];
        labels[label] = 1.;
        return labels;
    }

    public static double calculateError(final List<Neuron> inputNeurons, final List<Neuron> outputNeurons,
                                        final double[][] images, final double[][] labels) {

        List<Double> errors = new ArrayList<>(images.length);
        for (int index = 1; index < images.length; index++) {
            final double[] image = images[index];
            for (int iter = 0; iter < image.length; iter++) {
                inputNeurons.get(iter).forwardSignalReceived(null, image[iter]);
            }

            int answer = 0;
            double probability = -1.;
            int expect = -1;

            for (int iter = 1; iter < 27; iter++) { // try 1
                final double actual = (outputNeurons.get(iter)).getResult();
                if(actual > probability) {
                    probability = actual;
                    answer = iter;
                }

                if (labels[index][iter] > 0) expect = iter;
            }

            if (answer == expect) {
                errors.add(0.);
            } else {
                errors.add(1.);
            }
        }
        return errors.stream().mapToDouble(iter -> iter).average().getAsDouble();
    }
}
