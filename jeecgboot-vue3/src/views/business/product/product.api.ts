import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/business/product/jshProduct/list',
  save = '/business/product/jshProduct/add',
  edit = '/business/product/jshProduct/edit',
  get = '/business/product/jshProduct/queryById',
  delete = '/business/product/jshProduct/delete',
  deleteBatch = '/business/product/jshProduct/deleteBatch',
  importExcel = '/business/product/jshProduct/importExcel',
  exportXls = '/business/product/jshProduct/exportXls',
}

export const getExportUrl = Api.exportXls;
export const getImportUrl = Api.importExcel;

/**
 * 分页列表
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 详情（id 为字符串，保持 19 位 Long 精度）
 */
export const queryById = (params) => defHttp.get({ url: Api.get, params });

/**
 * 保存或更新：后端 add 为 POST，edit 为 PUT
 */
export const saveOrUpdate = (params, isUpdate: boolean) => {
  return isUpdate ? defHttp.put({ url: Api.edit, params }) : defHttp.post({ url: Api.save, params });
};

/**
 * 单条删除
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 */
export const batchDelete = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
