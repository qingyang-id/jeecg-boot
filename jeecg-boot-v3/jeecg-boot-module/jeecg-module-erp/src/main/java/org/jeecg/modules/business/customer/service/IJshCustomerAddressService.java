package org.jeecg.modules.business.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.customer.entity.JshCustomerAddress;

import java.util.List;

/**
 * @Description: jsh_customer_address
 * @Author: jeecg-boot
 * @Date:   2022-02-09
 * @Version: V1.0
 */
public interface IJshCustomerAddressService extends IService<JshCustomerAddress> {

	public List<JshCustomerAddress> selectByMainId(Long mainId);
}
