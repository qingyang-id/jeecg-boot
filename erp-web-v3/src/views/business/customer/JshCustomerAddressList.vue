<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
        <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="handleImport">导入</j-upload-button>
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
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>
    <JshCustomerAddressModal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" name="JshCustomerAddressList" setup>
  import { watch } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import JshCustomerAddressModal from './JshCustomerAddressModal.vue';
  import { addressColumns } from './customer.data';
  import { addressList, deleteAddress, batchDeleteAddress, addressExportUrl, getAddressImportUrl } from './customer.api';

  const props = defineProps({
    // 主表（客户）id，Long 按字符串处理；为空时与老页面一致：不带 customerId 查询全部
    mainId: { type: String, default: '' },
  });

  const [registerModal, { openModal }] = useModal();

  const { tableContext, onExportXls, onImportXls, createMessage } = useListPage({
    tableProps: {
      api: addressList,
      columns: addressColumns,
      useSearchForm: false,
      showIndexColumn: true,
      canResize: false,
      defSort: { column: 'id', order: 'desc' },
      pagination: { pageSize: 10, pageSizeOptions: ['10', '20', '30'] },
      actionColumn: { width: 147, fixed: 'right' },
      beforeFetch: (params) => {
        if (props.mainId) {
          params.customerId = props.mainId;
        }
        return params;
      },
    },
    exportConfig: {
      name: '客户地址',
      url: addressExportUrl,
      params: () => (props.mainId ? { customerId: props.mainId } : {}),
    },
    importConfig: {
      url: () => getAddressImportUrl(props.mainId),
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  watch(
    () => props.mainId,
    () => {
      selectedRowKeys.value = [];
      reload({ page: 1 });
    }
  );

  function getActions(record) {
    return [
      { label: '编辑', onClick: handleEdit.bind(null, record) },
      { label: '删除', popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record) } },
    ];
  }

  function handleAdd() {
    openModal(true, { isUpdate: false, mainId: props.mainId });
  }

  function handleEdit(record) {
    openModal(true, { isUpdate: true, record });
  }

  async function handleDelete(record) {
    await deleteAddress({ id: record.id }, reload);
  }

  function batchHandleDelete() {
    batchDeleteAddress(selectedRowKeys.value, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }

  /** 导入地址需要主表 id（后端路径变量 /{mainId}） */
  function handleImport(file) {
    if (!props.mainId) {
      createMessage.warning('请先选择客户');
      return Promise.reject();
    }
    return onImportXls(file);
  }
</script>
