<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <template v-if="mainId">
          <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
          <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
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
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            { label: '编辑', onClick: handleEdit.bind(null, record) },
            { label: '删除', popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record) } },
          ]"
        />
      </template>
    </BasicTable>
    <ProductExtendModal @register="registerModal" :mainId="mainId" @success="reload" />
  </div>
</template>
<script lang="ts" name="business-order-ProductExtendList" setup>
  /**
   * 订单商品扩展项（抽/拉/条）列表
   * 老：modules/JshOrderProductExtendList.vue（老页面 URL 指向不存在的 /order6/**，
   * 这里改为后端实际存在的 /business/order/jshOrderProductExtend/**）
   * mainId = 订单产品表ID（orderProductId）；为空时清空列表
   */
  import { reactive, watch } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import ProductExtendModal from './ProductExtendModal.vue';
  import {
    orderProductExtendList,
    orderProductExtendDelete,
    orderProductExtendDeleteBatch,
    orderProductExtendExportUrl,
    orderProductExtendImportUrl,
  } from './order.api';
  import { orderProductExtendColumns, orderProductExtendSuperQuerySchema, pagination } from './order.data';

  const props = defineProps({
    mainId: { type: String, default: '' },
  });

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      api: (params) => (props.mainId ? orderProductExtendList(params) : Promise.resolve({ records: [], total: 0 })),
      columns: orderProductExtendColumns,
      showIndexColumn: true,
      useSearchForm: false,
      pagination,
      actionColumn: { width: 147, fixed: 'right' },
      beforeFetch: (params) => Object.assign(params, queryParam, { orderProductId: props.mainId }),
    },
    exportConfig: {
      name: 'jsh_order_product_extend',
      url: orderProductExtendExportUrl,
      params: () => ({ ...queryParam, orderProductId: props.mainId }),
    },
    importConfig: {
      url: orderProductExtendImportUrl,
      success: () => reload(),
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  const superQueryConfig = reactive(orderProductExtendSuperQuerySchema);

  watch(
    () => props.mainId,
    () => {
      selectedRowKeys.value = [];
      reload({ page: 1 });
    }
  );

  function handleSuperQuery(params) {
    Object.keys(params).forEach((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  async function handleDelete(record: Recordable) {
    await orderProductExtendDelete({ id: record.id }, reload);
  }

  async function batchHandleDelete() {
    await orderProductExtendDeleteBatch({ ids: selectedRowKeys.value.join(',') }, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }
</script>
