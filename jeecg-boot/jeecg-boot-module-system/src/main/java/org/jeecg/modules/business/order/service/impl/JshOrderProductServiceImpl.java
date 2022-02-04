package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.mapper.JshOrderProductMapper;
import org.jeecg.modules.business.order.service.IJshOrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderProductServiceImpl extends ServiceImpl<JshOrderProductMapper, JshOrderProduct> implements IJshOrderProductService {

	@Autowired
	private JshOrderProductMapper jshOrderProductMapper;

	@Override
	public List<JshOrderProduct> selectByMainId(BigInteger mainId) {
		return jshOrderProductMapper.selectByMainId(mainId);
	}
}
