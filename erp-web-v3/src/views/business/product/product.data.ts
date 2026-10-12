import { BasicColumn, FormSchema } from '/@/components/Table';

/** 金额：库里存分，页面显示元 */
const fenToYuan = ({ text }) => (text === null || text === undefined || text === '' ? text : text / 100);

const moneyPattern = /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/;
const intPattern = /^-?\d+$/;

export const columns: BasicColumn[] = [
  { title: '名称', align: 'center', dataIndex: 'name' },
  { title: '商品条码', align: 'center', dataIndex: 'barcode' },
  { title: '采购价格(元)', align: 'center', dataIndex: 'purchasePrice', customRender: fenToYuan },
  { title: '销售价格(元)', align: 'center', dataIndex: 'wholesalePrice', customRender: fenToYuan },
  { title: '铝材宽度差(mm)', align: 'center', dataIndex: 'aluminumWidthDiff' },
  { title: '铝材高度差(mm)', align: 'center', dataIndex: 'aluminumHeightDiff' },
  { title: '玻璃宽度差(mm)', align: 'center', dataIndex: 'glassWidthDiff' },
  { title: '玻璃高度差(mm)', align: 'center', dataIndex: 'glassHeightDiff' },
  { title: '备注', align: 'center', dataIndex: 'remark' },
  { title: '状态', align: 'center', dataIndex: 'status_dictText' },
];

export const searchFormSchema: FormSchema[] = [
  { label: '名称', field: 'name', component: 'Input', componentProps: { placeholder: '请输入名称' } },
  { label: '商品条码', field: 'barcode', component: 'Input', componentProps: { placeholder: '请输入商品条码' } },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      placeholder: '请选择',
      options: [
        { label: '正常', value: '1' },
        { label: '禁用', value: '0' },
      ],
    },
  },
];

export const formSchema: FormSchema[] = [
  { label: '', field: 'id', component: 'Input', show: false },
  {
    label: '名称',
    field: 'name',
    component: 'Input',
    componentProps: { placeholder: '请输入名称' },
    dynamicRules: () => [{ required: true, message: '请输入名称!' }],
  },
  {
    label: '商品条码',
    field: 'barcode',
    component: 'Input',
    componentProps: { placeholder: '请输入商品条码' },
    // 编辑时条码不可改
    dynamicDisabled: ({ values }) => !!values.id,
  },
  {
    label: '采购价格(元)',
    field: 'purchasePrice',
    component: 'InputNumber',
    componentProps: { placeholder: '请输入采购价格', style: { width: '100%' } },
    dynamicRules: () => [
      { required: true, message: '请输入采购价格!' },
      { pattern: moneyPattern, message: '请输入正确的金额!' },
    ],
  },
  {
    label: '销售价格(元)',
    field: 'wholesalePrice',
    component: 'InputNumber',
    componentProps: { placeholder: '请输入销售价格', style: { width: '100%' } },
    dynamicRules: () => [
      { required: true, message: '请输入采购价格!' },
      { pattern: moneyPattern, message: '请输入正确的金额!' },
    ],
  },
  ...(
    [
      ['aluminumWidthDiff', '铝材宽度差'],
      ['aluminumHeightDiff', '铝材高度差'],
      ['glassWidthDiff', '玻璃宽度差'],
      ['glassHeightDiff', '玻璃高度差'],
    ] as const
  ).map(
    ([field, name]): FormSchema => ({
      label: `${name}(mm)`,
      field,
      component: 'InputNumber',
      componentProps: { placeholder: `请输入${name}`, style: { width: '100%' } },
      dynamicRules: () => [
        { required: true, message: `请属于${name}!` },
        { pattern: intPattern, message: '请输入正确的数量!' },
      ],
    })
  ),
  { label: '备注', field: 'remark', component: 'Input', componentProps: { placeholder: '请输入备注' } },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    defaultValue: 1,
    componentProps: { dictCode: 'status', type: 'radioButton', stringToNumber: true, placeholder: '请选择状态' },
  },
];

/** 高级查询（对应老页面 superFieldList） */
export const superQuerySchema = {
  name: { title: '名称', order: 0, view: 'text', type: 'string' },
  barcode: { title: '商品条码', order: 1, view: 'text', type: 'string' },
  purchasePrice: { title: '采购价格', order: 2, view: 'number', type: 'number' },
  wholesalePrice: { title: '销售价格', order: 3, view: 'number', type: 'number' },
  aluminumWidthDiff: { title: '铝材宽度差(mm)', order: 4, view: 'number', type: 'number' },
  aluminumHeightDiff: { title: '铝材高度差(mm)', order: 5, view: 'number', type: 'number' },
  glassWidthDiff: { title: '玻璃宽度差(mm)', order: 6, view: 'number', type: 'number' },
  glassHeightDiff: { title: '玻璃高度差(mm)', order: 7, view: 'number', type: 'number' },
  remark: { title: '备注', order: 8, view: 'text', type: 'string' },
  status: { title: '状态', order: 9, view: 'list', type: 'number', dictCode: 'status' },
};
