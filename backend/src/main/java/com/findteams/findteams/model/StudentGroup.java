package com.findteams.findteams.model;



import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_group")
@Data
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;     // group name
    private Long ownerId;    // student who created
    private int capacity;
    private String purpose;

    @ElementCollection
    private Set<Long> memberIds = new HashSet<>(); // stores student IDs
}
 
