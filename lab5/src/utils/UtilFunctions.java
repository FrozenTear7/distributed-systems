package utils;

import java.io.*;

public class UtilFunctions {
    public static synchronized void writeOrder(String title) throws IOException {
        try {
            FileWriter fw = new FileWriter("./orders.txt", true);
            fw.write(title + "\n");
            fw.close();
        } catch (IOException e) {
            throw new IOException();
        }
    }

    public static synchronized int checkBook(String title, int db) throws IOException, InterruptedException {
        int price = -1;

        File db1 = new File("./src/bookstore/database1.txt");
        File db2 = new File("./src/bookstore/database2.txt");

        BufferedReader br = new BufferedReader(new FileReader(db1));

        if (db == 2) {
            new BufferedReader(new FileReader(db2));
        }

        String st;
        while ((st = br.readLine()) != null) {
            if (st.startsWith(title)) {
                price = Integer.parseInt(st.split(" ")[1]);
                break;
            }
        }

        Thread.sleep(10000);

        return price;
    }
}
