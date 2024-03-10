package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.bitisan.constant.AnnouncementClassification;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 公告表
 * </p>
 *
 * @author markchao
 * @since 2021-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 是否显示 0-不显示 1-显示
     */
    private Boolean isShow;

    /**
     * 是否置顶 0-置顶 1-不置顶(默认)
     */
    private String isTop;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 标题
     */
    private String title;

    /**
     * 语言
     */
    private String lang;
    /**
     * 分类
     */
    private AnnouncementClassification announcementClassification;


}
