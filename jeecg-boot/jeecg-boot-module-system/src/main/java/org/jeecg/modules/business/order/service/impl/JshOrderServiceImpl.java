package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
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
 * @Date: 2022-02-04
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
    if (jshOrderProductPageList != null && jshOrderProductPageList.size() > 0) {
      // 12位前缀 用于保存订单ID
      String prefix = "1" + StringUtils.leftPad(jshOrder.getId().toString(), 11, "0");
      long i = 0L;
      for (JshOrderProductPage jshOrderProductPage : jshOrderProductPageList) {
        i++;
        JshOrderProduct jshOrderProduct = new JshOrderProduct();
        BeanUtils.copyProperties(jshOrderProductPage, jshOrderProduct);
        // 设置主键 3位用于商品数量
        Long orderProductId = Long.parseLong(prefix + StringUtils.leftPad(String.valueOf(i), 3, "0"));
        jshOrderProduct.setId(orderProductId);
        // 外键设置
        jshOrderProduct.setOrderId(jshOrder.getId());
        jshOrderProduct.setCustomerId(jshOrder.getCustomerId());
        jshOrderProductMapper.insert(jshOrderProduct);

        List<JshOrderProductExtend> jshOrderProductExtendList = jshOrderProductPage.getJshOrderProductExtendList();
        if (jshOrderProductExtendList != null && jshOrderProductExtendList.size() > 0) {
          long j = 0L;
          for (JshOrderProductExtend jshOrderProductExtend : jshOrderProductExtendList) {
            j++;
            // 设置主键
            Long orderProductExtendId = j + Long.parseLong(StringUtils.rightPad(orderProductId.toString(), 18, "0"));
            jshOrderProductExtend.setId(orderProductExtendId);
            // 外键设置
            jshOrderProductExtend.setOrderId(jshOrder.getId());
            jshOrderProductExtend.setOrderProductId(jshOrderProduct.getId());
            jshOrderProductExtendMapper.insert(jshOrderProductExtend);
          }
        }

        List<JshOrderProductDetail> jshOrderProductDetailList = jshOrderProductPage.getJshOrderProductDetailList();
        if (jshOrderProductDetailList != null && jshOrderProductDetailList.size() > 0) {
          long j = 0L;
          for (JshOrderProductDetail jshOrderProductDetail : jshOrderProductDetailList) {
            j++;
            // 设置主键
            Long orderProductDetailId = j + Long.parseLong(StringUtils.rightPad(orderProductId.toString(), 18, "0"));
            jshOrderProductDetail.setId(orderProductDetailId);
            // 外键设置
            jshOrderProductDetail.setOrderId(jshOrder.getId());
            jshOrderProductDetail.setCustomerId(jshOrder.getCustomerId());
            jshOrderProductDetail.setOrderProductId(jshOrderProduct.getId());
            jshOrderProductDetail.setProductId(jshOrderProduct.getProductId());
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
    jshOrderProductDetailMapper.deleteByOrderId(jshOrder.getId());
    jshOrderProductExtendMapper.deleteByOrderId(jshOrder.getId());

    // 2.子表数据重新插入
    this.saveSubs(jshOrder, jshOrderPage.getJshOrderProductPageList());
  }

  @Override
  @Transactional
  public void delMain(Long id) {
    jshOrderProductDetailMapper.deleteByOrderId(id);
    jshOrderProductExtendMapper.deleteByOrderId(id);
    jshOrderProductMapper.deleteByMainId(id);
    jshOrderMapper.deleteById(id);
  }

  @Override
  @Transactional
  public void delBatchMain(Collection<? extends Serializable> idList) {
    for (Serializable id : idList) {
      Long mainId = Long.parseLong(id.toString());
      jshOrderProductDetailMapper.deleteByOrderId(mainId);
      jshOrderProductExtendMapper.deleteByOrderId(mainId);
      jshOrderProductMapper.deleteByMainId(mainId);
      jshOrderMapper.deleteById(id);
    }
  }

}
