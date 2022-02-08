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
 * @Description: jsh_order_product_detail
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
@TableName("jsh_order_product_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jsh_order_product_detail对象", description = "jsh_order_product_detail")
public class JshOrderProductDetail implements Serializable {
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
  @Excel(name = "订单ID", width = 15)
  @ApiModelProperty(value = "订单ID")
  private java.lang.Long orderId;
  /**
   * 订单产品表ID
   */
  @Excel(name = "订单产品表ID", width = 15)
  @ApiModelProperty(value = "订单产品表ID")
  private java.lang.Long orderProductId;
  /**
   * 产品id
   */
  @Excel(name = "产品id", width = 15)
  @ApiModelProperty(value = "产品id")
  private java.lang.Long productId;

  /**
   * 类型:1铝材,2玻璃
   */
  @Excel(name = "类型:1铝材,2玻璃", width = 15, dicCode = "1,2")
  @Dict(dicCode = "1,2")
  @ApiModelProperty(value = "类型:1铝材,2玻璃")
  private java.lang.Integer type;

  /**
   * 参考宽度
   */
  @Excel(name = "参考宽度", width = 15)
  @ApiModelProperty(value = "参考宽度")
  private java.lang.Long referenceWidth;
  /**
   * 参考高度
   */
  @Excel(name = "参考高度", width = 15)
  @ApiModelProperty(value = "参考高度")
  private java.lang.Long referenceHeight;
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
   * 颜色
   */
  @Excel(name = "颜色", width = 15)
  @ApiModelProperty(value = "颜色")
  private java.lang.String color;
}
