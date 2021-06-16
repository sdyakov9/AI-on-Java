import com.google.common.base.Converter;
import engine.build.Read;
import engine.build.Wrapper;
import engine.emnist.Download;
import engine.emnist.Recognizer;
import engine.emnist.Trainer;
import image.Contrast;
import image.Convert;
import image.Filter;

import image.Image;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Throwable {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        BufferedImage img = ImageIO.read(new File("C:\\Users\\sdyak\\Desktop\\z.jpeg"));


        String path = Download.TMP_DIR_PATH + "\\tmp\\teste.dj";
        Wrapper wrapper = Read.fromFile(path);
        double[] result = Recognizer.read(wrapper, img);
        System.out.println(Arrays.toString(result));

        //source = Convert.binarization(source);
        //Imgcodecs.imwrite("C:\\Users\\sdyak\\Desktop\\openCV.jpg", source);
        //Image.segmentation(new File("C:\\Users\\sdyak\\Desktop\\openCV.jpg"));
    }
}
