package com.guthub.guthubserver.domain.user.dto;


import com.guthub.guthubserver.domain.user.entity.Gender;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private final String nickname;
    private final Integer ageRange;
    private final Gender gender;
    private final GutTypeInProfileDto gutType;

    public ProfileResponseDto(UserEntity entity) {
        this.nickname = entity.getNickname();
        this.ageRange = entity.getAgeRange();
        this.gender = entity.getGender();
        this.gutType = new GutTypeInProfileDto(entity.getGutType());
    }
}
