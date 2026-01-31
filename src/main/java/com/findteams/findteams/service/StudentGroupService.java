package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final StudentGroupRepo groupRepo;
    private final MessageRepo messageRepo; // ✅ added

    public StudentGroup createGroup(String name, Long ownerId,boolean isPublic, String purpose) {
        StudentGroup group = new StudentGroup();
        group.setName(name);
        group.setOwnerId(ownerId);
        group.setPublicGroup(isPublic);

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

    // ✅ Remove a student from a group
    public StudentGroup removeStudent(Long groupId, Long studentId, Long requesterId) {
        StudentGroup group = getGroup(groupId);

        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only the group owner can remove members");
        }

        if (group.getOwnerId().equals(studentId)) {
            throw new RuntimeException("Owner cannot be removed. Use leaveGroup instead.");
        }

        if (!group.getMemberIds().contains(studentId)) {
            throw new RuntimeException("Student is not a member of this group");
        }

        group.getMemberIds().remove(studentId);
        return groupRepo.save(group);
    }

    // ✅ Delete group (owner only) — now removes messages too
    @Transactional
    public void deleteGroup(Long groupId, Long requesterId) {
        StudentGroup group = getGroup(groupId);

        if (!group.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Only the group owner can delete the group.");
        }

        // ✅ Delete all messages for this group before deleting the group
        messageRepo.deleteByGroupId(groupId);

        // ✅ Then delete the group
        groupRepo.delete(group);
    }

    // ✅ Leave group — deletes messages if owner leaves (group deleted)
    @Transactional
    public StudentGroup leaveGroup(Long groupId, Long studentId) {
        StudentGroup group = getGroup(groupId);

        if (group.getOwnerId().equals(studentId)) {
            // ✅ Delete group and all messages if owner leaves
            messageRepo.deleteByGroupId(groupId);
            groupRepo.delete(group);
            return null;
        }

        group.getMemberIds().remove(studentId);
        return groupRepo.save(group);
    }
}
