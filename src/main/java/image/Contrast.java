package image;

import org.opencv.core.Mat;


public class Contrast {
    private static byte paint(double val) {
        int minorVal = (int) Math.round(val);
        minorVal = minorVal > 255 ? 255 : (Math.max(minorVal, 0));
        return (byte) minorVal;
    }

    public static Mat higher(Mat source, double alpha, int beta) throws Throwable {
        Mat workImage = Mat.zeros(source.size(), source.type()); // returns a Matlab-style zero initializer

        byte[] sourceData = new byte[(int) (source.total() * source.channels())];
        source.get(0, 0, sourceData);

        byte[] workImageData = new byte[(int) (workImage.total() * workImage.channels())];

        for (int height = 0; height < source.rows(); height++) {
            for (int width = 0; width < source.cols(); width++) {
                for (int channel = 0; channel < source.channels(); channel++) {
                    double pixelValue = sourceData[(height * source.cols() + width) * source.channels() + channel];
                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;

                    workImageData[(height * source.cols() + width) * source.channels() + channel] = paint(alpha * pixelValue + beta);
                }
            }
        }

        workImage.put(0, 0, workImageData);
        return workImage;
    }
}
