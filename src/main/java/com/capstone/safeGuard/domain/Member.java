package com.capstone.safeGuard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@MappedSuperclass
@DiscriminatorColumn

public class Member {
    @Id
    private String memberId;
    private String name;
    private String password;
    private String email;

    @OneToOne(mappedBy = "member")
    @JsonIgnore
    private Parent parent;

    @OneToOne(mappedBy = "member")
    @JsonIgnore
    private Helper helper;

}
