package com.learnify.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;
import java.util.HashSet;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
}