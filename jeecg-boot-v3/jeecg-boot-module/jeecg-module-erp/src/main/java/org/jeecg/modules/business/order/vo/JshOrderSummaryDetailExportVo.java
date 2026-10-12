package org.jeecg.modules.business.order.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: jsh_order_summary
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
public class JshOrderSummaryDetailExportVo {
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    private java.lang.String orderCode;

    /**
     * 客户
     */
    @Excel(name = "客户", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    private java.lang.Long customerId;

    /**
     * 下单时间
     */
    @Excel(name = "下单时间", width = 25, format = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date orderTime;

    /**
     * 地址
     */
    @Excel(name = "地址", width = 15)
    private java.lang.String address;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    private java.lang.String remark;

    /**
     * 产品id
     */
    @Excel(name = "产品", width = 15, dictTable = "jsh_product", dicText = "name", dicCode = "id")
    private java.lang.Long productId;

    /**
     * 铝材颜色
     */
    @Excel(name = "铝材颜色", width = 15)
    private java.lang.String color;

    /**
     * 玻璃颜色
     */
    @Excel(name = "玻璃颜色", width = 15)
    private java.lang.String glassColor;

    /**
     * 高
     */
    @Excel(name = "高(mm)", width = 15)
    private java.lang.Long height;
    /**
     * 宽
     */
    @Excel(name = "宽(mm)", width = 15)
    private java.lang.Long width;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    private java.lang.Integer num;

    /**
     * 方向:0其他,1双开,2左开,3右开
     */
    @Excel(name = "方向", width = 15, dicCode = "direction")
    private java.lang.Integer direction;

    /**
     * 类型:1抽,2拉,3条
     */
    @Excel(name = "抽/拉/条", width = 15, dicCode = "product_extend")
    private java.lang.Integer extendType;

    /**
     * 数量
     */
    @Excel(name = "抽/拉/条数量", width = 15)
    private java.lang.Integer extendNum;

    /**
     * 单价(分)
     */
    @Excel(name = "单价(元)", width = 15)
    private java.math.BigDecimal price;

    /**
     * 附加(分)
     */
    @Excel(name = "附加(元)", width = 15)
    private java.math.BigDecimal extendPrice;

    /**
     * 总面积
     */
    @Excel(name = "总面积(m²)", width = 15)
    private java.math.BigDecimal totalArea;

    /**
     * 订单总额
     */
    @Excel(name = "订单总额(元)", width = 15)
    private java.math.BigDecimal totalPrice;
}
