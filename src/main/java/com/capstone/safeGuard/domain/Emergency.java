package com.capstone.safeGuard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long emergencyId;
    private String title;
    private String content;

    @ManyToOne
    private Member senderId;
    @ManyToOne
    private Child child;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "comment")
    @JsonIgnore
    private List<Comment> commentList;
}