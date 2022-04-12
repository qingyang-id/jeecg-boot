package org.jeecg.modules.business.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: jsh_customer
 * @Author: jeecg-boot
 * @Date: 2022-02-03
 * @Version: V1.0
 */
@Data
@TableName("jsh_customer")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jsh_customer对象", description = "jsh_customer")
public class JshCustomer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Excel(name = "客户ID", width = 15)
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private java.lang.Long id;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 客户名字
     */
    @Excel(name = "客户名字", width = 15)
    @ApiModelProperty(value = "客户名字")
    private java.lang.String name;

    /**
     * 性别（1:男 2:女 0:未知）
     */
    @Excel(name = "性别", width = 15, dicCode = "sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 常用地址
     */
    @Excel(name = "常用地址", width = 15)
    @ApiModelProperty(value = "常用地址")
    private java.lang.String address;
    /**
     * 手机
     */
    @Excel(name = "手机", width = 15)
    @ApiModelProperty(value = "手机")
    private java.lang.String phone;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private java.lang.Integer rank;

    /**
     * 状态:1启用,0禁用
     */
    @Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @ApiModelProperty(value = "状态")
    private java.lang.Integer status;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.math.BigInteger tenantId;
}
