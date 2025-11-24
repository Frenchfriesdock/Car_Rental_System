package com.hosiky.domain.dto.userDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {
    @Email
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 32)
    private String password;

    private String code;
}
