package com.example.Java_Ngay9;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FeedbackProcessor {

    private static final Predicate<CustomerFeedback> isSatisfied = feedback -> feedback.getRating() >= 4;

    private static final Consumer<CustomerFeedback> displayFeedback = System.out::println;

    private static final Function<CustomerFeedback, String> feedbackToString = feedback ->
            String.format("Customer ID: %d, Name: %s, Rating: %d, Comment: %s",
                    feedback.getCustomerId(), feedback.getName(), feedback.getRating(), feedback.getComment());

    private static final Supplier<List<CustomerFeedback>> fallbackData = () -> {
        List<CustomerFeedback> list = new java.util.ArrayList<>();
        list.add(new CustomerFeedback(9999, "Sample User", 5, "Sample Comment"));
        return list;
    };

    // Xử lý phản hồi khách hàng
    public static void processFeedback(List<CustomerFeedback> feedbackList) {
        if (feedbackList == null || feedbackList.isEmpty()) {
            System.out.println("Danh sách phản hồi rỗng. Không có dữ liệu để xử lý.");
            return;
        }

        System.out.println("Số lượng phản hồi đọc được: " + feedbackList.size());

        StringBuilder reportContent = new StringBuilder();

        List<CustomerFeedback> unsatisfiedFeedbacks = feedbackList.stream()
                .filter(feedback -> feedback.getRating() < 4)
                .collect(Collectors.toList());

        reportContent.append("Danh sách phản hồi không hài lòng:\n");
        unsatisfiedFeedbacks.forEach(f -> reportContent.append(feedbackToString.apply(f)).append("\n"));

        System.out.println("Tổng số phản hồi không hài lòng: " + unsatisfiedFeedbacks.size());

        Map<Integer, Long> feedbackCountByRating = feedbackList.stream()
                .collect(Collectors.groupingBy(CustomerFeedback::getRating, Collectors.counting()));

        reportContent.append("\nThống kê số lượng phản hồi theo rating:\n");
        feedbackCountByRating.forEach((rating, count) ->
                reportContent.append("Rating " + rating + ": " + count + " phản hồi\n")
        );

        feedbackCountByRating.forEach((rating, count) ->
                System.out.println("Rating " + rating + ": " + count + " phản hồi")
        );

        double averageRating = feedbackList.stream()
                .mapToInt(CustomerFeedback::getRating)
                .average()
                .orElse(0.0);

        reportContent.append("\nTrung bình rating: ").append(averageRating).append("\n");
        System.out.println("Trung bình rating: " + averageRating);

        Map<Boolean, List<CustomerFeedback>> partitionedFeedback = feedbackList.stream()
                .collect(Collectors.partitioningBy(isSatisfied));

        reportContent.append("\nPhản hồi tích cực:\n");
        partitionedFeedback.get(true).forEach(f -> reportContent.append(feedbackToString.apply(f)).append("\n"));

        reportContent.append("\nPhản hồi tiêu cực:\n");
        partitionedFeedback.get(false).forEach(f -> reportContent.append(feedbackToString.apply(f)).append("\n"));

        System.out.println("Số lượng phản hồi tích cực: " + partitionedFeedback.get(true).size());
        System.out.println("Số lượng phản hồi tiêu cực: " + partitionedFeedback.get(false).size());

        reportContent.append("\nKiểm tra phản hồi có comment thiếu:\n");
        feedbackList.forEach(feedback -> {
            Optional<String> optionalComment = Optional.ofNullable(feedback.getComment());
            reportContent.append("ID: ").append(feedback.getCustomerId())
                    .append(" - Bình luận: ").append(optionalComment.orElse("Không có bình luận")).append("\n");
        });

        // Ghi vào file report.txt
        writeToFile("report.txt", reportContent.toString());

        // Xóa file CSV sau khi xử lý xong
        deleteFile("feedback.csv");
    }

    // Hàm ghi dữ liệu vào file
    private static void writeToFile(String fileName, String content) {
        System.out.println("Bắt đầu ghi dữ liệu vào file " + fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            if (content.isEmpty()) {
                System.out.println("Dữ liệu cần ghi vào file rỗng.");
            } else {
                writer.write(content);
                System.out.println("Kết quả đã được ghi vào " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    // Hàm xóa file sau khi xử lý
    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.delete()) {
            System.out.println("File " + fileName + " đã được xóa thành công.");
        } else {
            System.err.println("Không thể xóa file " + fileName);
        }
    }

    public static void main(String[] args) {
        List<CustomerFeedback> feedbacks = CSVReader.readCSV();

        // Nếu dữ liệu rỗng, dùng Supplier để cấp dữ liệu mẫu
        if (feedbacks.isEmpty()) {
            System.out.println("❗ Không tìm thấy dữ liệu, dùng dữ liệu mẫu:");
            feedbacks = fallbackData.get();
        }

        processFeedback(feedbacks);
    }
}
