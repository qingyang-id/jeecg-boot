import { ref } from 'vue';
import { BasicColumn, FormSchema } from '/@/components/Table';
import { JVxeColumn, JVxeTypes } from '/@/components/jeecg/JVxeTable/types';
import { customerList } from './order.api';

/* ======================= 公共 ======================= */

/** 老页面统一分页：pageSize 10，可选 5/10/30/50 */
export const pagination = { pageSize: 10, pageSizeOptions: ['5', '10', '30', '50'] };
/** 统计页分页：可选 5/10/50 */
export const summaryPagination = { pageSize: 10, pageSizeOptions: ['5', '10', '50'] };

const divide = (t, n) => (t === null || t === undefined || t === '' ? t : t / n);

/** 规格(高*宽*数量) */
const specRender = ({ record }) => `${record.height} * ${record.width} * ${record.num}`;
/** 方向：direction 为空时不显示 */
const directionRender = ({ text, record }) => (!record.direction ? '' : text);
/** 抽/拉/条：数量为空时不显示 */
const extendRender = ({ record }) => (!record.extendNum ? '' : `${record.extendType_dictText} * ${record.extendNum}`);

/**
 * 客户下拉（老：initDictConfig，/jshCustomer/list?status=1&pageSize=5000&column=rank&order=asc，按 rank 排序）
 * 多个页面共用一份
 */
export const customerOptions = ref<{ value: string; text: string; title: string }[]>([]);
let customerLoading: Promise<void> | null = null;
export function loadCustomerOptions(force = false) {
  if (customerLoading && !force) return customerLoading;
  customerLoading = customerList({ status: 1, pageSize: 5000, column: 'rank', order: 'asc' })
    .then((res) => {
      const records = (res && res.records) || [];
      customerOptions.value = records
        .sort((a, b) => a.rank - b.rank)
        .map((item) => ({ value: `${item.id}`, text: item.name, title: item.name }));
    })
    .catch(() => {
      customerLoading = null;
    });
  return customerLoading;
}

/**
 * 日期区间 -> xxx_begin / xxx_end
 * 列表查询时 fieldMapToTime 已转换；导出时 useListPage 用的是表单原始值，需要在 beforeFetch 里再转一次
 */
export function mapRangeParams(params: Recordable, field: string, begin: string, end: string) {
  const v = params[field];
  if (Array.isArray(v)) {
    if (v[0]) params[begin] = v[0];
    if (v[1]) params[end] = v[1];
  }
  delete params[field];
  return params;
}

/* ======================= 订单列表 ======================= */

export const orderColumns: BasicColumn[] = [
  { title: '订单编码', align: 'center', sorter: true, dataIndex: 'orderCode' },
  { title: '客户', align: 'center', dataIndex: 'customerId_dictText' },
  { title: '总面积(m²)', align: 'center', dataIndex: 'totalArea', customRender: ({ text }) => divide(text, 1000000) },
  { title: '总价(元)', align: 'center', dataIndex: 'totalPrice', customRender: ({ text }) => divide(text, 100) },
  { title: '下单时间', align: 'center', sorter: true, dataIndex: 'orderTime', width: 180 },
  { title: '地址', align: 'center', dataIndex: 'address' },
  { title: '备注', align: 'center', dataIndex: 'remark' },
  { title: '更新时间', align: 'center', sorter: true, dataIndex: 'updateTime', width: 180 },
];

export const orderSearchFormSchema: FormSchema[] = [
  { label: '订单编码', field: 'orderCode', component: 'Input', componentProps: { placeholder: '请输入订单编码' } },
  {
    label: '客户',
    field: 'customerId',
    component: 'JSearchSelect',
    componentProps: () => ({ placeholder: '请选择客户', dictOptions: customerOptions.value }),
  },
  {
    label: '订单日期',
    field: 'orderTimeRange',
    component: 'RangePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD', format: 'YYYY-MM-DD', placeholder: ['开始时间', '结束时间'] },
  },
];

/** 高级查询（老 superFieldList） */
export const orderSuperQuerySchema = {
  orderCode: { title: '订单编码', order: 0, view: 'text', type: 'string' },
  customerId: { title: '客户', order: 1, view: 'sel_search', type: 'string', dictTable: 'jsh_customer', dictText: 'name', dictCode: 'id' },
  orderTime: { title: '下单时间', order: 2, view: 'date', type: 'string' },
  address: { title: '地址', order: 3, view: 'text', type: 'string' },
  remark: { title: '备注', order: 4, view: 'text', type: 'string' },
};

/* ======================= 订单商品（原尺寸） ======================= */

export const orderProductColumns: BasicColumn[] = [
  { title: '原尺寸ID', align: 'center', dataIndex: 'id' },
  { title: '订单号', align: 'center', dataIndex: 'orderId_dictText' },
  { title: '产品', align: 'center', dataIndex: 'productId_dictText' },
  { title: '铝材颜色', align: 'center', dataIndex: 'color' },
  { title: '玻璃色号', align: 'center', dataIndex: 'glassColor' },
  { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
  { title: '方向', align: 'center', dataIndex: 'direction_dictText', customRender: directionRender },
  { title: '抽/拉/条', align: 'left', dataIndex: 'extendType_dictText', customRender: extendRender },
  { title: '单价(元)', align: 'center', dataIndex: 'price', customRender: ({ text }) => divide(text, 100) },
  { title: '附加(元)', align: 'center', dataIndex: 'extendPrice', customRender: ({ text }) => divide(text, 100) },
  { title: '总面积(m²)', align: 'center', dataIndex: 'totalArea', customRender: ({ text }) => divide(text, 1000000) },
  { title: '总价(元)', align: 'center', dataIndex: 'totalPrice', customRender: ({ text }) => divide(text, 100) },
];

/** 老 JshOrderProductList 的查询区（search=true 时显示） */
export const orderProductSearchFormSchema: FormSchema[] = orderSearchFormSchema;

/* ======================= 铝材 / 玻璃明细 ======================= */

export const aluminumDetailColumns: BasicColumn[] = [
  { title: '原尺寸ID', align: 'center', dataIndex: 'orderProductId' },
  { title: '订单号', align: 'center', dataIndex: 'orderId_dictText' },
  { title: '产品', align: 'center', dataIndex: 'productId_dictText' },
  { title: '颜色', align: 'center', dataIndex: 'color' },
  {
    title: '原尺寸(高*宽)',
    align: 'left',
    dataIndex: 'referenceHeight',
    customRender: ({ record }) => `${record.referenceHeight} * ${record.referenceWidth}`,
  },
  { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
  { title: '方向', align: 'center', dataIndex: 'direction_dictText', customRender: directionRender },
  { title: '抽/拉/条', align: 'left', dataIndex: 'extendType_dictText', customRender: extendRender },
];

export const glassDetailColumns: BasicColumn[] = [
  { title: '原尺寸ID', align: 'center', dataIndex: 'orderProductId' },
  { title: '订单号', align: 'center', dataIndex: 'orderId_dictText' },
  { title: '产品', align: 'center', dataIndex: 'productId_dictText' },
  { title: '色号', align: 'center', dataIndex: 'color' },
  { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
];

/* ======================= 订单商品扩展 ======================= */

export const orderProductExtendColumns: BasicColumn[] = [
  { title: '订单ID', align: 'center', dataIndex: 'orderId' },
  { title: '订单产品表ID', align: 'center', dataIndex: 'orderProductId' },
  { title: '类型', align: 'center', dataIndex: 'type_dictText' },
  { title: '数量', align: 'center', dataIndex: 'num' },
];

export const orderProductExtendFormSchema: FormSchema[] = [
  { label: '', field: 'id', component: 'Input', show: false },
  {
    label: '订单ID',
    field: 'orderId',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入订单ID', disabled: true },
  },
  {
    label: '订单产品表ID',
    field: 'orderProductId',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入订单产品表ID', disabled: true },
  },
  {
    label: '类型',
    field: 'type',
    component: 'JDictSelectTag',
    required: true,
    componentProps: { dictCode: 'product_extend', type: 'list', placeholder: '请选择类型' },
  },
  {
    label: '数量',
    field: 'num',
    component: 'InputNumber',
    required: true,
    componentProps: { placeholder: '请输入数量', disabled: true, style: { width: '100%' } },
  },
];

/** 老 JshOrderProductExtendList superFieldList */
export const orderProductExtendSuperQuerySchema = {
  delFlag: { title: '删除状态:0未删除,1删除', order: 0, view: 'number', type: 'number' },
  customerId: { title: '客户id', order: 1, view: 'number', type: 'number' },
  orderId: { title: '订单ID', order: 2, view: 'number', type: 'number' },
  productId: { title: '产品id', order: 3, view: 'number', type: 'number' },
  width: { title: '宽', order: 4, view: 'number', type: 'number' },
  height: { title: '高', order: 5, view: 'number', type: 'number' },
  num: { title: '数量', order: 6, view: 'number', type: 'number' },
  direction: { title: '方向:0其他,1双开,2左开,3右开', order: 7, view: 'number', type: 'number' },
  color: { title: '颜色', order: 8, view: 'text', type: 'string' },
  price: { title: '单价', order: 9, view: 'number', type: 'number' },
  totalPrice: { title: '总价', order: 10, view: 'number', type: 'number' },
};

/* ======================= 订单录入：明细 JVxeTable 列 ======================= */

/** 老 JshOrderForm jshOrderProductTable.columns；产品列 options 运行时由商品列表填充 */
export function createOrderProductVxeColumns(): JVxeColumn[] {
  return [
    {
      title: '产品',
      key: 'productId',
      type: JVxeTypes.select,
      options: [],
      allowSearch: true,
      width: '10%',
      minWidth: '100px',
      placeholder: '请选择${title}',
      defaultValue: '',
      validateRules: [{ required: true, message: '${title}不能为空' }],
    },
    {
      title: '铝材颜色',
      key: 'color',
      type: JVxeTypes.select,
      width: '8%',
      minWidth: '100px',
      placeholder: '请输入${title}',
      defaultValue: '',
      options: [
        { text: '默认', value: '' },
        { text: '黑色', value: '黑色' },
        { text: '金色', value: '金色' },
        { text: '亮色', value: '亮色' },
        { text: '哑光', value: '哑光' },
      ],
    },
    {
      title: '玻璃色号',
      key: 'glassColor',
      type: JVxeTypes.input,
      width: '8%',
      minWidth: '100px',
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '高(mm)',
      key: 'height',
      type: JVxeTypes.inputNumber,
      width: '8%',
      minWidth: '80px',
      placeholder: '请输入${title}',
      defaultValue: '',
      validateRules: [{ required: true, message: '${title}不能为空' }],
    },
    {
      title: '宽(mm)',
      key: 'width',
      type: JVxeTypes.inputNumber,
      width: '8%',
      minWidth: '80px',
      placeholder: '请输入${title}',
      defaultValue: '',
      validateRules: [{ required: true, message: '${title}不能为空' }],
    },
    {
      title: '数量',
      key: 'num',
      type: JVxeTypes.inputNumber,
      width: '6%',
      minWidth: '80px',
      placeholder: '请输入${title}',
      defaultValue: '',
      validateRules: [{ required: true, message: '${title}不能为空' }],
    },
    {
      title: '方向',
      key: 'direction',
      type: JVxeTypes.select,
      options: [],
      dictCode: 'direction',
      width: '8%',
      minWidth: '80px',
      placeholder: '请选择${title}',
      defaultValue: '0',
    },
    {
      title: '抽/拉/条',
      key: 'extendType',
      type: JVxeTypes.select,
      options: [],
      dictCode: 'product_extend',
      width: '10%',
      minWidth: '80px',
      placeholder: '请选择${title}',
      defaultValue: '0',
    },
    {
      title: '抽/拉/条数量',
      key: 'extendNum',
      type: JVxeTypes.inputNumber,
      width: '8%',
      minWidth: '80px',
      placeholder: '请输入${title}',
      defaultValue: 0,
      validateRules: [
        {
          // 有抽/拉/条类型时数量必填（老 JshOrderForm.vue:238-251）
          handler({ cellValue, row }, callback) {
            const extendNum = cellValue;
            if (Number(row.extendType) && !extendNum) {
              callback(false, '${title}不正确');
            } else {
              callback(true);
            }
          },
          message: '${title}不正确',
        },
      ],
    },
    {
      title: '单价(元)',
      key: 'price',
      type: JVxeTypes.input,
      disabled: true,
      width: '8%',
      minWidth: '80px',
      placeholder: '${title}',
      defaultValue: '0',
    },
    {
      title: '附加(元)',
      key: 'extendPrice',
      type: JVxeTypes.input,
      disabled: true,
      width: '8%',
      minWidth: '80px',
      placeholder: '${title}',
      defaultValue: '0',
    },
    {
      title: '总面积(m²)',
      key: 'totalArea',
      type: JVxeTypes.input,
      disabled: true,
      width: '8%',
      minWidth: '80px',
      placeholder: '${title}',
      defaultValue: '0',
    },
    {
      title: '总价(元)',
      key: 'totalPrice',
      type: JVxeTypes.input,
      disabled: true,
      width: '8%',
      minWidth: '80px',
      placeholder: '${title}',
      defaultValue: '0',
    },
    {
      title: '操作',
      key: 'action',
      type: JVxeTypes.slot,
      fixed: 'right',
      width: '6%',
      minWidth: '80px',
      align: 'center',
      slotName: 'action',
    },
  ];
}

/* ======================= 统计 ======================= */

export const summaryDailyColumns: BasicColumn[] = [
  {
    title: '时间',
    align: 'center',
    sorter: true,
    dataIndex: 'time',
    customRender: ({ text }) => (!text ? '' : text.length > 10 ? text.substr(0, 10) : text),
  },
  { title: '客户', align: 'center', dataIndex: 'customerId_dictText' },
  { title: '总订单数', align: 'center', dataIndex: 'totalNum' },
  { title: '总面积(m²)', align: 'center', dataIndex: 'totalArea', customRender: ({ text }) => divide(text, 1000000) },
  { title: '总价(元)', align: 'center', dataIndex: 'totalPrice', customRender: ({ text }) => divide(text, 100) },
];

export const summaryMonthlyColumns: BasicColumn[] = [
  {
    title: '时间',
    align: 'center',
    sorter: true,
    dataIndex: 'time',
    customRender: ({ text }) => (!text ? '' : text.length > 7 ? text.substr(0, 7) : text),
  },
  { title: '客户', align: 'center', dataIndex: 'customerId_dictText' },
  { title: '总订单数', align: 'center', dataIndex: 'totalNum' },
  { title: '总面积(m²)', align: 'center', dataIndex: 'totalArea', customRender: ({ text }) => divide(text, 1000000) },
  { title: '总价(元)', align: 'center', dataIndex: 'totalPrice', customRender: ({ text }) => divide(text, 100) },
];

/** 统计页查询：customerId 固定（总账 '0'）时隐藏客户条件 */
export function summaryDailySearchSchema(fixedCustomer: boolean): FormSchema[] {
  return [
    {
      label: '客户',
      field: 'customerId',
      component: 'JSearchSelect',
      ifShow: !fixedCustomer,
      componentProps: () => ({ placeholder: '请选择客户', dictOptions: customerOptions.value }),
    },
    {
      label: '日期',
      field: 'timeRange',
      component: 'RangePicker',
      componentProps: { valueFormat: 'YYYY-MM-DD', format: 'YYYY-MM-DD', placeholder: ['开始时间', '结束时间'] },
    },
  ];
}

export function summaryMonthlySearchSchema(fixedCustomer: boolean): FormSchema[] {
  return [
    {
      label: '客户',
      field: 'customerId',
      component: 'JSearchSelect',
      ifShow: !fixedCustomer,
      componentProps: () => ({ placeholder: '请选择客户', dictOptions: customerOptions.value }),
    },
    {
      label: '月份',
      field: 'month',
      component: 'MonthPicker',
      componentProps: { valueFormat: 'YYYY-MM', format: 'YYYY-MM', placeholder: '请选择月份' },
    },
  ];
}
