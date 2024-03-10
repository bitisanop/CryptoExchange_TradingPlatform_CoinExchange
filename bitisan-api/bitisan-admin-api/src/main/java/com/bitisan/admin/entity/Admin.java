package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.bitisan.constant.CommonStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 管理员
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    private String email;

    /**
     * 可用状态 0 正常  1非法
     */
    private CommonStatus enable;

    /**
     * 最后登录ip
     */
    private String lastLoginIp;

    /**
     * 用户最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 联系号码
     */
    private String mobilePhone;

    private String password;

    private String qq;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色
     */
    private Long roleId;

    /**
     * 状态 0 正常 1非法
     */
    private Integer status = 0 ;

    /**
     * 用户登录名
     */
    private String username;

    private Long departmentId;

    @TableField(exist = false)//非数据库字段
    private String roleName;

    @TableField(exist = false)//非数据库字段
    private String departmentName;

}
