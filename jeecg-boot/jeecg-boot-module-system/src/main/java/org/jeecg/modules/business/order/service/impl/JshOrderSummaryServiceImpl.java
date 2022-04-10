package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.business.order.entity.JshOrderSummary;
import org.jeecg.modules.business.order.entity.JshOrderSummaryDaily;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryDailyMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryMonthlyMapper;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Service
public class JshOrderSummaryServiceImpl extends ServiceImpl<JshOrderSummaryDailyMapper, JshOrderSummaryDaily> implements IJshOrderSummaryService {

  @Resource
  private JshOrderMapper jshOrderMapper;

  @Resource
  private JshOrderSummaryDailyMapper jshOrderSummaryDailyMapper;

  @Resource
  private JshOrderSummaryMonthlyMapper jshOrderSummaryMonthlyMapper;

  @Override
  public void statisticDailyOrders(Date startTime, Integer deeps) {
    // 最多复合近10天的数据
    if (deeps >= 10) return;
    // 查询当前日期
    Date endTime = DateUtils.getDateEnd(startTime);
    List<JshOrderSummary> jshOrderSummaryList = jshOrderMapper.selectDailyOrderSummary(startTime, endTime);
    if (jshOrderSummaryList.size() > 0 && jshOrderSummaryList.get(0).getTenantId() != null) {
      // 计算总账
      JshOrderSummary jshOrderSummary = new JshOrderSummary(startTime, jshOrderSummaryList.get(0).getTenantId());
      jshOrderSummaryList = jshOrderSummaryList.stream()
              .map(item -> {
                jshOrderSummary.setTotalNum(jshOrderSummary.getTotalNum() + item.getTotalNum());
                jshOrderSummary.setTotalArea(jshOrderSummary.getTotalArea() + item.getTotalArea());
                jshOrderSummary.setTotalPrice(jshOrderSummary.getTotalPrice() + item.getTotalPrice());
                return new JshOrderSummary(item, startTime);
              }).collect(Collectors.toList());
      jshOrderSummaryList.add(jshOrderSummary);
      jshOrderSummaryDailyMapper.batchInsertOrUpdate(jshOrderSummaryList);
    }
    // 复合近10天的数据
    Date lastDay = org.apache.commons.lang3.time.DateUtils.addDays(startTime, -1);
    this.statisticDailyOrders(lastDay, deeps + 1);
  }

  @Override
  public void statisticMonthlyOrders(Date startTime, Integer deeps) {
    // 向前复合近2条数据
    if (deeps >= 2) return;
    // 查询当前日期
    Date endTime = DateUtils.getMonthEnd(startTime);
    List<JshOrderSummary> jshOrderSummaryList = jshOrderSummaryDailyMapper.selectOrderSummary(startTime, endTime);
    if (jshOrderSummaryList.size() > 0 && jshOrderSummaryList.get(0).getTenantId() != null) {
      // 计算总账
      JshOrderSummary jshOrderSummary = new JshOrderSummary(startTime, jshOrderSummaryList.get(0).getTenantId());
      jshOrderSummaryList = jshOrderSummaryList.stream()
              .map(item -> {
                jshOrderSummary.setTotalNum(jshOrderSummary.getTotalNum() + item.getTotalNum());
                jshOrderSummary.setTotalArea(jshOrderSummary.getTotalArea() + item.getTotalArea());
                jshOrderSummary.setTotalPrice(jshOrderSummary.getTotalPrice() + item.getTotalPrice());
                return new JshOrderSummary(item, startTime);
              }).collect(Collectors.toList());
      jshOrderSummaryList.add(jshOrderSummary);
      jshOrderSummaryMonthlyMapper.batchInsertOrUpdate(jshOrderSummaryList);
    }
    // 重新统计上一月数据
    Date lastMonth = org.apache.commons.lang3.time.DateUtils.addMonths(startTime, -1);
    this.statisticMonthlyOrders(lastMonth, deeps + 1);
  }
}
