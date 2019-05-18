package utils;

import java.io.FileWriter;
import java.io.IOException;

public class WriteOrder {
    public static synchronized void writeOrder(String title) throws IOException, InterruptedException {
        try {
            FileWriter fw = new FileWriter("./orders.txt", true);
            fw.write(title + "\n");
            Thread.sleep(10000);
            fw.close();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
