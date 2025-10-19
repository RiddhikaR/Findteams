package com.findteams.findteams.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.findteams.findteams.model.StudentResume;

@Repository
public interface StudentResumeRepo extends JpaRepository<StudentResume, Long> {
    StudentResume findByStudentUsername(String username);
}
