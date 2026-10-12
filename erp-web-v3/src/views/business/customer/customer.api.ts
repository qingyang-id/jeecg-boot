import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/business/customer/jshCustomer/list',
  save = '/business/customer/jshCustomer/add',
  edit = '/business/customer/jshCustomer/edit',
  updateStatus = '/business/customer/jshCustomer/status',
  deleteOne = '/business/customer/jshCustomer/delete',
  deleteBatch = '/business/customer/jshCustomer/deleteBatch',
  exportXls = '/business/customer/jshCustomer/exportXls',
  importExcel = '/business/customer/jshCustomer/importExcel',
  // 子表：客户地址
  addressList = '/business/customer/jshCustomer/listJshCustomerAddressByMainId',
  addressSave = '/business/customer/jshCustomer/addJshCustomerAddress',
  addressEdit = '/business/customer/jshCustomer/editJshCustomerAddress',
  addressDelete = '/business/customer/jshCustomer/deleteJshCustomerAddress',
  addressDeleteBatch = '/business/customer/jshCustomer/deleteBatchJshCustomerAddress',
  addressExportXls = '/business/customer/jshCustomer/exportJshCustomerAddress',
  addressImport = '/business/customer/jshCustomer/importJshCustomerAddress',
}

export const getExportUrl = Api.exportXls;
export const getImportUrl = Api.importExcel;

export const list = (params) => defHttp.get({ url: Api.list, params });

/** 新增 POST /add，编辑 PUT /edit（与后端注解一致） */
export const saveOrUpdate = (params, isUpdate: boolean) => {
  return isUpdate ? defHttp.put({ url: Api.edit, params }) : defHttp.post({ url: Api.save, params });
};

/** 启用/禁用：后端按当前 status 取反 */
export const updateStatus = (params: { id: string; status: number | string }) => {
  return defHttp.post({ url: Api.updateStatus, params });
};

export const deleteOne = (params: { id: string }, handleSuccess: () => void) => {
  return defHttp.delete({ url: Api.deleteOne, data: params }, { joinParamsToUrl: true }).then(() => handleSuccess());
};

function confirmBatchDelete(url: string, ids: string[], handleSuccess: () => void) {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url, data: { ids: ids.join(',') } }, { joinParamsToUrl: true }).then(() => handleSuccess());
    },
  });
}

export const batchDelete = (ids: string[], handleSuccess: () => void) => confirmBatchDelete(Api.deleteBatch, ids, handleSuccess);

/* ---------------- 子表：客户地址 ---------------- */
export const addressExportUrl = Api.addressExportXls;
/** 导入地址需带主表 id：/importJshCustomerAddress/{mainId} */
export const getAddressImportUrl = (mainId: string) => `${Api.addressImport}/${mainId}`;

export const addressList = (params) => defHttp.get({ url: Api.addressList, params });

export const saveOrUpdateAddress = (params, isUpdate: boolean) => {
  return isUpdate ? defHttp.put({ url: Api.addressEdit, params }) : defHttp.post({ url: Api.addressSave, params });
};

export const deleteAddress = (params: { id: string }, handleSuccess: () => void) => {
  return defHttp.delete({ url: Api.addressDelete, data: params }, { joinParamsToUrl: true }).then(() => handleSuccess());
};

export const batchDeleteAddress = (ids: string[], handleSuccess: () => void) =>
  confirmBatchDelete(Api.addressDeleteBatch, ids, handleSuccess);
