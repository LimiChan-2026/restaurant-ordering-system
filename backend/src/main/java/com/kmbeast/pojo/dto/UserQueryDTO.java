package com.kmbeast.pojo.dto;

import lombok.Data;

/**
 * 用户列表查询DTO
 */
@Data
public class UserQueryDTO {

    /** 当前页码（从0开始） */
    private Integer current = 0;

    /** 每页条数 */
    private Integer size = 10;

    /** 用户名（模糊查询） */
    private String username;

    /** 角色：1-普通用户，2-管理员 */
    private Integer role;
}
