package org.jeecg.modules.business.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
@Data
@ApiModel(value="jsh_order_productPage对象", description="jsh_order_product")
public class JshOrderProductPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private Integer id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**删除状态:0未删除,1删除*/
	@Excel(name = "删除状态:0未删除,1删除", width = 15)
	@ApiModelProperty(value = "删除状态:0未删除,1删除")
    private Integer delFlag;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
	@ApiModelProperty(value = "客户id")
    private Integer customerId;
	/**订单ID*/
	@Excel(name = "订单ID", width = 15)
	@ApiModelProperty(value = "订单ID")
    private Integer orderId;
	/**产品id*/
	@Excel(name = "产品id", width = 15)
	@ApiModelProperty(value = "产品id")
    private Integer productId;
	/**宽*/
	@Excel(name = "宽", width = 15)
	@ApiModelProperty(value = "宽")
    private Integer width;
	/**高*/
	@Excel(name = "高", width = 15)
	@ApiModelProperty(value = "高")
    private Integer height;
	/**数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
    private Integer num;
	/**方向:0其他,1双开,2左开,3右开*/
	@Excel(name = "方向:0其他,1双开,2左开,3右开", width = 15)
	@ApiModelProperty(value = "方向:0其他,1双开,2左开,3右开")
    private Integer direction;
	/**单价*/
	@Excel(name = "单价", width = 15)
	@ApiModelProperty(value = "单价")
    private Integer price;
	/**总价*/
	@Excel(name = "总价", width = 15)
	@ApiModelProperty(value = "总价")
    private Integer totalPrice;

	@ExcelCollection(name="jsh_order_product_detail")
	@ApiModelProperty(value = "jsh_order_product_detail")
	private List<JshOrderProductDetail> jshOrderProductDetailList;
	@ExcelCollection(name="jsh_order_product_extend")
	@ApiModelProperty(value = "jsh_order_product_extend")
	private List<JshOrderProductExtend> jshOrderProductExtendList;

}
