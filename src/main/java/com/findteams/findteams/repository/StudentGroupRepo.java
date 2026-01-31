package com.findteams.findteams.repository;


import com.findteams.findteams.model.StudentGroup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentGroupRepo extends JpaRepository<StudentGroup, Long> {
@Query("SELECT g.id FROM StudentGroup g WHERE :userId MEMBER OF g.memberIds")
List<Long> findGroupIdsByMemberId(@Param("userId") Long userId);
// ✅ Correct
List<StudentGroup> findByPublicGroupTrue();





}
 
