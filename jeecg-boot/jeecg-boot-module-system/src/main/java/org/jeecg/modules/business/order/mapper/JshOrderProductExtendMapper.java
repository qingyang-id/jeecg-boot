package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrderProductExtend;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description: jsh_order_product_extend
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderProductExtendMapper extends BaseMapper<JshOrderProductExtend> {

  public boolean deleteByMainId(@Param("mainId") BigInteger mainId);

  public List<JshOrderProductExtend> selectByMainId(@Param("mainId") BigInteger mainId);
}
