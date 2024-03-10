package com.bitisan.user.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月16日
 */
@ApiModel(value = "会员账户")
@Builder
@Data
public class MemberAccount {
    @ApiModelProperty(value = "真实姓名")
    private String realName;
}
