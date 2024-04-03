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
    private long notice_id;
    private String title;
    private String content;

    public enum nLevel{
        INFO, WARN, FATAL, CALL
    }
    @Column(name="level")
    private nLevel nlevel;

    @ManyToOne
    private Child child;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "notice")
    @JsonIgnore
    private List<Comment> commentList;
}
