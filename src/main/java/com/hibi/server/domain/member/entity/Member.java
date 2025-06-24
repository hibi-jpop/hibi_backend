package com.hibi.server.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "members")
@Builder
@SQLDelete(sql = "UPDATE members SET deleted_at = NOW() WHERE id = ?")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private ProviderType provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "profile_url", length = 512)
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleType role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Member of(
            final String email,
            final String password,
            final String nickname,
            final ProviderType provider,
            final String providerId,
            final String profileUrl,
            final UserRoleType role) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileUrl(profileUrl)
                .role(role)
                .build();
    }

    public void updateNickname(@NotBlank String nickname) {
        this.nickname = nickname;
    }

    public void updatePasswordHash(@NotBlank String password) {
        this.password = password;
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}