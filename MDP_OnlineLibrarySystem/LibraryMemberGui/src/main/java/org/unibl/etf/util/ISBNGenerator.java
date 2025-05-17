package org.unibl.etf.util;

import java.util.Random;

public class ISBNGenerator {
    public static String generateSimulatedISBN() {
        Random random = new Random();

        int eanPrefix = 978;
        int group = random.nextInt(10_000);
        int publisher = random.nextInt(1_000_000);
        int title = random.nextInt(10_000);
        int checkDigit = random.nextInt(10);

        return String.format("%d-%d-%d-%d-%d", eanPrefix, group, publisher, title, checkDigit);
    }
}
