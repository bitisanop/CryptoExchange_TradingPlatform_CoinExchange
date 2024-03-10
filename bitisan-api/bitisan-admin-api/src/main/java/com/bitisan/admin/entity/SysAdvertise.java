package com.bitisan.admin.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysAdvertiseLocation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 轮播图
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysAdvertise implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String serialNumber;

    private String author;

    /**
     * 内容
     */
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 图片链接url
     */
    private String linkUrl;

    /**
     * 系统广告名称
     */
    private String name;

    private String remark;

    private Integer sort;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 状态 0正常  1非法
     */
    private Integer status;

    /**
     * 系统广告位置 0app首页轮播 1pc首页轮播  2pc分类广告
     */
    private Integer sysAdvertiseLocation;

    /**
     * 广告图
     */
    private String url;

    /**
     * 广告语言
     */
    private String lang;


}
