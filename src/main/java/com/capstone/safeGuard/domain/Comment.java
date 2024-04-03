package com.capstone.safeGuard.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "comment")
public class Comment {
    @Id
    private long comment_id;
    private long notice_id;
    private String commentator_id;
    private String comment;
    private LocalDateTime createdAt;

    @ManyToOne
    private Notice notice;

}
