package com.findteams.findteams.repository;
import com.findteams.findteams.model.StudentProfileDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface StudentRepo extends JpaRepository<StudentProfileDetails,Long>,JpaSpecificationExecutor<StudentProfileDetails>{
    StudentProfileDetails getByCgpa = null;

    StudentProfileDetails findByUsername(String username);

    StudentProfileDetails findByCgpa(String cgpa);
    
    @Query("SELECT DISTINCT s FROM StudentProfileDetails s " +
       "WHERE (:cgpa IS NULL OR s.cgpa = :cgpa) " +
       "AND (:batch IS NULL OR SUBSTRING(s.regNo,1,2) = :batch) " +
       "AND (:course IS NULL OR s.course = :course) " +
       "AND (:hosteller IS NULL OR s.hostellerOrDayscholar = :hosteller) " +
       "AND (:preferences IS NULL OR EXISTS (SELECT p FROM s.preferences p WHERE p IN :preferences))")
List<StudentProfileDetails> searchStudents(
    @Param("cgpa") String cgpa,
    @Param("batch") String batch,
    @Param("course") String course,
    @Param("hosteller") String hosteller,
    @Param("preferences") List<String> preferences
);

    
}
