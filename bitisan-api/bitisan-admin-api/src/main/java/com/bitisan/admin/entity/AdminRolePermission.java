package com.bitisan.admin.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 后台用户权限
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdminRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long ruleId;


}
