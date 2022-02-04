package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;
import org.jeecg.modules.business.order.mapper.JshOrderProductExtendMapper;
import org.jeecg.modules.business.order.service.IJshOrderProductExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product_extend
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderProductExtendServiceImpl extends ServiceImpl<JshOrderProductExtendMapper, JshOrderProductExtend> implements IJshOrderProductExtendService {
  @Autowired
  private JshOrderProductExtendMapper jshOrderProductExtendMapper;

  @Override
  public List<JshOrderProductExtend> selectByMainId(BigInteger mainId) {
    return jshOrderProductExtendMapper.selectByMainId(mainId);
  }
}
