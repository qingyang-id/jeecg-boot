package org.jeecg.modules.business.orders.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.business.orders.entity.CesOrderCustomer;
import org.jeecg.modules.business.orders.entity.CesOrderGoods;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: 商城订单表
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
@Data
@ApiModel(value="ces_order_mainPage对象", description="商城订单表")
public class CesOrderMainPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**订单编码*/
	@Excel(name = "订单编码", width = 15)
	@ApiModelProperty(value = "订单编码")
    private java.lang.String orderCode;
	/**下单时间*/
	@Excel(name = "下单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "下单时间")
    private java.util.Date xdDate;
	/**订单总额*/
	@Excel(name = "订单总额", width = 15)
	@ApiModelProperty(value = "订单总额")
    private java.lang.Double money;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
    private java.lang.String remark;

	@ExcelCollection(name="订单商品")
	@ApiModelProperty(value = "订单商品")
	private List<CesOrderGoods> cesOrderGoodsList;
	@ExcelCollection(name="订单客户")
	@ApiModelProperty(value = "订单客户")
	private List<CesOrderCustomer> cesOrderCustomerList;

}
