<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>
<script lang="ts" name="business-order-SummaryDailyList" setup>
  /**
   * 订单日数据
   * 老：modules/JshOrderSummaryDailyList.vue
   * customerId 传 '0' 时为“日总账数据”：固定 customerId=0、隐藏客户条件与客户列
   */
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { summaryDailyList, summaryDailyExportUrl } from './order.api';
  import { summaryDailyColumns, summaryDailySearchSchema, summaryPagination, loadCustomerOptions, mapRangeParams } from './order.data';

  const props = defineProps({
    customerId: { type: String, default: null },
  });

  loadCustomerOptions();

  const fixedCustomer = !!props.customerId;
  const columns = fixedCustomer ? summaryDailyColumns.filter((item) => item.title !== '客户') : summaryDailyColumns;

  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      api: summaryDailyList,
      columns,
      showIndexColumn: true,
      showActionColumn: false,
      formConfig: {
        schemas: summaryDailySearchSchema(fixedCustomer),
        autoSubmitOnEnter: true,
        fieldMapToTime: [['timeRange', ['time_begin', 'time_end'], 'YYYY-MM-DD']],
      },
      pagination: summaryPagination,
      defSort: { column: 'id', order: 'desc' },
      beforeFetch: (params) => {
        mapRangeParams(params, 'timeRange', 'time_begin', 'time_end');
        if (props.customerId) params.customerId = props.customerId;
        return params;
      },
    },
    exportConfig: {
      name: '订单日数据',
      url: summaryDailyExportUrl,
      params: () => (props.customerId ? { customerId: props.customerId } : {}),
    },
  });

  const [registerTable] = tableContext;
</script>
