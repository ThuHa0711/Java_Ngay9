package com.example.Java_Ngay9;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FeedbackProcessor {
    // Predicate: Kiểm tra phản hồi có rating >= 4 là hài lòng
    private static final Predicate<CustomerFeedback> isSatisfied = feedback -> feedback.getRating() >= 4;

    // Consumer: Hiển thị thông tin phản hồi
    private static final Consumer<CustomerFeedback> displayFeedback = System.out::println;

    // Function: Chuyển đổi phản hồi thành chuỗi
    private static final Function<CustomerFeedback, String> feedbackToString = feedback ->
            String.format("Customer ID: %d, Name: %s, Rating: %d, Comment: %s",
                    feedback.getCustomerId(), feedback.getName(), feedback.getRating(), feedback.getComment());

    // Supplier: Cung cấp dữ liệu mẫu nếu file rỗng hoặc lỗi
    private static final Supplier<List<CustomerFeedback>> fallbackData = () -> {
        List<CustomerFeedback> list = new ArrayList<>();
        list.add(new CustomerFeedback(9999, "Sample User", 5, "Sample Comment"));
        return list;
    };

    public static void processFeedback(List<CustomerFeedback> feedbackList) {
        // Lọc phản hồi có rating < 4
        List<CustomerFeedback> unsatisfiedFeedbacks = feedbackList.stream()
                .filter(feedback -> feedback.getRating() < 4)
                .collect(Collectors.toList());

        System.out.println("Danh sách phản hồi không hài lòng:");
        unsatisfiedFeedbacks.forEach(displayFeedback);

        //Đếm số lượng phản hồi theo rating
        Map<Integer, Long> feedbackCountByRating = feedbackList.stream()
                .collect(Collectors.groupingBy(CustomerFeedback::getRating, Collectors.counting()));

        System.out.println("\nThống kê số lượng phản hồi theo rating:");
        feedbackCountByRating.forEach((rating, count) ->
                System.out.println("Rating " + rating + ": " + count + " phản hồi")
        );

        // Tính trung bình rating
        double averageRating = feedbackList.stream()
                .mapToInt(CustomerFeedback::getRating)
                .average()
                .orElse(0.0);

        System.out.println("\nTrung bình rating: " + averageRating);

        // Phân loại thành tích cực và tiêu cực
        Map<Boolean, List<CustomerFeedback>> partitionedFeedback = feedbackList.stream()
                .collect(Collectors.partitioningBy(isSatisfied));

        System.out.println("\nPhản hồi tích cực:");
        partitionedFeedback.get(true).forEach(displayFeedback);

        System.out.println("\nPhản hồi tiêu cực:");
        partitionedFeedback.get(false).forEach(displayFeedback);

        // Optional để tránh NullPointerException khi comment bị thiếu
        System.out.println("\nKiểm tra phản hồi có comment thiếu:");
        feedbackList.forEach(feedback -> {
            Optional<String> optionalComment = Optional.ofNullable(feedback.getComment());
            System.out.println("ID: " + feedback.getCustomerId() + " - Bình luận: " +
                    optionalComment.orElse("Không có bình luận"));
        });
    }

    public static void main(String[] args) {
        List<CustomerFeedback> feedbacks = CSVReader.readCSV();

        // Nếu dữ liệu rỗng, dùng Supplier để cấp dữ liệu mẫu
        if (feedbacks.isEmpty()) {
            System.out.println("Không tìm thấy dữ liệu, dùng dữ liệu mẫu:");
            feedbacks = fallbackData.get();
        }

        processFeedback(feedbacks);
    }
}
