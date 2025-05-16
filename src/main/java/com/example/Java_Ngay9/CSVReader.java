package com.example.Java_Ngay9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final String FILE_PATH = "feedback.csv";

    public static List<CustomerFeedback> readCSV() {
        List<CustomerFeedback> feedbackList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                int customerId = Integer.parseInt(values[0]);
                String name = values[1];
                int rating = Integer.parseInt(values[2]);
                String comment = values[3].replaceAll("^\"|\"$", "");
                feedbackList.add(new CustomerFeedback(customerId, name, rating, comment));
            }
            System.out.println("Đã đọc thành công dữ liệu từ file feedback.csv!");
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file CSV: " + e.getMessage());
        }
        return feedbackList;
    }

    public static void main(String[] args) {
        List<CustomerFeedback> feedbacks = readCSV();
        feedbacks.forEach(System.out::println);
    }

}
