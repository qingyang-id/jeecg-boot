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
          <a-button>
            批量操作
            <Icon icon="ant-design:down-outlined" />
          </a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" :dropDownActions="getDropDownActions(record)" />
      </template>
    </BasicTable>

    <!-- 子表：选中一行客户后显示其地址；未选中时与老页面一致显示全部地址 -->
    <a-tabs defaultActiveKey="address" style="margin-top: 8px; background: #fff; padding: 0 8px">
      <a-tab-pane tab="客户地址" key="address" forceRender>
        <JshCustomerAddressList :mainId="selectedMainId" />
      </a-tab-pane>
    </a-tabs>

    <JshCustomerModal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" name="JshCustomerList" setup>
  import { computed, reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import JshCustomerModal from './JshCustomerModal.vue';
  import JshCustomerAddressList from './JshCustomerAddressList.vue';
  import { columns, searchFormSchema, superQuerySchema } from './customer.data';
  import { list, deleteOne, batchDelete, updateStatus, getExportUrl, getImportUrl } from './customer.api';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  const { tableContext, onExportXls, onImportXls, createMessage } = useListPage({
    tableProps: {
      title: '客户信息',
      api: list,
      columns,
      formConfig: {
        schemas: searchFormSchema,
        labelWidth: 60,
      },
      showIndexColumn: true,
      // 老页面：单选 + 点击行即选中
      rowSelection: { type: 'radio' },
      clickToRowSelect: true,
      defSort: { column: 'id', order: 'desc' },
      pagination: { pageSize: 5, pageSizeOptions: ['5', '10', '50'] },
      actionColumn: { width: 147, fixed: 'right' },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
      // 老页面 loadData 每次刷新都会清空选中（子表随之回到未选中状态）
      afterFetch: (items) => {
        selectedRowKeys.value = [];
        return items;
      },
    },
    exportConfig: {
      name: 'jsh_customer',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  // Long id 按字符串传给子表
  const selectedMainId = computed(() => {
    const key = selectedRowKeys.value[0];
    return key == null ? '' : String(key);
  });

  const superQueryConfig = reactive(superQuerySchema);

  function handleSuperQuery(params) {
    Object.keys(params).forEach((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function getActions(record) {
    return [
      { label: '编辑', onClick: handleEdit.bind(null, record) },
      {
        label: record.status === 1 ? '禁用' : '启用',
        popConfirm: {
          title: `确定${record.status === 1 ? '禁用' : '启用'}吗?`,
          confirm: handleStatus.bind(null, record),
        },
      },
    ];
  }

  function getDropDownActions(record) {
    return [
      { label: '详情', onClick: handleDetail.bind(null, record) },
      { label: '删除', popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record) } },
    ];
  }

  function handleAdd() {
    openModal(true, { isUpdate: false, showFooter: true });
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true, showFooter: true });
  }

  function handleDetail(record) {
    openModal(true, { record, isUpdate: true, showFooter: false, title: '详情' });
  }

  async function handleStatus(record) {
    await updateStatus({ id: String(record.id), status: record.status });
    createMessage.success('操作成功!');
    reload();
  }

  async function handleDelete(record) {
    await deleteOne({ id: record.id }, reload);
  }

  function batchHandleDelete() {
    batchDelete(selectedRowKeys.value, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }
</script>
