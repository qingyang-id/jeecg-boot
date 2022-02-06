package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductExtendMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductMapper;
import org.jeecg.modules.business.order.service.IJshOrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
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


  @Autowired
  private JshOrderProductDetailMapper jshOrderProductDetailMapper;
  @Autowired
  private JshOrderProductExtendMapper jshOrderProductExtendMapper;

	@Override
	public List<JshOrderProduct> selectByMainId(BigInteger mainId) {
		return jshOrderProductMapper.selectByMainId(mainId);
	}


  @Override
  @Transactional
  public void saveMain(JshOrderProduct jshOrderProduct, List<JshOrderProductDetail> jshOrderProductDetailList,List<JshOrderProductExtend> jshOrderProductExtendList) {
    jshOrderProductMapper.insert(jshOrderProduct);
    if(jshOrderProductDetailList!=null && jshOrderProductDetailList.size()>0) {
      for(JshOrderProductDetail entity:jshOrderProductDetailList) {
        //外键设置
        entity.setOrderProductId(jshOrderProduct.getId());
        jshOrderProductDetailMapper.insert(entity);
      }
    }
    if(jshOrderProductExtendList!=null && jshOrderProductExtendList.size()>0) {
      for(JshOrderProductExtend entity:jshOrderProductExtendList) {
        //外键设置
        entity.setOrderProductId(jshOrderProduct.getId());
        jshOrderProductExtendMapper.insert(entity);
      }
    }
  }

  @Override
  @Transactional
  public void updateMain(JshOrderProduct jshOrderProduct, List<JshOrderProductDetail> jshOrderProductDetailList, List<JshOrderProductExtend> jshOrderProductExtendList) {
    jshOrderProductMapper.updateById(jshOrderProduct);

    //1.先删除子表数据
    jshOrderProductDetailMapper.deleteByMainId(jshOrderProduct.getId());
    jshOrderProductExtendMapper.deleteByMainId(jshOrderProduct.getId());

    //2.子表数据重新插入
    if(jshOrderProductDetailList!=null && jshOrderProductDetailList.size()>0) {
      for(JshOrderProductDetail entity:jshOrderProductDetailList) {
        //外键设置
        entity.setOrderProductId(jshOrderProduct.getId());
        jshOrderProductDetailMapper.insert(entity);
      }
    }
    if(jshOrderProductExtendList!=null && jshOrderProductExtendList.size()>0) {
      for(JshOrderProductExtend entity:jshOrderProductExtendList) {
        //外键设置
        entity.setOrderProductId(jshOrderProduct.getId());
        jshOrderProductExtendMapper.insert(entity);
      }
    }
  }

  @Override
  @Transactional
  public void delMain(String id) {
    BigInteger mainId = new BigInteger(id.toString());
    jshOrderProductDetailMapper.deleteByMainId(mainId);
    jshOrderProductExtendMapper.deleteByMainId(mainId);
    jshOrderProductMapper.deleteById(id);
  }

  @Override
  @Transactional
  public void delBatchMain(Collection<? extends Serializable> idList) {
    for(Serializable id:idList) {
      BigInteger mainId = new BigInteger(id.toString());
      jshOrderProductDetailMapper.deleteByMainId(mainId);
      jshOrderProductExtendMapper.deleteByMainId(mainId);
      jshOrderProductMapper.deleteById(id);
    }
  }
}
