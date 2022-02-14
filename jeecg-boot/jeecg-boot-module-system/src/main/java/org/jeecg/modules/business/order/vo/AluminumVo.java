package org.jeecg.modules.business.order.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: jsh_order_product_detail
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
public class AluminumVo {
  /**
   * 参考宽度(mm)
   */
  @Excel(name = "参考宽度(mm)", width = 15)
  private Long referenceWidth;

  /**
   * 参考高度(mm)
   */
  @Excel(name = "参考高度(mm)", width = 15)
  private Long referenceHeight;

  /**
   * 宽(mm)
   */
  @Excel(name = "宽(mm)", width = 15)
  private Long width;

  /**
   * 高(mm)
   */
  @Excel(name = "高(mm)", width = 15)
  private Long height;

  /**
   * 数量
   */
  @Excel(name = "数量", width = 15)
  private Integer num;

  /**
   * 方向:0其他,1双开,2左开,3右开
   */
  @Excel(name = "方向", width = 15, dicCode = "direction")
  private Integer direction;

  /**
   * 颜色
   */
  @Excel(name = "颜色", width = 15)
  private String color;
}
