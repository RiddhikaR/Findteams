package com.findteams.findteams.controller;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.service.StudentGroupService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudentGroupController {

    private final StudentGroupService groupService;
    private final StudentRepo studentRepo;
    private final StudentGroupRepo studentGroupRepo;

    @PostMapping("/create")
    @Transactional
public StudentGroup createGroup(@RequestParam String name,
                                @RequestParam int capacity,
                                @RequestParam String purpose,
                                Principal principal) {
    StudentProfileDetails owner = studentRepo.findByUsername(principal.getName());
    return groupService.createGroup(name, owner.getId(), capacity, purpose);
}

    @GetMapping("/myGroups")
    @Transactional
public List<StudentGroup> myGroups(Principal principal) {
    StudentProfileDetails student = studentRepo.findByUsername(principal.getName());
    return studentGroupRepo.findAll().stream()
            .filter(g -> g.getMemberIds().contains(student.getId()))
            .toList();
}


}
