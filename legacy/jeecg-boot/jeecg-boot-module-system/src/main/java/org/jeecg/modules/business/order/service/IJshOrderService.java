package org.jeecg.modules.business.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.jeecg.modules.business.order.vo.JshOrderVo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
	public void delMain (Long id);

	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	List<JshOrderVo> getOrderExportList(QueryWrapper<JshOrder> queryWrapper);
}
