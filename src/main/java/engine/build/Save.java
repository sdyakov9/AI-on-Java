package engine.build;

import java.io.*;

public class Save {
    private Save() { }

    public static void toFile(final Wrapper wrapper, final String path) {
        try(FileOutputStream fileOut = new FileOutputStream(path); ObjectOutputStream outStream = new ObjectOutputStream(fileOut)) {
            outStream.writeObject(wrapper);
            System.out.println("File saved!");
        } catch (IOException  e) {
            e.printStackTrace();
            throw new RuntimeException("Save failed", e);
        }

    }
}
