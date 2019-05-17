package utils;

import java.io.FileWriter;
import java.io.IOException;

public class WriteOrder {
    public static synchronized void writeOrder(String title) throws IOException {
        try {
            FileWriter fw = new FileWriter("./orders.txt", true);
            fw.write(title + "\n");
            fw.close();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
