package com.guthub.guthubserver.domain.user.service;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import com.guthub.guthubserver.domain.gut.repository.GutTypeRepository;
import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.dto.*;
import com.guthub.guthubserver.domain.user.entity.SocialProviderType;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.entity.UserRoleType;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.util.JWTUtil; // JWTUtil import 추가
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService extends DefaultOAuth2UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final GutTypeRepository gutTypeRepository;
    private final JWTUtil jwtUtil; // JWTUtil 주입 추가

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, GutTypeRepository gutTypeRepository, JWTUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.gutTypeRepository = gutTypeRepository;
        this.jwtUtil = jwtUtil; // JWTUtil 초기화
    }

    @Transactional
    public Long updateProfile(ProfileUpdateDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        GutType gutType = gutTypeRepository.findByName(dto.gutType())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장 건강 타입입니다: " + dto.gutType()));

        userEntity.updateProfile(dto, gutType);

        // 프로필 업데이트 후, 역할이 TEMP이면 USER로 승격
        if (userEntity.getRoleType() == UserRoleType.TEMP) {
            userEntity.promoteToUser();
        }

        return userEntity.getId();
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto readUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return new ProfileResponseDto(userEntity);
    }

    @Transactional(readOnly = true)
    public Boolean existUser(UserRequestDTO dto) {
        return userRepository.existsByUsername(dto.getUsername());
    }

    @Transactional
    public Long addUser(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }
        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(UserRoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();
        return userRepository.save(entity).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }

    @Transactional
    public Long updateUser(UserRequestDTO dto) throws AccessDeniedException {
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }
        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(dto.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));
        entity.updateUser(dto);
        return userRepository.save(entity).getId();
    }

    @Transactional
    public void deleteUser(UserRequestDTO dto) throws AccessDeniedException {
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();
        boolean isOwner = sessionUsername.equals(dto.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_" + UserRoleType.ADMIN.name());
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }
        userRepository.deleteByUsername(dto.getUsername());
        jwtService.removeRefreshUser(dto.getUsername());
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes;
        String username;
        String email;
        String nickname;
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (registrationId.equals(SocialProviderType.NAVER.name())) {
            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("nickname");
        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {
            attributes = oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        Optional<UserEntity> entityOptional = userRepository.findByUsernameAndIsSocial(username, true);
        UserRoleType roleType;

        if (entityOptional.isPresent()) {
            UserEntity entity = entityOptional.get();
            roleType = entity.getRoleType();
            UserRequestDTO dto = new UserRequestDTO();
            dto.setNickname(nickname);
            dto.setEmail(email);
            entity.updateUser(dto);
            userRepository.save(entity);
        } else {
            roleType = UserRoleType.TEMP; // 신규 사용자는 TEMP 역할 부여
            UserEntity newUserEntity = UserEntity.builder()
                    .username(username)
                    .password("")
                    .isLock(false)
                    .isSocial(true)
                    .socialProviderType(SocialProviderType.valueOf(registrationId))
                    .roleType(roleType)
                    .nickname(nickname)
                    .email(email)
                    .build();
            userRepository.save(newUserEntity);
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleType.name()));
        return new CustomOAuth2User(attributes, authorities, username);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO readUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));
        return new UserResponseDTO(username, entity.getIsSocial(), entity.getNickname(), entity.getEmail());
    }

    @Transactional
    public String completeSocialSignUp( ProfileUpdateDto dto) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        GutType gutType = gutTypeRepository.findByName(dto.gutType())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장 건강 타입입니다: " + dto.gutType()));

        userEntity.updateProfile(dto, gutType);

        userEntity.promoteToUser(); // TEMP -> USER 역할로 승격

        // 3. 새로운 정식 Access Token 생성 및 반환
        return jwtUtil.createJWT(userEntity.getUsername(), userEntity.getRoleType().name(), true);
    }
}
