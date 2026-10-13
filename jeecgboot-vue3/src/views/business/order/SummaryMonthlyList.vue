<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>
<script lang="ts" name="business-order-SummaryMonthlyList" setup>
  /**
   * 订单月数据
   * 老：modules/JshOrderSummaryMonthlyList.vue
   * customerId 传 '0' 时为“月总账数据”：固定 customerId=0、隐藏客户条件与客户列
   */
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { summaryMonthlyList, summaryMonthlyExportUrl } from './order.api';
  import { summaryMonthlyColumns, summaryMonthlySearchSchema, summaryPagination, loadCustomerOptions } from './order.data';

  const props = defineProps({
    customerId: { type: String, default: null },
  });

  loadCustomerOptions();

  const fixedCustomer = !!props.customerId;
  const columns = fixedCustomer ? summaryMonthlyColumns.filter((item) => item.title !== '客户') : summaryMonthlyColumns;

  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      api: summaryMonthlyList,
      columns,
      // 老月数据表没有 # 列
      showIndexColumn: false,
      showActionColumn: false,
      formConfig: {
        schemas: summaryMonthlySearchSchema(fixedCustomer),
        autoSubmitOnEnter: true,
      },
      pagination: summaryPagination,
      defSort: { column: 'id', order: 'desc' },
      beforeFetch: (params) => {
        // 老：onTimeChange —— 月份 -> 当月 1 号（YYYY-MM-DD），按 time 精确匹配
        params.time = params.month ? `${params.month}-01` : '';
        delete params.month;
        if (props.customerId) params.customerId = props.customerId;
        return params;
      },
    },
    exportConfig: {
      name: '订单月数据',
      url: summaryMonthlyExportUrl,
      params: () => (props.customerId ? { customerId: props.customerId } : {}),
    },
  });

  const [registerTable] = tableContext;
</script>
