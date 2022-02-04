package org.jeecg.modules.business.orders.mapper;

import java.util.List;
import org.jeecg.modules.business.orders.entity.CesOrderCustomer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
public interface CesOrderCustomerMapper extends BaseMapper<CesOrderCustomer> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<CesOrderCustomer> selectByMainId(@Param("mainId") String mainId);
}
