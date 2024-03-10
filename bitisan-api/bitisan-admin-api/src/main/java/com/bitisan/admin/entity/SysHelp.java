package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysHelpClassification;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统帮助
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysHelp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String author;

    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 是否置顶（0，置顶  1，不置顶（默认）
     */
    private String isTop;

    private Integer sort;

    /**
     * 状态 0正常 1非法
     */
    private CommonStatus status;

    /**
     * 分类
     */
    private SysHelpClassification sysHelpClassification;

    /**
     * 帮助标题
     */
    private String title;

    /**
     * 文章语言
     */
    private String lang;


}
