package engine.build;

import java.io.*;

public class Read {
    private Read() {}

    public static Wrapper fromFile(final String path) {
        try(FileInputStream file = new FileInputStream(path); ObjectInputStream inStream = new ObjectInputStream(file)) {
            return (Wrapper) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Read failed", e);
        }
    }
}
