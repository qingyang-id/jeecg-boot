package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductExtendMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductMapper;
import org.jeecg.modules.business.order.service.IJshOrderService;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderServiceImpl extends ServiceImpl<JshOrderMapper, JshOrder> implements IJshOrderService {

	@Autowired
	private JshOrderMapper jshOrderMapper;
	@Autowired
	private JshOrderProductMapper jshOrderProductMapper;
  @Autowired
  private JshOrderProductExtendMapper jshOrderProductExtendMapper;
  @Autowired
  private JshOrderProductDetailMapper jshOrderProductDetailMapper;

	@Override
	@Transactional
	public void saveMain(JshOrderPage jshOrderPage) {
    JshOrder jshOrder = new JshOrder();
    BeanUtils.copyProperties(jshOrderPage, jshOrder);
    List<JshOrderProduct> jshOrderProductList = jshOrderPage.getJshOrderProductList();
		jshOrderMapper.insert(jshOrder);
		if(jshOrderProductList!=null && jshOrderProductList.size()>0) {
			for(JshOrderProduct entity:jshOrderProductList) {
				//外键设置
				entity.setOrderId(jshOrder.getId());
				jshOrderProductMapper.insert(entity);
			}
		}
    List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderPage.getJshOrderProductExtendList();
    if(jshOrderProductExtendList!=null && jshOrderProductExtendList.size()>0) {
      for(JshOrderProductExtend entity:jshOrderProductExtendList) {

      }
    }
	}

	@Override
	@Transactional
	public void updateMain(JshOrderPage jshOrderPage) {
    JshOrder jshOrder = new JshOrder();
    BeanUtils.copyProperties(jshOrderPage, jshOrder);
    List<JshOrderProduct> jshOrderProductList = jshOrderPage.getJshOrderProductList();

		jshOrderMapper.updateById(jshOrder);

		//1.先删除子表数据
		jshOrderProductMapper.deleteByMainId(jshOrder.getId());

		//2.子表数据重新插入
		if(jshOrderProductList!=null && jshOrderProductList.size()>0) {
			for(JshOrderProduct entity:jshOrderProductList) {
				//外键设置
				entity.setOrderId(jshOrder.getId());
				jshOrderProductMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(BigInteger id) {
		jshOrderProductMapper.deleteByMainId(id);
		jshOrderMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			jshOrderProductMapper.deleteByMainId(new BigInteger(id.toString()));
			jshOrderMapper.deleteById(id);
		}
	}

}
