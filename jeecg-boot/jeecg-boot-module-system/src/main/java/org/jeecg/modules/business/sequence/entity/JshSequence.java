package org.jeecg.modules.business.sequence.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: jsh_sequence
 * @Author: jeecg-boot
 * @Date: 2022-02-07
 * @Version: V1.0
 */
@Data
@TableName("jsh_sequence")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jsh_sequence对象", description = "jsh_sequence")
public class JshSequence implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 序列名称
   */
  @Excel(name = "序列名称", width = 15)
  @ApiModelProperty(value = "序列名称")
  @TableId
  private java.lang.String seqName;
  /**
   * 最小值
   */
  @Excel(name = "最小值", width = 15)
  @ApiModelProperty(value = "最小值")
  private java.math.BigInteger minValue;
  /**
   * 最大值
   */
  @Excel(name = "最大值", width = 15)
  @ApiModelProperty(value = "最大值")
  private java.math.BigInteger maxValue;
  /**
   * 当前值
   */
  @Excel(name = "当前值", width = 15)
  @ApiModelProperty(value = "当前值")
  private java.math.BigInteger currentVal;
  /**
   * 增长步数
   */
  @Excel(name = "增长步数", width = 15)
  @ApiModelProperty(value = "增长步数")
  private java.math.BigInteger incrementVal;
  /**
   * 备注
   */
  @Excel(name = "备注", width = 15)
  @ApiModelProperty(value = "备注")
  private java.lang.String remark;
}
