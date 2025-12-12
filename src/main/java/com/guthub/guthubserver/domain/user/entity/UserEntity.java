package com.guthub.guthubserver.domain.user.entity;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import com.guthub.guthubserver.domain.user.dto.ProfileUpdateDto;
import com.guthub.guthubserver.domain.user.dto.UserRequestDTO;
import com.guthub.guthubserver.global.entity.BaseEntity;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "age_range")
    private Integer ageRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gut_type_id")
    private GutType gutType;

    @Column(name = "email")
    private String email;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateUser(UserRequestDTO dto) {
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
    }

    public void updateProfile(ProfileUpdateDto dto) {
        this.nickname = dto.nickname();
        this.ageRange = dto.ageRange();
        this.gender = dto.gender();
        this.gutType = dto.gutType();
    }
}
