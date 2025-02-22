package com.learnify.repositories;

import com.learnify.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    // Add custom queries if needed
    List<Course> findByUserId(String userId);
    // List<Course> findByUserEmail(String email);
    Optional<Course> findByIdAndUserId(String id, String userId);
}
