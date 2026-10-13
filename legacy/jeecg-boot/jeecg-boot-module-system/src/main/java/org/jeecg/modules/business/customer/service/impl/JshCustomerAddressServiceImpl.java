package org.jeecg.modules.business.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.customer.entity.JshCustomerAddress;
import org.jeecg.modules.business.customer.mapper.JshCustomerAddressMapper;
import org.jeecg.modules.business.customer.service.IJshCustomerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: jsh_customer_address
 * @Author: jeecg-boot
 * @Date:   2022-02-09
 * @Version: V1.0
 */
@Service
public class JshCustomerAddressServiceImpl extends ServiceImpl<JshCustomerAddressMapper, JshCustomerAddress> implements IJshCustomerAddressService {

	@Autowired
	private JshCustomerAddressMapper jshCustomerAddressMapper;

	@Override
	public List<JshCustomerAddress> selectByMainId(Long mainId) {
		return jshCustomerAddressMapper.selectByMainId(mainId);
	}
}
