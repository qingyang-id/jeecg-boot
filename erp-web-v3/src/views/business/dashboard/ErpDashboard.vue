<template>
  <div class="p-4">
    <a-row :gutter="[16, 16]">
      <a-col v-for="card in cards" :key="card.title" :xs="24" :sm="12" :xl="6">
        <a-card :loading="loading" :bordered="false" size="small">
          <div class="erp-card-title">{{ card.title }}</div>
          <div class="erp-card-value">{{ card.value }}</div>
          <div class="erp-card-foot">{{ card.foot }}</div>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="[16, 16]" class="mt-4">
      <a-col :xs="24" :xl="16">
        <a-card :loading="loading" :bordered="false" title="近 12 个月">
          <template #extra>
            <a-radio-group v-model:value="metric" size="small" button-style="solid">
              <a-radio-button value="price">金额(元)</a-radio-button>
              <a-radio-button value="area">面积(m²)</a-radio-button>
              <a-radio-button value="num">订单数</a-radio-button>
            </a-radio-group>
          </template>
          <Bar :chartData="trendData" height="360px" />
        </a-card>
      </a-col>
      <a-col :xs="24" :xl="8">
        <a-card :loading="loading" :bordered="false" title="本月客户排行（金额）">
          <ol v-if="rank.length" class="erp-rank">
            <li v-for="(item, index) in rank" :key="item.id">
              <span :class="['erp-rank-no', index < 3 ? 'top' : '']">{{ index + 1 }}</span>
              <span class="erp-rank-name">{{ item.customerId_dictText || item.customerId }}</span>
              <span>{{ yuan(item.totalPrice) }}</span>
            </li>
          </ol>
          <a-empty v-else description="本月暂无订单" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
  /**
   * ERP 首页：数据来自订单统计表（StatisticOrderJob 每 10 分钟汇总一次）
   * customerId=0 的行是总账；不传 customerId 时后端排除总账行，即按客户的明细
   * 金额单位为分，面积单位为 mm²
   */
  import { computed, ref } from 'vue';
  import dayjs from 'dayjs';
  import Bar from '/@/components/chart/Bar.vue';
  import { summaryDailyList, summaryMonthlyList } from '../order/order.api';

  const loading = ref(true);
  const metric = ref('price');
  const months = ref<any[]>([]);
  const days = ref<any[]>([]);
  const rank = ref<any[]>([]);

  const today = dayjs().format('YYYY-MM-DD');
  const yesterday = dayjs().subtract(1, 'day').format('YYYY-MM-DD');
  const thisMonth = dayjs().format('YYYY-MM-01');
  const lastMonth = dayjs().subtract(1, 'month').format('YYYY-MM-01');

  const yuan = (fen) => ((Number(fen) || 0) / 100).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  const sqm = (mm2) => ((Number(mm2) || 0) / 1000000).toLocaleString('zh-CN', { maximumFractionDigits: 2 });
  const byTime = (rows, time) => rows.find((r) => (r.time || '').substring(0, 10) === time) || {};

  const cards = computed(() => {
    const d0 = byTime(days.value, today);
    const d1 = byTime(days.value, yesterday);
    const m0 = byTime(months.value, thisMonth);
    const m1 = byTime(months.value, lastMonth);
    return [
      { title: '今日订单数', value: d0.totalNum || 0, foot: `昨日 ${d1.totalNum || 0} 单` },
      { title: '今日金额(元)', value: yuan(d0.totalPrice), foot: `昨日 ${yuan(d1.totalPrice)}` },
      { title: '本月订单数 / 面积(m²)', value: `${m0.totalNum || 0} / ${sqm(m0.totalArea)}`, foot: `上月 ${m1.totalNum || 0} / ${sqm(m1.totalArea)}` },
      { title: '本月金额(元)', value: yuan(m0.totalPrice), foot: `上月 ${yuan(m1.totalPrice)}` },
    ];
  });

  const trendData = computed(() =>
    [...months.value].reverse().map((r) => {
      const raw = metric.value === 'price' ? r.totalPrice : metric.value === 'area' ? r.totalArea : r.totalNum;
      const n = Number(raw) || 0;
      const value = metric.value === 'price' ? n / 100 : metric.value === 'area' ? n / 1000000 : n;
      return { name: (r.time || '').substring(0, 7), value: Math.round(value * 100) / 100 };
    })
  );

  const records = (res) => res?.records || [];

  Promise.all([
    summaryMonthlyList({ customerId: '0', column: 'time', order: 'desc', pageNo: 1, pageSize: 12 }),
    summaryDailyList({ customerId: '0', column: 'time', order: 'desc', pageNo: 1, pageSize: 2 }),
    summaryMonthlyList({ time: thisMonth, column: 'totalPrice', order: 'desc', pageNo: 1, pageSize: 10 }),
  ])
    .then(([m, d, r]) => {
      months.value = records(m);
      days.value = records(d);
      rank.value = records(r);
    })
    .finally(() => {
      loading.value = false;
    });
</script>
<style lang="less" scoped>
  .erp-card-title {
    color: rgba(0, 0, 0, 0.45);
  }
  .erp-card-value {
    font-size: 28px;
    line-height: 44px;
  }
  .erp-card-foot {
    color: rgba(0, 0, 0, 0.45);
    border-top: 1px solid #f0f0f0;
    padding-top: 8px;
    margin-top: 8px;
  }
  .erp-rank {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      display: flex;
      align-items: center;
      height: 36px;
    }
    .erp-rank-no {
      width: 20px;
      height: 20px;
      line-height: 20px;
      border-radius: 50%;
      text-align: center;
      font-size: 12px;
      margin-right: 12px;
      background: #f5f5f5;
      &.top {
        background: #314659;
        color: #fff;
      }
    }
    .erp-rank-name {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
</style>
