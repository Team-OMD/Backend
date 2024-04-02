package com.onmydesk.backend.member.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.member.dto.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE user_id =?")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String name;

    @Column(unique = true)
    private String nickname;

    @Column
    private boolean activated;

    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    @ManyToMany
    private Set<Authority> authorities;

    public void update(MemberUpdateRequest request) {
        this.name = request.getName();
        this.nickname = request.getNickname();
    }

    }
