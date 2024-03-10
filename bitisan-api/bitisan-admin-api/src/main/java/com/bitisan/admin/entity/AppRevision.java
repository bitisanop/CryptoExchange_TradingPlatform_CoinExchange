package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.bitisan.constant.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * app修订版本表
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppRevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 下载链接
     */
    private String downloadUrl;

    /**
     * 平台标识 0-安卓 1-苹果
     */
    private Platform platform;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private String version;


}
