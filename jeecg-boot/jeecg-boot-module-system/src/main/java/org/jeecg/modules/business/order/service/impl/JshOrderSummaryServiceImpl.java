package org.jeecg.modules.business.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.business.order.entity.JshOrderSummary;
import org.jeecg.modules.business.order.entity.JshOrderSummaryDaily;
import org.jeecg.modules.business.order.entity.JshOrderSummaryMonthly;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryDailyMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryMonthlyMapper;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
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
    List<JshOrderSummaryDaily> jshOrderSummaryDailyList = jshOrderMapper.selectDailyOrderSummary(startTime, endTime);
    if (jshOrderSummaryDailyList.size() > 0 && jshOrderSummaryDailyList.get(0).getTime() != null) {
      // 计算总账
      JshOrderSummary jshOrderSummary = new JshOrderSummary(startTime);
      List<JshOrderSummary> jshOrderSummaryList = jshOrderSummaryDailyList.stream()
              .map(item -> {
                jshOrderSummary.setTotalNum(jshOrderSummary.getTotalNum() + item.getTotalNum());
                jshOrderSummary.setTotalArea(jshOrderSummary.getTotalArea() + item.getTotalArea());
                jshOrderSummary.setTotalPrice(jshOrderSummary.getTotalPrice() + item.getTotalPrice());
                return new JshOrderSummary(item, startTime);
              }).collect(Collectors.toList());
      jshOrderSummaryList.add(jshOrderSummary);
      jshOrderSummaryDailyMapper.batchInsertOrUpdate(jshOrderSummaryList);
    } else {
      jshOrderSummaryDailyMapper.batchInsertOrUpdate(Collections.singletonList(new JshOrderSummary(startTime)));
    }
    // 检查前一天数据是否已统计, 最多复合近10天的数据
    Date lastDay = DateUtils.addDays(startTime, -1);
    LambdaUpdateWrapper<JshOrderSummaryDaily> jshOrderSummaryDailyLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    jshOrderSummaryDailyLambdaUpdateWrapper.eq(JshOrderSummary::getTime, lastDay);
    JshOrderSummaryDaily jshOrderSummaryDaily = jshOrderSummaryDailyMapper.selectOne(jshOrderSummaryDailyLambdaUpdateWrapper);
    if (jshOrderSummaryDaily == null) {
      this.statisticDailyOrders(lastDay, deeps + 1);
    }
  }

  @Override
  public void statisticMonthlyOrders(Date startTime, Integer deeps) {
    // 向前复合近2条数据
    if (deeps >= 2) return;
    // 查询当前日期
    Date endTime = DateUtils.getDateEnd(startTime);
    List<JshOrderSummary> jshOrderSummaryList = jshOrderSummaryDailyMapper.selectOrderSummary(startTime, endTime);
    if (jshOrderSummaryList.size() > 0 && jshOrderSummaryList.get(0).getTime() != null) {
      // 计算总账
      JshOrderSummary jshOrderSummary = new JshOrderSummary(startTime);
      jshOrderSummaryList = jshOrderSummaryList.stream()
              .map(item -> {
                jshOrderSummary.setTotalNum(jshOrderSummary.getTotalNum() + item.getTotalNum());
                jshOrderSummary.setTotalArea(jshOrderSummary.getTotalArea() + item.getTotalArea());
                jshOrderSummary.setTotalPrice(jshOrderSummary.getTotalPrice() + item.getTotalPrice());
                return new JshOrderSummary(item, startTime);
              }).collect(Collectors.toList());
      jshOrderSummaryList.add(jshOrderSummary);
      jshOrderSummaryMonthlyMapper.batchInsertOrUpdate(jshOrderSummaryList);
    } else {
      jshOrderSummaryMonthlyMapper.batchInsertOrUpdate(Collections.singletonList(new JshOrderSummary(startTime)));
    }
    // 检查前一天数据是否已统计
    Date lastDay = DateUtils.addDays(startTime, -1);
    LambdaUpdateWrapper<JshOrderSummaryMonthly> jshOrderSummaryDailyLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    jshOrderSummaryDailyLambdaUpdateWrapper.eq(JshOrderSummary::getTime, lastDay);
    JshOrderSummaryMonthly jshOrderSummaryMonthly = jshOrderSummaryMonthlyMapper.selectOne(jshOrderSummaryDailyLambdaUpdateWrapper);
    if (jshOrderSummaryMonthly == null) {
      this.statisticMonthlyOrders(lastDay, deeps + 1);
    }
  }
}
