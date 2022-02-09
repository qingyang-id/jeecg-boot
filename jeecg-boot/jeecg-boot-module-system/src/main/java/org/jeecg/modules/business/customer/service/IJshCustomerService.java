package org.jeecg.modules.business.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.customer.entity.JshCustomer;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: jsh_customer
 * @Author: jeecg-boot
 * @Date:   2022-02-03
 * @Version: V1.0
 */
public interface IJshCustomerService extends IService<JshCustomer> {

  /**
   * 删除一对多
   */
  public void delMain (Long id);

  /**
   * 批量删除一对多
   */
  public void delBatchMain (Collection<? extends Serializable> idList);
}
