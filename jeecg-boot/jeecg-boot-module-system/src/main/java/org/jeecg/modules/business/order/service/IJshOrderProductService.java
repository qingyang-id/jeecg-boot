package org.jeecg.modules.business.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.order.entity.JshOrderProduct;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface IJshOrderProductService extends IService<JshOrderProduct> {

	public List<JshOrderProduct> selectByMainId(Long mainId);

  /**
   * 添加一对多
   *
   */
  public void saveMain(JshOrderProduct jshOrderProduct, List<JshOrderProductDetail> jshOrderProductDetailList, List<JshOrderProductExtend> jshOrderProductExtendList) ;

  /**
   * 修改一对多
   *
   */
  public void updateMain(JshOrderProduct jshOrderProduct,List<JshOrderProductDetail> jshOrderProductDetailList,List<JshOrderProductExtend> jshOrderProductExtendList);

  /**
   * 删除一对多
   */
  public void delMain (Long id);

  /**
   * 批量删除一对多
   */
  public void delBatchMain (Collection<? extends Serializable> idList);
}
