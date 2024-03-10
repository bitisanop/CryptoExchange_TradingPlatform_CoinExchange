package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author markchao
 * @since 2022-03-20
 */
@ApiModel(value = "币种协议")
@Data
@EqualsAndHashCode(callSuper = false)
public class Coinprotocol implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "协议")
    private Integer protocol;

    /**
     * 协议名称
     */
    @ApiModelProperty(value = "协议名称")
    private String protocolName;

    /**
     * RPCServer
     */
    @ApiModelProperty(value = "RPCServer")
    private String rpcServer;

    /**
     * RPCUser
     */
    @ApiModelProperty(value = "RPCUser")
    private String rpcUser;

    /**
     * RPCPassword
     */
    @ApiModelProperty(value = "RPCPassword")
    private String rpcPassword;

    /**
     * 浏览器
     */
    @ApiModelProperty(value = "浏览器")
    private String browser;

    /**
     * 符号
     */
    @ApiModelProperty(value = "符号")
    private String symbol;

    /**
     * ChainId 链Id
     */
    @ApiModelProperty(value = "链Id")
    private Integer chainId;


}
