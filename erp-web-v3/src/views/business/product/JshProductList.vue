<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
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
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>
    <JshProductModal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" name="business-product-JshProductList" setup>
  import { reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import type { ActionItem } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import JshProductModal from './JshProductModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './product.data';
  import { list, deleteOne, batchDelete, getExportUrl, getImportUrl } from './product.api';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '商品列表',
      api: list,
      columns,
      showIndexColumn: true,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        autoAdvancedCol: 2,
      },
      actionColumn: { width: 147, fixed: 'right' },
      // 老页面默认按 id 倒序
      defSort: { column: 'id', order: 'desc' },
      beforeFetch: (params) => Object.assign(params, queryParam),
    },
    exportConfig: {
      name: 'jsh_product',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: () => reload(),
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  const superQueryConfig = reactive(superQuerySchema);

  function handleSuperQuery(params) {
    Object.keys(params).forEach((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function handleAdd() {
    openModal(true, { isUpdate: false, isDetail: false });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true, isDetail: false });
  }

  function handleDetail(record: Recordable) {
    openModal(true, { record, isUpdate: true, isDetail: true });
  }

  async function handleDelete(record: Recordable) {
    // id 原样（字符串）传给后端，不做 Number 转换
    await deleteOne({ id: record.id }, reload);
  }

  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value.join(',') }, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }

  function getTableAction(record): ActionItem[] {
    return [{ label: '编辑', onClick: handleEdit.bind(null, record) }];
  }

  function getDropDownAction(record): ActionItem[] {
    return [
      { label: '详情', onClick: handleDetail.bind(null, record) },
      {
        label: '删除',
        popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record), placement: 'topLeft' },
      },
    ];
  }
</script>
