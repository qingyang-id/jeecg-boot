<template>
  <div class="p-2">
    <a-card :bordered="false">
      <a-tabs>
        <a-tab-pane tab="月数据" key="monthly">
          <SummaryMonthlyList />
        </a-tab-pane>
        <a-tab-pane tab="日数据" key="daily">
          <SummaryDailyList />
        </a-tab-pane>
        <a-tab-pane tab="月总账数据" key="all-monthly">
          <SummaryMonthlyList customerId="0" />
        </a-tab-pane>
        <a-tab-pane tab="日总账数据" key="all-daily">
          <SummaryDailyList customerId="0" />
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>
<script lang="ts" name="business-order-JshOrderSummaryList" setup>
  /**
   * 订单统计（菜单 component: business/order/JshOrderSummaryList）
   * 老：order/JshOrderSummaryList.vue
   */
  import SummaryDailyList from './SummaryDailyList.vue';
  import SummaryMonthlyList from './SummaryMonthlyList.vue';
</script>
