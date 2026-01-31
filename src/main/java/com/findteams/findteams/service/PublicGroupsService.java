package com.findteams.findteams.service;



import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.findteams.findteams.dto.PublicGroupsDto;
import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicGroupsService {

    private final StudentGroupRepo studentGroupRepo;
    private final StudentRepo studentRepo;

    @Transactional
    public List<PublicGroupsDto> getAllPublicGroups(Principal principal){
        List<StudentGroup> studentGroups = studentGroupRepo.findByPublicGroupTrue();

        StudentProfileDetails student = studentRepo.findByUsername(principal.getName());
        List<PublicGroupsDto> publicGroupsDtos = new ArrayList<>();
        for(StudentGroup s : studentGroups){
             System.out.println("Group: " + s.getName() + " Members: " + s.getMemberIds());
            
                PublicGroupsDto dto = new PublicGroupsDto();
                dto.setName(s.getName());
                dto.setPurpose(s.getPurpose());
                dto.setId(s.getId());
                publicGroupsDtos.add(dto);
            
        }
        return publicGroupsDtos;
    }
}
