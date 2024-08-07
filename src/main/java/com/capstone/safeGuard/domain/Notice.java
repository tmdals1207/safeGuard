package com.capstone.safeGuard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notice_id;
    private String title;
    private String content;

    @Column(name="level")
    private NoticeLevel noticeLevel;

    @ManyToOne
    private Child child;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "notice")
    @JsonIgnore
    private List<Comment> commentList;
}
