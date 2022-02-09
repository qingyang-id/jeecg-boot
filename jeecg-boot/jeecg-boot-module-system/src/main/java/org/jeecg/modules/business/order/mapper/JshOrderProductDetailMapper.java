package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrderProductDetail;

import java.util.List;

/**
 * @Description: jsh_order_product_detail
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderProductDetailMapper extends BaseMapper<JshOrderProductDetail> {
  public boolean deleteByMainId(@Param("mainId") Long mainId);

  public List<JshOrderProductDetail> selectByMainId(@Param("mainId") Long mainId);

  public boolean deleteByOrderId(@Param("orderId") Long orderId);

  public List<JshOrderProductDetail> selectByOrderId(@Param("orderId") Long orderId);
}
