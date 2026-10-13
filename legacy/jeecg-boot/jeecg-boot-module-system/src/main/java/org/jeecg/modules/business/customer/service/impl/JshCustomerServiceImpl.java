package org.jeecg.modules.business.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.business.customer.entity.JshCustomer;
import org.jeecg.modules.business.customer.mapper.JshCustomerMapper;
import org.jeecg.modules.business.customer.service.IJshCustomerService;
import org.jeecg.modules.business.customer.mapper.JshCustomerAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: jsh_customer
 * @Author: jeecg-boot
 * @Date: 2022-02-03
 * @Version: V1.0
 */
@Service
public class JshCustomerServiceImpl extends ServiceImpl<JshCustomerMapper, JshCustomer> implements IJshCustomerService {
  @Autowired
  private JshCustomerMapper jshCustomerMapper;
  @Autowired
  private JshCustomerAddressMapper jshCustomerAddressMapper;

  @Override
  @Transactional
  public void delMain(Long id) {
    jshCustomerAddressMapper.deleteByMainId(Long.parseLong(id.toString()));
    jshCustomerMapper.deleteById(id);
  }

  @Override
  @Transactional
  public void delBatchMain(Collection<? extends Serializable> idList) {
    for (Serializable id : idList) {
      jshCustomerAddressMapper.deleteByMainId(Long.parseLong(id.toString()));
      jshCustomerMapper.deleteById(id);
    }
  }
}
