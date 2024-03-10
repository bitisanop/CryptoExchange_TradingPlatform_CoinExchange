package com.bitisan.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.AuditStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 会员审核信息系表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberApplicationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 审核状态0待审核1审核失败2审核成功
     */
    private Integer auditStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    private String idCard;

    /**
     * 证件 正面
     */
    private String identityCardImgFront;

    /**
     * 证件 手持
     */
    private String identityCardImgInHand;

    /**
     * 证件 反面
     */
    private String identityCardImgReverse;

    /**
     * kyc2级视频认证URL
     */
    private String videoUrl;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 驳回理由
     */
    private String rejectReason;

    /**
     * 认证类型0身份证1护照2驾照
     */
    private Integer type;

    /**
     * 更改时间
     */
    private LocalDateTime updateTime;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * kyc等级
     */
    private Integer kycStatus;

    /**
     * 视频认证随机数
     */
    private String videoRandom;

    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationTime;


}
