package org.jeecg.modules.business.order.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
@Schema(description = "jsh_order")
public class JshOrderVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private java.lang.String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private java.lang.String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @Schema(description = "订单编号")
    private java.lang.String orderCode;

    /**
     * 客户
     */
    @Excel(name = "客户", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    @Schema(description = "客户id")
    private java.lang.Long customerId;

    /**
     * 下单时间
     */
    @Excel(name = "下单时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下单时间")
    private java.util.Date orderTime;

    /**
     * 总面积
     */
    @Excel(name = "总面积(m²)", width = 15)
    @Schema(description = "总面积(mm²)")
    private java.math.BigDecimal totalArea;

    /**
     * 订单总额
     */
    @Excel(name = "订单总额(元)", width = 15)
    @Schema(description = "订单总额(分)")
    private java.math.BigDecimal totalPrice;

    /**
     * 地址
     */
    @Excel(name = "地址", width = 15)
    @Schema(description = "地址")
    private java.lang.String address;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private java.lang.String remark;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private java.lang.Long tenantId;

    @ExcelCollection(name = "订单明细")
    @Schema(description = "jsh_order_product")
    private List<JshOrderProductVo> jshOrderProductVoList;
}
