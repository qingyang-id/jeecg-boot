<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
        <!-- 老页面导入按钮已注释，这里同样不提供导入 -->
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button
            >批量操作
            <Icon icon="ant-design:down-outlined" />
          </a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>

    <a-tabs defaultActiveKey="0">
      <a-tab-pane tab="原尺寸" key="0">
        <ProductList :orderId="selectedMainId" :search="false" />
      </a-tab-pane>
      <a-tab-pane tab="铝材" key="1">
        <ProductDetailList :orderId="selectedMainId" :type="1" />
      </a-tab-pane>
      <a-tab-pane tab="玻璃" key="2">
        <ProductDetailList :orderId="selectedMainId" :type="2" />
      </a-tab-pane>
    </a-tabs>

    <JshOrderModal @register="registerModal" @success="handleSuccess" />
    <OrderDetail ref="modalDetailRef" />
  </div>
</template>
<script lang="ts" name="business-order-JshOrderList" setup>
  /**
   * 订单管理
   * 老：order/JshOrderList.vue
   * - 单选行（点击行即选中），选中订单后下方 tab 显示其 原尺寸 / 铝材 / 玻璃；未选中时显示全部
   * - 默认按订单编码倒序
   */
  import { computed, reactive, ref } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import type { ActionItem } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import JshOrderModal from './JshOrderModal.vue';
  import OrderDetail from './OrderDetail.vue';
  import ProductList from './ProductList.vue';
  import ProductDetailList from './ProductDetailList.vue';
  import { orderColumns, orderSearchFormSchema, orderSuperQuerySchema, pagination, loadCustomerOptions, mapRangeParams } from './order.data';
  import { orderList, orderDelete, orderDeleteBatch, orderExportUrl } from './order.api';

  const queryParam = reactive<any>({});
  const modalDetailRef = ref();
  const [registerModal, { openModal }] = useModal();

  loadCustomerOptions();

  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '订单列表',
      api: orderList,
      columns: orderColumns,
      showIndexColumn: true,
      clickToRowSelect: true,
      rowSelection: { type: 'radio' },
      formConfig: {
        schemas: orderSearchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        autoAdvancedCol: 2,
        fieldMapToTime: [['orderTimeRange', ['orderTime_begin', 'orderTime_end'], 'YYYY-MM-DD']],
      },
      pagination,
      actionColumn: { width: 180, fixed: 'right' },
      defSort: { column: 'orderCode', order: 'desc' },
      beforeFetch: (params) => Object.assign(mapRangeParams(params, 'orderTimeRange', 'orderTime_begin', 'orderTime_end'), queryParam),
      // 老 loadData 每次加载都会 onClearSelected()（选中订单清空，下方 tab 回到全部）
      // 放在 afterFetch：beforeFetch 也会被导出调用，清空会丢掉导出用的 selections
      afterFetch: (records) => {
        clearSelection();
        return records;
      },
    },
    exportConfig: {
      name: '订单信息',
      url: orderExportUrl,
      params: queryParam,
    },
  });

  const [registerTable, { reload, clearSelectedRowKeys }, { rowSelection, selectedRowKeys }] = tableContext;

  function clearSelection() {
    if (selectedRowKeys.value.length) {
      selectedRowKeys.value = [];
      clearSelectedRowKeys();
    }
  }

  /** 选中的订单 ID（字符串原样）；未选中为 '' */
  const selectedMainId = computed(() => (selectedRowKeys.value[0] != null ? `${selectedRowKeys.value[0]}` : ''));

  const superQueryConfig = reactive(orderSuperQuerySchema);

  function handleSuperQuery(params) {
    Object.keys(params).forEach((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  /** 老：printDetail(record, type) */
  function printDetail(record: Recordable, type: string) {
    modalDetailRef.value?.myHandleDetail(record, type);
  }

  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  function handleSuccess() {
    reload();
  }

  async function handleDelete(record: Recordable) {
    // id 原样（字符串）传给后端，不做 Number 转换
    await orderDelete({ id: record.id }, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }

  async function batchHandleDelete() {
    await orderDeleteBatch({ ids: selectedRowKeys.value.join(',') }, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }

  function getTableAction(record): ActionItem[] {
    return [
      { label: '生产单打印', onClick: printDetail.bind(null, record, '生产单') },
      { label: '编辑', onClick: handleEdit.bind(null, record) },
    ];
  }

  function getDropDownAction(record): ActionItem[] {
    return [
      { label: '订单打印', onClick: printDetail.bind(null, record, '订单信息') },
      { label: '铝材打印', onClick: printDetail.bind(null, record, '铝材生产单') },
      { label: '玻璃打印', onClick: printDetail.bind(null, record, '玻璃生产单') },
      {
        label: '删除',
        popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record), placement: 'topLeft' },
      },
    ];
  }
</script>
