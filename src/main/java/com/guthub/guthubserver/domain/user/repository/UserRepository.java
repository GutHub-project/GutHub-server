package com.guthub.guthubserver.domain.user.repository;

import com.guthub.guthubserver.domain.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);

    Optional<UserEntity> findByUsernameAndIsSocial(String username, Boolean isSocial);

    @Transactional
    void deleteByUsername(String username);

    Optional<UserEntity> findByUsernameAndIsLock(String username, Boolean isLock);

    Optional<UserEntity> findByEmail(String email);
}
