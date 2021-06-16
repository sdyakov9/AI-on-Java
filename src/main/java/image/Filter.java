package image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Filter {
    public static void median(BufferedImage img) {

        Color[] pixel = new Color[9];

        int[] R = new int[9];
        int[] G = new int[9];
        int[] B = new int[9];

        for (int width = 1; width < img.getWidth() - 1; width++) {
            for (int height = 1; height < img.getHeight() - 1; height++) {
                pixel[0] = new Color(img.getRGB(width - 1, height - 1));
                pixel[1] = new Color(img.getRGB(width - 1, height));
                pixel[2] = new Color(img.getRGB(width - 1, height + 1));
                pixel[3] = new Color(img.getRGB(width, height + 1));
                pixel[4] = new Color(img.getRGB(width + 1, height + 1));
                pixel[5] = new Color(img.getRGB(width + 1, height));
                pixel[6] = new Color(img.getRGB(width + 1, height - 1));
                pixel[7] = new Color(img.getRGB(width, height - 1));
                pixel[8] = new Color(img.getRGB(width, height));

                for (int m = 0; m < 9; m++) {
                    R[m] = pixel[m].getRed();
                    G[m] = pixel[m].getGreen();
                    B[m] = pixel[m].getBlue();
                }

                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                img.setRGB(width, height, new Color(R[4], G[4], B[4]).getRGB());
            }
        }
        System.out.println("Median filter finished");
    }
}
