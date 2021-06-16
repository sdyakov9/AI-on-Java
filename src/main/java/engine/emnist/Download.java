package engine.emnist;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;


public class Download {
    public static final String TMP_DIR_PATH = System.getProperty("java.io.tmpdir");
    private static final String DATASET_PATH = TMP_DIR_PATH + "\\emnist-gzip\\gzip\\";


    private static final File DATASET_ZIP = Paths.get(TMP_DIR_PATH, "emnist-gzip.zip").toFile();
    private static final File EMNIST_TRAIN_SET_ZIP = Paths.get(DATASET_PATH, "emnist-letters-train-images-idx3-ubyte.gz").toFile();
    private static final File EMNIST_TRAIN_LABELS_ZIP = Paths.get(DATASET_PATH, "emnist-letters-train-labels-idx1-ubyte.gz").toFile();
    private static final File EMNIST_TEST_SET_ZIP = Paths.get(DATASET_PATH, "emnist-letters-test-images-idx3-ubyte.gz").toFile();
    private static final File EMNIST_TEST_LABELS_ZIP = Paths.get(DATASET_PATH, "emnist-letters-test-labels-idx1-ubyte.gz").toFile();

    public static final File DATASET = Paths.get(TMP_DIR_PATH, "emnist-gzip").toFile();
    public static final File EMNIST_TRAIN_SET_FILE = Paths.get(TMP_DIR_PATH, "train-images").toFile();
    public static final File EMNIST_TRAIN_LABELS_FILE = Paths.get(TMP_DIR_PATH, "train-labels").toFile();
    public static final File EMNIST_TEST_SET_FILE = Paths.get(TMP_DIR_PATH, "test-images").toFile();
    public static final File EMNIST_TEST_LABELS_FILE = Paths.get(TMP_DIR_PATH, "test-labels").toFile();

    private Download() {
    }

    public static void downloadEmnist() {
        if (EMNIST_TRAIN_SET_FILE.exists() && EMNIST_TRAIN_LABELS_FILE.exists() &&
                EMNIST_TEST_SET_FILE.exists() && EMNIST_TEST_LABELS_FILE.exists()) {
            return;
        }

        final URL dataset;

        try {
            dataset = new URL("http://rds.westernsydney.edu.au/Institutes/MARCS/BENS/EMNIST/emnist-gzip.zip");
            System.out.println(DATASET_PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failure to create URLs.", e);
        }

        try {
            if (!DATASET_ZIP.exists()) {
                System.out.println("Downloading EMNIST Dataset...");
                FileUtils.copyURLToFile(dataset, DATASET_ZIP);
                System.out.println("Done!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failure to download EMNIST Dataset.");
        }

        gzUnzip(EMNIST_TRAIN_SET_ZIP, EMNIST_TRAIN_SET_FILE);
        gzUnzip(EMNIST_TRAIN_LABELS_ZIP, EMNIST_TRAIN_LABELS_FILE);
        gzUnzip(EMNIST_TEST_SET_ZIP, EMNIST_TEST_SET_FILE);
        gzUnzip(EMNIST_TEST_LABELS_ZIP, EMNIST_TEST_LABELS_FILE);

    }

    public static void unzip(final String source, final String dest) {
        try {
            System.out.println("Unzip file...");
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(dest);
            System.out.println("File unzipped!");
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void gzUnzip(final File gzFile, final File dest) {
        System.out.println("Unzipping gz file...");
        final byte[] buffer = new byte[1024];

        try (GZIPInputStream source = new GZIPInputStream(new FileInputStream(gzFile));
             FileOutputStream output = new FileOutputStream(dest)) {

            int length;
            while ((length = source.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unzip gz file have failed.");
        }

        System.out.println("GZ file unzipped!");
    }
}
