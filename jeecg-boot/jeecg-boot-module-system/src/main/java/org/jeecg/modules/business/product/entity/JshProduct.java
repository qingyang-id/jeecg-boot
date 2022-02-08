package org.jeecg.modules.business.product.entity;

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
 * @Description: jsh_product
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
@TableName("jsh_product")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jsh_product对象", description = "jsh_product")
public class JshProduct implements Serializable {
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
   * 名称
   */
  @Excel(name = "名称", width = 15)
  @ApiModelProperty(value = "名称")
  private java.lang.String name;
  /**
   * 商品条码
   */
  @Excel(name = "商品条码", width = 15)
  @ApiModelProperty(value = "商品条码")
  private java.lang.String barCode;
  /**
   * 采购价格
   */
  @Excel(name = "采购价格", width = 15)
  @ApiModelProperty(value = "采购价格")
  private java.lang.Long purchasePrice;
  /**
   * 铝材高差值
   */
  @Excel(name = "铝材高差值", width = 15)
  @ApiModelProperty(value = "铝材高差值")
  private java.lang.Long aluminumHeightDiff;
  /**
   * 铝材宽差值
   */
  @Excel(name = "铝材宽差值", width = 15)
  @ApiModelProperty(value = "铝材宽差值")
  private java.lang.Long aluminumWidthDiff;
  /**
   * 玻璃高差值
   */
  @Excel(name = "玻璃高差值", width = 15)
  @ApiModelProperty(value = "玻璃高差值")
  private java.lang.Long grassHeightDiff;
  /**
   * 玻璃宽差值
   */
  @Excel(name = "玻璃宽差值", width = 15)
  @ApiModelProperty(value = "玻璃宽差值")
  private java.lang.Long grassWidthDiff;
  /**
   * 备注
   */
  @Excel(name = "备注", width = 15)
  @ApiModelProperty(value = "备注")
  private java.lang.String remark;

  /**
   * 状态:1启用,0禁用
   */
  @Excel(name = "状态:1启用,0禁用", width = 15, dicCode = "status")
  @Dict(dicCode = "status")
  @ApiModelProperty(value = "状态:1启用,0禁用")
  private java.lang.Integer status;

  /**
   * 租户id
   */
  @Excel(name = "租户id", width = 15)
  @ApiModelProperty(value = "租户id")
  private java.lang.Long tenantId;
}
