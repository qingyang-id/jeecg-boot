<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>
<script lang="ts" name="business-order-ProductList" setup>
  /**
   * 订单商品（原尺寸）列表
   * 老：modules/JshOrderProductList.vue
   * orderId 为空时 orderId='' 即查询全部（与老页面一致）
   */
  import { watch } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { orderProductList, orderProductExportUrl } from './order.api';
  import { orderProductColumns, orderProductSearchFormSchema, pagination, loadCustomerOptions, mapRangeParams } from './order.data';

  const props = defineProps({
    orderId: { type: String, default: '' },
    search: { type: Boolean, default: true },
  });

  if (props.search) {
    loadCustomerOptions();
  }

  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      api: orderProductList,
      columns: orderProductColumns,
      showIndexColumn: true,
      showActionColumn: false,
      useSearchForm: props.search,
      formConfig: {
        schemas: orderProductSearchFormSchema,
        fieldMapToTime: [['orderTimeRange', ['orderTime_begin', 'orderTime_end'], 'YYYY-MM-DD']],
      },
      pagination,
      defSort: { column: 'id', order: 'desc' },
      beforeFetch: (params) =>
        Object.assign(mapRangeParams(params, 'orderTimeRange', 'orderTime_begin', 'orderTime_end'), { orderId: props.orderId || '' }),
    },
    exportConfig: {
      name: '订单明细',
      url: orderProductExportUrl,
      params: () => ({ orderId: props.orderId || '' }),
    },
  });

  const [registerTable, { reload }] = tableContext;

  watch(
    () => props.orderId,
    () => reload({ page: 1 })
  );
</script>
