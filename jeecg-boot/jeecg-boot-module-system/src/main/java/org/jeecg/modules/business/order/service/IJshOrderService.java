package org.jeecg.modules.business.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.vo.JshOrderPage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface IJshOrderService extends IService<JshOrder> {

	/**
	 * 添加一对多
	 *
	 */
	public void saveMain(JshOrderPage jshOrderPage) ;

	/**
	 * 修改一对多
	 *
	 */
	public void updateMain(JshOrderPage jshOrderPage);

	/**
	 * 删除一对多
	 */
	public void delMain (BigInteger id);

	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

}
