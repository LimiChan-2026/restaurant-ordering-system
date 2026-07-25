package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * 更新用户角色/状态DTO
 */
@Data
public class UpdateUserRoleDTO {

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Integer id;

    /** 角色：1-普通用户，2-管理员 */
    @Min(value = 1, message = "角色只能为1或2")
    @Max(value = 2, message = "角色只能为1或2")
    private Integer role;

    /** 状态：0禁用，1启用 */
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;
}
