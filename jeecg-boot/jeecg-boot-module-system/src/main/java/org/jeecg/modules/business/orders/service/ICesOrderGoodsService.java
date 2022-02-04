package org.jeecg.modules.business.orders.service;

import org.jeecg.modules.business.orders.entity.CesOrderGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 订单商品
 * @Author: jeecg-boot
 * @Date:   2022-02-05
 * @Version: V1.0
 */
public interface ICesOrderGoodsService extends IService<CesOrderGoods> {

	public List<CesOrderGoods> selectByMainId(String mainId);
}
