package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.bitisan.constant.AdminModule;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 管理员访问日志
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdminAccessLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String accessIp;

    private String accessMethod;

    private Date accessTime;

    private Long adminId;

    private AdminModule module;

    private String operation;

    private String uri;


}
