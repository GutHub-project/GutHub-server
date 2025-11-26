package com.guthub.guthubserver.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {


    public interface existGroup {} // 회원 가입시 username 존재 확인
    public interface addGroup {} // 회원 가입시
    public interface passwordGroup {} // 비밀번호 변경시
    public interface updateGroup {} // 회원 수정시
    public interface deleteGroup {} // 회원 삭제시

    @NotBlank(groups = {existGroup.class, addGroup.class, updateGroup.class, deleteGroup.class}, message = "아이디는 필수 입력값입니다.")
    @Size(min = 8, message = "아이디는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", groups = {existGroup.class, addGroup.class}, message = "아이디는 영문/숫자만 가능합니다.")
    private String username;
    @NotBlank(groups = {addGroup.class, passwordGroup.class}, message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;
    @NotBlank(groups = {addGroup.class, updateGroup.class}, message = "닉네임은 필수 입력값입니다.")
    private String nickname;
    @Email(groups = {addGroup.class, updateGroup.class}, message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
