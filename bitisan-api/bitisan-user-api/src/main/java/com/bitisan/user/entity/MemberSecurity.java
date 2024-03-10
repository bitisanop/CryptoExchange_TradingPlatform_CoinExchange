package com.bitisan.user.entity;

import com.bitisan.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月15日
 */
@ApiModel(value = "用户中心认证")
@Builder
@Data
public class MemberSecurity {
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "id")
    private long id;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "是否实名")
    private Integer realVerified = BooleanEnum.IS_FALSE.getCode();
    @ApiModelProperty(value = "邮箱是否认证")
    private Integer emailVerified = BooleanEnum.IS_FALSE.getCode();;
    @ApiModelProperty(value = "手机是否认证")
    private Integer phoneVerified = BooleanEnum.IS_FALSE.getCode();;
    @ApiModelProperty(value = "登录是否认证")
    private Integer loginVerified = BooleanEnum.IS_FALSE.getCode();;
    @ApiModelProperty(value = "资金是否认证")
    private Integer fundsVerified = BooleanEnum.IS_FALSE.getCode();;
    @ApiModelProperty(value = "是否审核中")
    private Integer realAuditing;
    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    @ApiModelProperty(value = "实名拒绝原因")
    private String realNameRejectReason;
    @ApiModelProperty(value = "身份证")
    private String idCard;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "账户是否认证")
    private Integer accountVerified = BooleanEnum.IS_FALSE.getCode();;
    @ApiModelProperty(value = "google状态")
    private Integer googleStatus = BooleanEnum.IS_FALSE.getCode();;
}
