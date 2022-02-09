package org.jeecg.modules.business.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: jsh_customer_address
 * @Author: jeecg-boot
 * @Date: 2022-02-09
 * @Version: V1.0
 */
@ApiModel(value = "jsh_customer_address对象", description = "jsh_customer_address")
@Data
@TableName("jsh_customer_address")
public class JshCustomerAddress implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
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
  @Excel(name = "删除状态", width = 15, dicCode = "del_flag")
  @TableLogic
  private Integer delFlag;

  /**
   * 客户id
   */
  @Dict(dicCode = "id",dicText = "name",dictTable = "jsh_customer")
  @ApiModelProperty(value = "客户id")
  private java.lang.Long customerId;

  /**
   * 地址
   */
  @Excel(name = "地址", width = 15)
  @ApiModelProperty(value = "地址")
  private String address;
}
