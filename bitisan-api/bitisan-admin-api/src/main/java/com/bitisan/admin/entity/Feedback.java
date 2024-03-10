package com.bitisan.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 反馈
 * </p>
 *
 * @author markchao
 * @since 2021-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Date createTime;

    private String remark;

    private Long memberId;


}
