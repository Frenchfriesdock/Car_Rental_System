package com.hosiky.domain.dto.userDto;

import lombok.Data;

@Data
public class UserUpdatePasswordDto {

    private String id;
    private String password;

    private String newPassword;
    private String confirmPassword;
}
