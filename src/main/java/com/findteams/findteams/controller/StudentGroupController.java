package com.findteams.findteams.controller;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.service.StudentGroupService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.HashMap;
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
public HashMap<String, Object> createGroup(@RequestParam String name,
                                           @RequestParam String purpose,
                                           @RequestParam(name = "isPublic", defaultValue = "false") boolean isPublic,
                                           Principal principal) {
    StudentProfileDetails owner = studentRepo.findByUsername(principal.getName());
    StudentGroup group = groupService.createGroup(name, owner.getId(),isPublic, purpose);

    var response = new HashMap<String, Object>();
    response.put("id", group.getId());
    response.put("name", group.getName());
    response.put("purpose", group.getPurpose());
    response.put("ownerId", group.getOwnerId());
    response.put("isPublic", group.isPublicGroup());

    var members = group.getMemberIds().stream()
            .map(id -> {
                var m = studentRepo.findById(id).orElse(null);
                if (m == null) return null;
                var map = new HashMap<String, Object>();
                map.put("id", m.getId());
                map.put("name", m.getName());
                return map;
            })
            .filter(m -> m != null)
            .toList();

    response.put("memberIds", members);
    return response; // ✅ Same structure as /myGroups
}


    @GetMapping("/myGroups")
@Transactional
public List<HashMap<String, Object>> myGroups(Principal principal) {
    StudentProfileDetails student = studentRepo.findByUsername(principal.getName());

    return studentGroupRepo.findAll().stream()
            .filter(g -> g.getMemberIds().contains(student.getId()))
            .map(g -> {
                var response = new HashMap<String, Object>();
                response.put("id", g.getId());
                response.put("name", g.getName());
                response.put("purpose", g.getPurpose());
                response.put("ownerId", g.getOwnerId());

                // Return full member profile for each member
                var members = g.getMemberIds().stream()
                        .map(memberId -> studentRepo.findById(memberId).orElse(null))
                        .filter(m -> m != null)
                        .map(m -> {
                            var sm = new HashMap<String, Object>();
                            sm.put("id", m.getId());
                            sm.put("name", m.getName());
                            sm.put("course", m.getCourse());
                            sm.put("cgpa", m.getCgpa());
                            sm.put("hostellerOrDayscholar", m.getHostellerOrDayscholar());
                            sm.put("preferences", m.getPreferences());
                            sm.put("summary", m.getResume() != null ? m.getResume().getSummary() : null);
                            return sm;
                        })
                        .toList();

                response.put("memberIds", members);
                return response;
            })
            .toList();
}


// ✅ Re
@DeleteMapping("/{groupId}/remove/{studentId}")
@Transactional
public StudentGroup removeStudent(@PathVariable Long groupId,
                                  @PathVariable Long studentId,
                                  Principal principal) {

    StudentProfileDetails requester = studentRepo.findByUsername(principal.getName());
    return groupService.removeStudent(groupId, studentId, requester.getId());
}



    // ✅ Delete group endpoint
    @DeleteMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable Long groupId,
                              Principal principal) {
        StudentProfileDetails requester = studentRepo.findByUsername(principal.getName());
        groupService.deleteGroup(groupId, requester.getId());
        return "Group deleted successfully.";
    }

    // ✅ Leave group endpoint
@PostMapping("/{groupId}/leave")
@Transactional
public String leaveGroup(@PathVariable Long groupId, Principal principal) {
    StudentProfileDetails student = studentRepo.findByUsername(principal.getName());
    groupService.leaveGroup(groupId, student.getId());
    return "Left group successfully.";
}




}