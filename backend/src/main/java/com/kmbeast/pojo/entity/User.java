package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    /** 用户编号 */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户账号 */
    private String account;

    /** 用户昵称 */
    private String username;

    /** 用户密码 */
    private String password;

    /** 用户头像 */
    private String avatar;

    /** 用户邮箱 */
    private String email;

    /** 用户角色：1-用户，2-管理员 */
    private Integer role;

    /** 性别：1-女；2-男 */
    private Integer gender;

    /** 生日 */
    private LocalDate birthday;

    /** 手机号 */
    private String phone;

    /** 状态：0禁用，1启用 */
    private Integer status;

    /** 用户注册时间 */
    private LocalDateTime createTime;
}
