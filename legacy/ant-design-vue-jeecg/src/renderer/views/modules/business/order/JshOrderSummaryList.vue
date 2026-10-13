<template>
  <a-card :bordered="false">
    <a-tabs>
      <a-tab-pane tab="月数据" key="monthly">
        <jsh-order-summary-monthly-list/>
      </a-tab-pane>
      <a-tab-pane tab="日数据" key="daily">
        <jsh-order-summary-daily-list/>
      </a-tab-pane>
      <a-tab-pane tab="月总账数据" key="all-monthly">
        <jsh-order-summary-monthly-list :customerId="'0'"/>
      </a-tab-pane>
      <a-tab-pane tab="日总账数据" key="all-daily">
        <jsh-order-summary-daily-list :customerId="'0'"/>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
import JshOrderSummaryDailyList from './modules/JshOrderSummaryDailyList'
import JshOrderSummaryMonthlyList from './modules/JshOrderSummaryMonthlyList'

export default {
  name: 'JshOrderSummaryList',
  components: {JshOrderSummaryDailyList, JshOrderSummaryMonthlyList}
}
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
