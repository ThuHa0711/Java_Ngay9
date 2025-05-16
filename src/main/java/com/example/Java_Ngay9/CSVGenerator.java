package com.example.Java_Ngay9;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CSVGenerator {
    private static final String[] NAMES = {"Alice", "Bob", "Charlie", "David", "Emma"};
    private static final String[] COMMENTS = {
            "Great service", "Satisfactory", "Not as expected", "Amazing experience", "Could be better"
    };
    private static final String FILE_PATH = "feedback.csv";

    public static void generateCSV(int numOfRecords) {
        Random random = new Random();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.append("customerId,name,rating,comment\n");
            for (int i = 1; i <= numOfRecords; i++) {
                int customerId = 1000 + i;
                String name = NAMES[random.nextInt(NAMES.length)];
                int rating = random.nextInt(5) + 1;
                String comment = COMMENTS[random.nextInt(COMMENTS.length)];
                writer.append(String.format("%d,%s,%d,\"%s\"\n", customerId, name, rating, comment));
            }
            System.out.println("File feedback.csv đã được tạo thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo file CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        generateCSV(10000);
    }
}
