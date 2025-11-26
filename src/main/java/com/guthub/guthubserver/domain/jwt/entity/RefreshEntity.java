package com.guthub.guthubserver.domain.jwt.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshEntity extends BaseEntity {


    /*
        토큰 검증은 빈번한 작업이므로 불필요한 Join을 피하기 위해 UserEntity와 직접 연결하지 않고 느슨하게 연결
     */
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "refresh", nullable = false, length = 512)
    private String refresh;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


}
