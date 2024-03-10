package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 字典信息表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataDictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 键
     */
    private String bond;

    /**
     * 注解
     */
    private String comment;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 值
     */
    private String value;

    public DataDictionary(){

    }

    public DataDictionary(String bond, String value, String comment) {
        this.bond = bond;
        this.value = value;
        this.comment = comment;
    }
}
