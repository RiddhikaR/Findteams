package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.repository.StudentGroupRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final StudentGroupRepo groupRepo;

    public StudentGroup createGroup(String name, Long ownerId, int capacity, String purpose) {
    StudentGroup group = new StudentGroup();
    group.setName(name);
    group.setOwnerId(ownerId);
    group.setCapacity(capacity);
    group.setPurpose(purpose);
    group.getMemberIds().add(ownerId);
    return groupRepo.save(group);
}


    public StudentGroup getGroup(Long id) {
        return groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public StudentGroup saveGroup(StudentGroup group) {
        return groupRepo.save(group);
    }
}
