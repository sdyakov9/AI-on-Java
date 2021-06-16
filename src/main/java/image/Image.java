package image;

import org.checkerframework.checker.units.qual.C;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Image {
    public static BufferedImage resize(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        BufferedImage scaled = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(image, 0, 0, 28, 28, null);
        g2d.dispose();
        return scaled;
    }
}

