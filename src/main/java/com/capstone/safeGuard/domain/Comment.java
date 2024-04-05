package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
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
    private Long comment_id;

    @ManyToOne
    private Notice notice;

    @ManyToOne
    private Member commentator;

    private String comment;
    private LocalDateTime createdAt;
}
