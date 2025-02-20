package com.learnify.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;
    private String userId;
    private String name;
    private int numberOfDays;
    private String startDate;
    private List<String> preferredLearningStyle;
    private List<StudyPlan> studyPlan;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyPlan {
        private String day;
        private List<Task> tasks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Task {
        private String title;
        private String description;
        private String[] resourceUrls;
        private String status; // "completed", "pending", "future"
    }
}
