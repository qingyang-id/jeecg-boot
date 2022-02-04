package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.mapper.JshOrderProductDetailMapper;
import org.jeecg.modules.business.order.service.IJshOrderProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product_detail
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderProductDetailServiceImpl extends ServiceImpl<JshOrderProductDetailMapper, JshOrderProductDetail> implements IJshOrderProductDetailService {
  @Autowired
  private JshOrderProductDetailMapper jshOrderProductDetailMapper;

  @Override
  public List<JshOrderProductDetail> selectByMainId(BigInteger mainId) {
    return jshOrderProductDetailMapper.selectByMainId(mainId);
  }
}
