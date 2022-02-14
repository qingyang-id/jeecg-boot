package org.jeecg.modules.business.order.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date: 2022-02-05
 * @Version: V1.0
 */
@Data
public class JshOrderProductExportVo {
    /**
     * 客户id
     */
    @Excel(name = "客户", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    private Long customerId;

    /**
     * 订单ID
     */
    @Excel(name = "订单编号", width = 15, dictTable = "jsh_order", dicText = "order_code", dicCode = "id")
    private Long orderId;

    /**
     * 产品id
     */
    @Excel(name = "产品", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    private Long productId;

    /**
     * 宽
     */
    @Excel(name = "宽(mm)", width = 15)
    private Long width;

    /**
     * 高
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
     * 铝材颜色
     */
    @Excel(name = "铝材颜色", width = 15)
    private String color;

    /**
     * 玻璃颜色
     */
    @Excel(name = "玻璃颜色", width = 15)
    private String glassColor;

    /**
     * 类型:1抽,2拉,3条
     */
    @Excel(name = "类型", width = 15, dicCode = "product_extend")
    private Integer extendType;

    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    private Integer extendNum;

    /**
     * 单价(分)
     */
    @Excel(name = "单价(元)", width = 15)
    private Long price;


    /**
     * 总面积
     */
    @Excel(name = "总面积(m²)", width = 15)
    private Long totalArea;

    /**
     * 订单总额
     */
    @Excel(name = "订单总额(元)", width = 15)
    private Long totalPrice;

    @ExcelCollection(name = "铝材信息")
    private AluminumVo aluminumVo;

    @ExcelCollection(name = "玻璃信息")
    private GlassVo glassVo;
}
