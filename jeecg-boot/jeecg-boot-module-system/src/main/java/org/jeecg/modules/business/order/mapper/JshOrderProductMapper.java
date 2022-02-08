package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrderProduct;

import java.util.List;

/**
 * @Description: jsh_order_product
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderProductMapper extends BaseMapper<JshOrderProduct> {

	public boolean deleteByMainId(@Param("mainId") Long mainId);

	public List<JshOrderProduct> selectByMainId(@Param("mainId") Long mainId);
}
