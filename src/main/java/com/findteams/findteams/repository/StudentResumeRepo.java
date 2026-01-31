package com.findteams.findteams.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.findteams.findteams.model.StudentResume;

@Repository
public interface StudentResumeRepo extends JpaRepository<StudentResume, Long> {
    StudentResume findByStudentUsername(String username);
    @EntityGraph(attributePaths = {"student", "summary", "skills"})
    @Query("SELECT r FROM StudentResume r JOIN FETCH r.student")
List<StudentResume> findAllWithStudent();
Optional<StudentResume> findByStudentId(Long studentId);



}
