package com.findteams.findteams.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="student_resume")
@Data
public class StudentResume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;
    @Column(columnDefinition = "TEXT")
    private String summary;
    @Column(columnDefinition = "jsonb")
    private String skills;
    @OneToOne
    @JoinColumn(name="student_id")
    @JsonIgnore
    private StudentProfileDetails student;
}
