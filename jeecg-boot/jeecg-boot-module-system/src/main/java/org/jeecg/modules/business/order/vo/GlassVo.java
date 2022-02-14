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
public class GlassVo {
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
   * 颜色
   */
  @Excel(name = "颜色", width = 15)
  private String color;
}
