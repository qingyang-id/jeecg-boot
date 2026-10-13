package org.jeecg.modules.business.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.customer.entity.JshCustomerAddress;

import java.util.List;

/**
 * @Description: jsh_customer_address
 * @Author: jeecg-boot
 * @Date:   2022-02-09
 * @Version: V1.0
 */
public interface JshCustomerAddressMapper extends BaseMapper<JshCustomerAddress> {

	public boolean deleteByMainId(@Param("mainId") Long mainId);

	public List<JshCustomerAddress> selectByMainId(@Param("mainId") Long mainId);

}
