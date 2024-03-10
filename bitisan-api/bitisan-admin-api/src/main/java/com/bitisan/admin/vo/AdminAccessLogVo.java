package com.bitisan.admin.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.AdminModule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
public class AdminAccessLogVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String accessIp;

    private String accessMethod;

    private Date accessTime;

    private Long adminId;

    private String adminName;

    private String moduleName;

    private Integer module;

    private String operation;

    private String uri;


}
