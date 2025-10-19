package com.findteams.findteams.repository;

import com.findteams.findteams.model.StudentInvitation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentInvitationRepo extends JpaRepository<StudentInvitation, Long> {
    List<StudentInvitation> findByReceiverId(Long StudentId );
}
