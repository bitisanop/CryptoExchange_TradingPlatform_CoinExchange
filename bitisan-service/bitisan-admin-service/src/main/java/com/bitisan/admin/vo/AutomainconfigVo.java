package com.bitisan.admin.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author markchao
 * @since 2022-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AutomainconfigVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;
    /**
     * 币种
     */
    private Integer coinId;

    /**
     * 币种
     */
    private String coinName;

    /**
     * 最低归集数量
     */
    private BigDecimal minNum;

    /**
     * 币种协议
     */
    private Integer protocol;

    /**
     * 归集地址（前端需要加密）
     */
    private String address;

    private String password;


}
