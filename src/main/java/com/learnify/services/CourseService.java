package com.learnify.services;

import com.learnify.models.Course;
import com.learnify.repositories.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public CourseService(CourseRepository courseRepository, GeminiService geminiService, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    public Course createCourseWithStudyPlan(Course course) {
        try {
            // Set the authenticated user's ID
            String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            course.setUserId(userId);

            // Generate study plan using Gemini
            String studyPlanJson = geminiService.generateCourseStudyPlan(course);
            
            // Parse the JSON response into our StudyPlan objects
            TypeReference<List<Course.StudyPlan>> typeRef = new TypeReference<>() {};
            List<Course.StudyPlan> studyPlan = objectMapper.readValue(
                objectMapper.readTree(studyPlanJson).get("studyPlan").toString(), 
                typeRef
            );
            
            // Set the generated study plan
            course.setStudyPlan(studyPlan);
            
            // Save to MongoDB
            return courseRepository.save(course);
        } catch (Exception e) {
            throw new RuntimeException("Error creating course with study plan: " + e.getMessage(), e);
        }
    }

    public List<Course> getAllCourses() {
        String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return courseRepository.findByUserId(userId);
    }

    public Course getCourseById(String id) {
        String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return courseRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }
}
