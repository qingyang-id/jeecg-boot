import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { BasicColumn, FormSchema } from '/@/components/Table';

/* ---------------- 主表：客户 ---------------- */
export const columns: BasicColumn[] = [
  { title: '客户ID', align: 'center', dataIndex: 'id', width: 100 },
  { title: '客户名字', align: 'center', dataIndex: 'name' },
  { title: '性别', align: 'center', dataIndex: 'sex_dictText', width: 80 },
  { title: '常用地址', align: 'center', dataIndex: 'address' },
  { title: '手机', align: 'center', dataIndex: 'phone' },
  { title: '排序', align: 'center', dataIndex: 'sortNo', sorter: true, width: 100 },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      if (text == 0) return h(Tag, { color: 'pink' }, () => '禁用');
      if (text == 1) return h(Tag, { color: '#87d068' }, () => '正常');
      return '';
    },
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名',
    field: 'name',
    component: 'JInput',
    componentProps: { placeholder: '输入姓名' },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'Select',
    componentProps: {
      placeholder: '请选择性别',
      options: [
        { label: '请选择', value: '' },
        { label: '男', value: '1' },
        { label: '女', value: '2' },
      ],
    },
  },
];

export const formSchema: FormSchema[] = [
  { label: '', field: 'id', component: 'Input', show: false },
  {
    label: '客户名字',
    field: 'name',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入客户名字' },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    defaultValue: 1,
    componentProps: { dictCode: 'sex', type: 'radioButton', stringToNumber: true, placeholder: '请选择性别' },
  },
  {
    label: '联系地址',
    field: 'address',
    component: 'Input',
    componentProps: { placeholder: '请输入联系地址' },
  },
  {
    label: '手机',
    field: 'phone',
    component: 'Input',
    componentProps: { placeholder: '请输入手机' },
    rules: [{ pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!' }],
  },
  {
    label: '排序',
    field: 'sortNo',
    component: 'InputNumber',
    componentProps: { min: 0, max: 100000, placeholder: '请输入排序值，越小越靠前' },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'status', type: 'radioButton', stringToNumber: true, placeholder: '请选择状态' },
  },
];

/** 高级查询（对应老页面 j-super-query 的 superFieldList） */
export const superQuerySchema = {
  name: { title: '客户名字', order: 0, view: 'text', type: 'string' },
  sex: { title: '客户性别', order: 1, view: 'radio', type: 'string', dictCode: 'sex' },
  address: { title: '联系地址', order: 2, view: 'text', type: 'string' },
  phone: { title: '手机', order: 3, view: 'text', type: 'string' },
};

/* ---------------- 子表：客户地址 ---------------- */
export const addressColumns: BasicColumn[] = [
  { title: '创建时间', align: 'center', dataIndex: 'createTime' },
  { title: '客户', align: 'center', dataIndex: 'customerId_dictText' },
  { title: '地址', align: 'center', dataIndex: 'address' },
];

export const addressFormSchema: FormSchema[] = [
  { label: '', field: 'id', component: 'Input', show: false },
  {
    label: '客户',
    field: 'customerId',
    component: 'JSearchSelect',
    required: true,
    componentProps: { dict: 'jsh_customer,name,id' },
  },
  {
    label: '地址',
    field: 'address',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入地址' },
  },
];
