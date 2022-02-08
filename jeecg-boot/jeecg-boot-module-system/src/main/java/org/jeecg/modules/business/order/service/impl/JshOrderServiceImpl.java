package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductExtendMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductMapper;
import org.jeecg.modules.business.order.service.IJshOrderService;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.jeecg.modules.business.order.vo.JshOrderProductPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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

  void saveSubs(JshOrder jshOrder, List<JshOrderProductPage> jshOrderProductPageList) {
    if(jshOrderProductPageList!=null && jshOrderProductPageList.size()>0) {
      for(JshOrderProductPage jshOrderProductPage:jshOrderProductPageList) {
        JshOrderProduct jshOrderProduct = new JshOrderProduct();
        BeanUtils.copyProperties(jshOrderProductPage, jshOrderProduct);
        // 外键设置
        jshOrderProduct.setOrderId(jshOrder.getId());
        jshOrderProduct.setCustomerId(jshOrder.getCustomerId());
        jshOrderProductMapper.insert(jshOrderProduct);

        List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductPage.getJshOrderProductExtendList();
        if(jshOrderProductExtendList!=null && jshOrderProductExtendList.size()>0) {
          for(JshOrderProductExtend jshOrderProductExtend: jshOrderProductExtendList) {
            // 外键设置
            jshOrderProductExtend.setOrderId(jshOrder.getId());
            jshOrderProductExtend.setOrderProductId(jshOrderProduct.getId());
            jshOrderProductExtendMapper.insert(jshOrderProductExtend);
          }
        }

        List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductPage.getJshOrderProductDetailList();
        if(jshOrderProductDetailList!=null && jshOrderProductDetailList.size()>0) {
          for(JshOrderProductDetail jshOrderProductDetail: jshOrderProductDetailList) {
            // 外键设置
            jshOrderProductDetail.setOrderId(jshOrder.getId());
            jshOrderProductDetail.setOrderProductId(jshOrderProduct.getId());
            jshOrderProductDetail.setCustomerId(jshOrder.getCustomerId());
            jshOrderProductDetail.setColor(jshOrderProduct.getColor());
            jshOrderProductDetailMapper.insert(jshOrderProductDetail);
          }
        }
      }
    }
  }

	@Override
	@Transactional
	public void saveMain(JshOrderPage jshOrderPage) {
    JshOrder jshOrder = new JshOrder();
    BeanUtils.copyProperties(jshOrderPage, jshOrder);
		jshOrderMapper.insert(jshOrder);

    // 2.子表数据重新插入
    this.saveSubs(jshOrder, jshOrderPage.getJshOrderProductPageList());
	}

	@Override
	@Transactional
	public void updateMain(JshOrderPage jshOrderPage) {
    JshOrder jshOrder = new JshOrder();
    BeanUtils.copyProperties(jshOrderPage, jshOrder);
		jshOrderMapper.updateById(jshOrder);

		// 1.先删除子表数据
		jshOrderProductMapper.deleteByMainId(jshOrder.getId());
		jshOrderProductDetailMapper.deleteByMainId(jshOrder.getId());
    jshOrderProductExtendMapper.deleteByMainId(jshOrder.getId());

		// 2.子表数据重新插入
    this.saveSubs(jshOrder, jshOrderPage.getJshOrderProductPageList());
	}

	@Override
	@Transactional
	public void delMain(Long id) {
		jshOrderProductDetailMapper.deleteByMainId(id);
		jshOrderProductExtendMapper.deleteByMainId(id);
		jshOrderProductMapper.deleteByMainId(id);
		jshOrderMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
		  Long mainId = Long.parseLong(id.toString());
      jshOrderProductDetailMapper.deleteByMainId(mainId);
      jshOrderProductExtendMapper.deleteByMainId(mainId);
			jshOrderProductMapper.deleteByMainId(mainId);
			jshOrderMapper.deleteById(id);
		}
	}

}
