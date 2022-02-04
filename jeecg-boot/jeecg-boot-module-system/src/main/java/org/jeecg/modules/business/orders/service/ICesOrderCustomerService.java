package org.jeecg.modules.business.orders.service;

import org.jeecg.modules.business.orders.entity.CesOrderCustomer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
public interface ICesOrderCustomerService extends IService<CesOrderCustomer> {

	public List<CesOrderCustomer> selectByMainId(String mainId);
}
