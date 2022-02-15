package org.jeecg.modules.business.order.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date: 2022-02-05
 * @Version: V1.0
 */
@Data
@ApiModel(value = "jsh_order_productVo对象", description = "jsh_order_product")
public class JshOrderProductVo {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

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
     * 客户id
     */
    @Dict(dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户id")
    private java.lang.Long customerId;
    /**
     * 订单ID
     */
    @Dict(dictTable = "jsh_order", dicText = "order_code", dicCode = "id")
    @ApiModelProperty(value = "订单ID")
    private java.lang.Long orderId;
    /**
     * 产品id
     */
    @Excel(name = "产品", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "jsh_product", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "产品id")
    private java.lang.Long productId;

    /**
     * 铝材颜色
     */
    @Excel(name = "铝材颜色", width = 15)
    @ApiModelProperty(value = "铝材颜色")
    private java.lang.String color;

    /**
     * 玻璃颜色
     */
    @Excel(name = "玻璃颜色", width = 15)
    @ApiModelProperty(value = "玻璃颜色")
    private java.lang.String glassColor;

    /**
     * 高
     */
    @Excel(name = "高(mm)", width = 15)
    @ApiModelProperty(value = "高(mm)")
    private java.lang.Long height;
    /**
     * 宽
     */
    @Excel(name = "宽(mm)", width = 15)
    @ApiModelProperty(value = "宽(mm)")
    private java.lang.Long width;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer num;

    /**
     * 方向:0其他,1双开,2左开,3右开
     */
    @Excel(name = "方向", width = 15, dicCode = "direction")
    @Dict(dicCode = "direction")
    @ApiModelProperty(value = "方向:0其他,1双开,2左开,3右开")
    private java.lang.Integer direction;

    /**
     * 类型:1抽,2拉,3条
     */
    @Excel(name = "抽/拉/条", width = 15, dicCode = "product_extend")
    @Dict(dicCode = "product_extend")
    @ApiModelProperty(value = "类型:0无,1抽,2拉,3条")
    private java.lang.Integer extendType;

    /**
     * 数量
     */
    @Excel(name = "抽/拉/条数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer extendNum;

    /**
     * 单价(分)
     */
    @Excel(name = "单价(元)", width = 15)
    @ApiModelProperty(value = "单价(分)")
    private java.math.BigDecimal price;

    /**
     * 总面积
     */
    @Excel(name = "总面积(m²)", width = 15)
    @ApiModelProperty(value = "总面积(mm²)")
    private java.math.BigDecimal totalArea;

    /**
     * 订单总额
     */
    @Excel(name = "订单总额(元)", width = 15)
    @ApiModelProperty(value = "订单总额(分)")
    private java.math.BigDecimal totalPrice;
}
