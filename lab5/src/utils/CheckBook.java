package utils;

import java.io.IOException;

import static utils.UtilFunctions.checkBook;

public class CheckBook implements Runnable {
    private static int price;

    public void run() {
        price = -1;
    }

    public int check(String title, int db) throws IOException, InterruptedException {
        price = checkBook(title, db);

        return price;
    }
}
