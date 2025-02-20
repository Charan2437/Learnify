package com.learnify.controllers;

import com.learnify.models.Course;
import com.learnify.services.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            Course createdCourse = courseService.createCourseWithStudyPlan(course);
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error fetching course: " + e.getMessage());
        }
    }
}