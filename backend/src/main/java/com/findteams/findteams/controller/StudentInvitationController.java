package com.findteams.findteams.controller;

import com.findteams.findteams.dto.StudentInvitationDto;
import com.findteams.findteams.model.StudentInvitation;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.service.StudentInvitationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class StudentInvitationController {

    private final StudentInvitationService invitationService;
    private final StudentRepo studentRepo;
    private final StudentGroupRepo groupRepo;
    @PostMapping("/send")
    public StudentInvitation sendInvitation(@RequestParam Long senderId,
                                            @RequestParam Long receiverId,
                                            @RequestParam Long groupId,
                                            @RequestParam String purpose) {
        return invitationService.sendInvitation(senderId, receiverId, groupId);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<StudentInvitation> acceptInvitation(@PathVariable Long id) {
        return ResponseEntity.ok(invitationService.acceptInvitation(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<StudentInvitation> rejectInvitation(@PathVariable Long id) {
        return ResponseEntity.ok(invitationService.rejectInvitation(id));
    }
    @GetMapping("/myInvitations")
@Transactional
public List<StudentInvitationDto> myInvitations(Principal principal) {
    StudentProfileDetails student = studentRepo.findByUsername(principal.getName());
    List<StudentInvitation> invitations = invitationService.getInvitationsForStudent(student.getId());

    return invitations.stream().map(inv -> {
        StudentInvitationDto dto = new StudentInvitationDto();
        dto.setId(inv.getId());
        dto.setPurpose(inv.getPurpose());
        dto.setStatus(inv.getStatus());

        // Resolve names from IDs
        dto.setSenderName(studentRepo.findById(inv.getSenderId())
                         .map(StudentProfileDetails::getName)
                         .orElse("Unknown"));

        dto.setReceiverName(studentRepo.findById(inv.getReceiverId())
                           .map(StudentProfileDetails::getName)
                           .orElse("Unknown"));

        dto.setGroupName(groupRepo.findById(inv.getGroupId())
                         .map(g -> g.getName())
                         .orElse("Unknown Group"));

        return dto;
    }).toList();
}

}
