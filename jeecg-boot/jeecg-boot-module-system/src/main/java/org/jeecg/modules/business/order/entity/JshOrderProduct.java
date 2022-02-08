package org.jeecg.modules.business.order.entity;

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
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@ApiModel(value = "jsh_order_product对象", description = "jsh_order_product")
@Data
@TableName("jsh_order_product")
public class JshOrderProduct implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
  @TableId(type = IdType.ASSIGN_ID)
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
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
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
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
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
  @Excel(name = "客户id", width = 15)
  @ApiModelProperty(value = "客户id")
  private java.lang.Long customerId;
  /**
   * 订单ID
   */
  @ApiModelProperty(value = "订单ID")
  private java.lang.Long orderId;
  /**
   * 产品id
   */
  @Excel(name = "产品id", width = 15)
  @ApiModelProperty(value = "产品id")
  private java.lang.Long productId;
  /**
   * 宽
   */
  @Excel(name = "宽", width = 15)
  @ApiModelProperty(value = "宽")
  private java.lang.Long width;
  /**
   * 高
   */
  @Excel(name = "高", width = 15)
  @ApiModelProperty(value = "高")
  private java.lang.Long height;
  /**
   * 数量
   */
  @Excel(name = "数量", width = 15)
  @ApiModelProperty(value = "数量")
  private java.lang.Integer num;

  /**
   * 方向:0其他,1双开,2左开,3右开
   */
  @Excel(name = "方向:0其他,1双开,2左开,3右开", width = 15, dicCode = "direction")
  @Dict(dicCode = "direction")
  @ApiModelProperty(value = "方向:0其他,1双开,2左开,3右开")
  private java.lang.Integer direction;

  /**
   * 颜色
   */
  @Excel(name = "颜色", width = 15)
  @ApiModelProperty(value = "颜色")
  private java.lang.String color;

  /**
   * 类型:1抽,2拉,3条
   */
  @Excel(name = "类型:1抽,2拉,3条", width = 15, dicCode = "product_extend")
  @Dict(dicCode = "product_extend")
  @ApiModelProperty(value = "类型:1抽,2拉,3条")
  private java.lang.Integer extendType;

  /**
   * 数量
   */
  @Excel(name = "数量", width = 15)
  @ApiModelProperty(value = "数量")
  private java.lang.Integer extendNum;

  /**
   * 单价
   */
  @Excel(name = "单价", width = 15)
  @ApiModelProperty(value = "单价")
  private java.lang.Long price;
  /**
   * 总价
   */
  @Excel(name = "总价", width = 15)
  @ApiModelProperty(value = "总价")
  private java.lang.Long totalPrice;
}
