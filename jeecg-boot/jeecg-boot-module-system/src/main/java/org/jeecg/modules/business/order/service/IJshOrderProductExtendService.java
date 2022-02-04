package org.jeecg.modules.business.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product_extend
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface IJshOrderProductExtendService extends IService<JshOrderProductExtend> {
  public List<JshOrderProductExtend> selectByMainId(BigInteger mainId);
}
