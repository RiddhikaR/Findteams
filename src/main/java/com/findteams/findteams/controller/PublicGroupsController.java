package com.findteams.findteams.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.findteams.findteams.dto.PublicGroupsDto;

import com.findteams.findteams.model.StudentInvitation;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.service.PublicGroupsService;
import com.findteams.findteams.service.StudentInvitationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publicGroups")
@RequiredArgsConstructor
public class PublicGroupsController {

    private final PublicGroupsService publicGroupsService;
    private final StudentRepo studentRepo;
    private final StudentInvitationService invitationService;

    @GetMapping("/getAll")
    @Transactional
    public List<PublicGroupsDto> getAllPublicGroups(Principal principal){
        return publicGroupsService.getAllPublicGroups(principal);
    }
    @Transactional
   @PostMapping("/requestJoinGroup")
public ResponseEntity<?> requestJoinPublicGroup(@RequestParam Long groupId,
                                                Principal principal) {

    StudentProfileDetails student = studentRepo.findByUsername(principal.getName());

    try {
        StudentInvitation invitation =
                invitationService.requestToJoinPublicGroup(student.getId(), groupId);

        return ResponseEntity.ok(invitation);

    } catch (ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason());
    }
}


}
