<template>
  <div>
    <BasicTable @register="registerTable" />
  </div>
</template>
<script lang="ts" name="business-order-ProductDetailList" setup>
  /**
   * 铝材(type=1) / 玻璃(type=2) 明细列表
   * 老：modules/JshOrderProductDetailList.vue
   * orderId 为空时 orderId='' 即查询全部（与老页面一致）
   */
  import { watch } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { orderProductDetailList } from './order.api';
  import { aluminumDetailColumns, glassDetailColumns, pagination } from './order.data';

  const props = defineProps({
    orderId: { type: String, default: '' },
    type: { type: Number, default: 0 },
  });

  const [registerTable, { reload }] = useTable({
    api: orderProductDetailList,
    columns: props.type === 1 ? aluminumDetailColumns : glassDetailColumns,
    rowKey: 'id',
    bordered: true,
    showIndexColumn: true,
    showActionColumn: false,
    useSearchForm: false,
    canResize: false,
    pagination,
    defSort: { column: 'id', order: 'desc' },
    beforeFetch: (params) => Object.assign(params, { orderId: props.orderId || '', type: props.type || '' }),
  });

  watch(
    () => props.orderId,
    () => reload({ page: 1 })
  );
</script>
