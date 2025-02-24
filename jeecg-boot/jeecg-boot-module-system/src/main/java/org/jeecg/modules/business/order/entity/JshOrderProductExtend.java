package org.jeecg.modules.business.order.entity;

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
 * @Description: jsh_order_product_extend
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
@TableName("jsh_order_product_extend")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jsh_order_product_extend对象", description = "jsh_order_product_extend")
public class JshOrderProductExtend implements Serializable {
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
   * 订单ID
   */
  @Excel(name = "订单编号", width = 15, dictTable = "jsh_order", dicText = "order_code", dicCode = "id")
  @Dict(dictTable = "jsh_order", dicText = "order_code", dicCode = "id")
  @ApiModelProperty(value = "订单ID")
  private java.lang.Long orderId;

  /**
   * 订单产品表ID
   */
  @Excel(name = "订单产品表ID", width = 15)
  @ApiModelProperty(value = "订单产品表ID")
  private java.lang.Long orderProductId;

  /**
   * 类型:1拉,2抽,3条
   */
  @Excel(name = "类型", width = 15, dicCode = "product_extend")
  @Dict(dicCode = "product_extend")
  @ApiModelProperty(value = "类型:0无,1抽,2拉,3条")
  private java.lang.Integer type;

  /**
   * 数量
   */
  @Excel(name = "数量", width = 15)
  @ApiModelProperty(value = "数量")
  private java.lang.Integer num;
}
