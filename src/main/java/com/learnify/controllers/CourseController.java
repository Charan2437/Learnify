package com.learnify.controllers;

import com.learnify.models.Course;
import com.learnify.services.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            System.out.println("Creating course: " + course);
            Course createdCourse = courseService.createCourseWithStudyPlan(course);
            System.out.println("Created course: " + createdCourse);
            return ResponseEntity.ok(createdCourse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error creating course: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error fetching courses: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(course);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403)
                .body("Access denied: Please ensure you are authenticated and have proper permissions");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error fetching course: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> updateCourse(@PathVariable("id") String courseId, @RequestBody Course course) {
        try {
            String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            Course existingCourse = courseService.getCourseById(courseId);
            if (existingCourse == null) {
                return ResponseEntity.notFound().build();
            }
            if (!existingCourse.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body("Access denied: You can only update your own courses");
            }
            // Set the courseId on the Course object
            course.setId(courseId);
            Course updatedCourse = courseService.UpdateCourse(course);
            return ResponseEntity.ok(updatedCourse);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403)
                .body("Access denied: Please ensure you are authenticated and have proper permissions");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error updating course: " + e.getMessage());
        }
    }

    @GetMapping("/mycourses")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getCourseByUserId() {
        try {
            System.out.println("Request Received");
            String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            List<Course> courses = courseService.getCourseByUserId(userId);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error fetching courses: " + e.getMessage());
        }
    }

    public static class UserIdRequest {
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}