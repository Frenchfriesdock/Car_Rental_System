package com.hosiky.domain.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


@Data
public class UserUpdateDto implements Serializable {

    private Long id;

//    @Schema(description = "bcrypt密文")
//    private String password;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "生日")
    private LocalDate birthday;

}
