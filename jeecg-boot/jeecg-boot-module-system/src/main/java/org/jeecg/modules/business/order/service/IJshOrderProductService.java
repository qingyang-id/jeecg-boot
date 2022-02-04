package org.jeecg.modules.business.order.service;

import org.jeecg.modules.business.order.entity.JshOrderProduct;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface IJshOrderProductService extends IService<JshOrderProduct> {

	public List<JshOrderProduct> selectByMainId(BigInteger mainId);
}
