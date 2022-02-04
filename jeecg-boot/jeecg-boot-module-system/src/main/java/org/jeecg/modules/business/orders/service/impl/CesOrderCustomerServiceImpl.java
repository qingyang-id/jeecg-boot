package org.jeecg.modules.business.orders.service.impl;

import org.jeecg.modules.business.orders.entity.CesOrderCustomer;
import org.jeecg.modules.business.orders.mapper.CesOrderCustomerMapper;
import org.jeecg.modules.business.orders.service.ICesOrderCustomerService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
@Service
public class CesOrderCustomerServiceImpl extends ServiceImpl<CesOrderCustomerMapper, CesOrderCustomer> implements ICesOrderCustomerService {
	
	@Autowired
	private CesOrderCustomerMapper cesOrderCustomerMapper;
	
	@Override
	public List<CesOrderCustomer> selectByMainId(String mainId) {
		return cesOrderCustomerMapper.selectByMainId(mainId);
	}
}
