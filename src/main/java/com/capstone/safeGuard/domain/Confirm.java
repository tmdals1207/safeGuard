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
@Table(name = "confirm")
public class Confirm  {
    @Id
    private long confirmId;
    private String title;
    private String content;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ConfirmType confirmType;

    @ManyToOne
    private Helping helping_id;

    @ManyToOne
    private Child child;

    private LocalDateTime createdAt;
}
