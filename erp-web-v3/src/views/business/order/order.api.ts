import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

/**
 * 订单模块接口（URL 与老前端 / 后端 controller 保持一致）
 * 后端：jeecg-module-erp/.../business/order/controller/*
 * id 一律按字符串原样传递，不做 Number 转换（19 位 Long 精度）
 */
enum Api {
  // 订单主表
  orderList = '/business/order/jshOrder/list',
  orderAdd = '/business/order/jshOrder/add',
  orderEdit = '/business/order/jshOrder/edit',
  orderDelete = '/business/order/jshOrder/delete',
  orderDeleteBatch = '/business/order/jshOrder/deleteBatch',
  orderQueryById = '/business/order/jshOrder/queryById',
  orderProductByMainId = '/business/order/jshOrder/queryJshOrderProductByMainId',
  orderExportXls = '/business/order/jshOrder/exportXls',
  orderImportExcel = '/business/order/jshOrder/importExcel',
  // 订单商品（原尺寸）
  orderProductList = '/business/order/jshOrderProduct/list',
  orderProductExportXls = '/business/order/jshOrderProduct/exportXls',
  // 铝材 / 玻璃明细
  orderProductDetailList = '/business/order/jshOrderProductDetail/list',
  orderProductDetailExportXls = '/business/order/jshOrderProductDetail/exportXls',
  // 订单商品扩展（抽/拉/条）
  orderProductExtendList = '/business/order/jshOrderProductExtend/list',
  orderProductExtendAdd = '/business/order/jshOrderProductExtend/add',
  orderProductExtendEdit = '/business/order/jshOrderProductExtend/edit',
  orderProductExtendDelete = '/business/order/jshOrderProductExtend/delete',
  orderProductExtendDeleteBatch = '/business/order/jshOrderProductExtend/deleteBatch',
  orderProductExtendExportXls = '/business/order/jshOrderProductExtend/exportXls',
  orderProductExtendImportExcel = '/business/order/jshOrderProductExtend/importExcel',
  // 统计
  summaryDailyList = '/business/order/jshOrderSummaryDaily/list',
  summaryDailyExportXls = '/business/order/jshOrderSummaryDaily/exportXls',
  summaryMonthlyList = '/business/order/jshOrderSummaryMonthly/list',
  summaryMonthlyExportXls = '/business/order/jshOrderSummaryMonthly/exportXls',
  // 订单页面用到的客户 / 商品接口（不依赖 customer/、product/ 目录）
  customerList = '/business/customer/jshCustomer/list',
  productList = '/business/product/jshProduct/list',
  productQueryById = '/business/product/jshProduct/queryById',
}

export const orderExportUrl = Api.orderExportXls;
export const orderImportUrl = Api.orderImportExcel;
export const orderProductExportUrl = Api.orderProductExportXls;
export const orderProductDetailExportUrl = Api.orderProductDetailExportXls;
export const orderProductExtendExportUrl = Api.orderProductExtendExportXls;
export const orderProductExtendImportUrl = Api.orderProductExtendImportExcel;
export const summaryDailyExportUrl = Api.summaryDailyExportXls;
export const summaryMonthlyExportUrl = Api.summaryMonthlyExportXls;

/* ---------------- 订单主表 ---------------- */
export const orderList = (params) => defHttp.get({ url: Api.orderList, params });

export const orderQueryById = (params) => defHttp.get({ url: Api.orderQueryById, params });

/** 新增 POST /add，编辑 PUT /edit（与后端 @PostMapping/@PutMapping 一致） */
export const orderSaveOrUpdate = (params, isUpdate: boolean) => {
  return isUpdate ? defHttp.put({ url: Api.orderEdit, params }) : defHttp.post({ url: Api.orderAdd, params });
};

export const orderDelete = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.orderDelete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

export const orderDeleteBatch = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.orderDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/** 订单商品（编辑时加载子表），返回数组 */
export const orderProductByMainId = (params) => defHttp.get({ url: Api.orderProductByMainId, params });

/* ---------------- 订单商品 / 明细 ---------------- */
export const orderProductList = (params) => defHttp.get({ url: Api.orderProductList, params });

export const orderProductDetailList = (params) => defHttp.get({ url: Api.orderProductDetailList, params });

/* ---------------- 订单商品扩展 ---------------- */
export const orderProductExtendList = (params) => defHttp.get({ url: Api.orderProductExtendList, params });

export const orderProductExtendSaveOrUpdate = (params, isUpdate: boolean) => {
  return isUpdate
    ? defHttp.put({ url: Api.orderProductExtendEdit, params })
    : defHttp.post({ url: Api.orderProductExtendAdd, params });
};

export const orderProductExtendDelete = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.orderProductExtendDelete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

export const orderProductExtendDeleteBatch = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.orderProductExtendDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/* ---------------- 统计 ---------------- */
export const summaryDailyList = (params) => defHttp.get({ url: Api.summaryDailyList, params });

export const summaryMonthlyList = (params) => defHttp.get({ url: Api.summaryMonthlyList, params });

/* ---------------- 客户 / 商品 ---------------- */
export const customerList = (params) => defHttp.get({ url: Api.customerList, params });

export const productList = (params) => defHttp.get({ url: Api.productList, params });

export const productQueryById = (params) => defHttp.get({ url: Api.productQueryById, params });
