package org.jeecg.modules.business.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Data
@ApiModel(value="jsh_orderPage对象", description="jsh_order")
public class JshOrderPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
  private java.math.BigInteger id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**删除状态:0未删除,1删除*/
	@Excel(name = "删除状态:0未删除,1删除", width = 15, dicCode = "1,0")
    @Dict(dicCode = "1,0")
	@ApiModelProperty(value = "删除状态:0未删除,1删除")
    private java.lang.Integer delFlag;
	/**订单编码*/
	@Excel(name = "订单编码", width = 15)
	@ApiModelProperty(value = "订单编码")
    private java.lang.String orderCode;
	/**客户id*/
	@Excel(name = "客户id", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "jsh_customer", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "客户id")
    private java.math.BigInteger customerId;
	/**订单总额*/
	@Excel(name = "订单总额", width = 15)
	@ApiModelProperty(value = "订单总额")
    private java.math.BigInteger totalPrice;
	/**下单时间*/
	@Excel(name = "下单时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "下单时间")
    private java.util.Date orderTime;
	/**地址*/
	@Excel(name = "地址", width = 15)
	@ApiModelProperty(value = "地址")
    private java.lang.String address;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
    private java.lang.String remark;

	/**租户id*/
	@Excel(name = "租户id", width = 15)
	@ApiModelProperty(value = "租户id")
    private java.math.BigInteger tenantId;

	@ExcelCollection(name="jsh_order_product")
	@ApiModelProperty(value = "jsh_order_product")
	private List<JshOrderProduct> jshOrderProductList;

  @ExcelCollection(name="jsh_order_product_extend")
  @ApiModelProperty(value = "jsh_order_product_extend")
  private List<JshOrderProductExtend> jshOrderProductExtendList;

  @ExcelCollection(name="jsh_order_product_detail")
  @ApiModelProperty(value = "jsh_order_product_detail")
  private List<JshOrderProductDetail> jshOrderProductDetailList;

}
