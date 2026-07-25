package com.kmbeast.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 更新用户信息DTO
 */
@Data
public class UpdateUserDTO {

    /** 用户昵称 */
    private String username;

    /** 用户头像 */
    private String avatar;

    /** 性别：1-女；2-男 */
    private Integer gender;

    /** 生日 */
    private LocalDate birthday;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;
}
