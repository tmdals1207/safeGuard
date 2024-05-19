package com.capstone.safeGuard.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

@ToString
@RequiredArgsConstructor
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long noticeId;
    private String title;
    private String content;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private NoticeLevel noticeLevel;

    @ManyToOne
    private Child child;

    private LocalDateTime createdAt;

    public void setChild(Child childId) {
    }
    public void changeTitle(String title){
        this.title=title;
    }
    public void changeContent(String content){
        this.content=content;
    }


}
