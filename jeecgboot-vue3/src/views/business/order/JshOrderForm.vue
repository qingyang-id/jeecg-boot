<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <a-form ref="formRef" :model="model" :rules="validatorRules" :labelCol="labelCol" :wrapperCol="wrapperCol" :disabled="formDisabled">
      <a-row>
        <a-col :md="8" :sm="24">
          <a-form-item label="订单编码" name="orderCode">
            <a-input v-model:value="model.orderCode" placeholder="订单编码" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="总面积(m²)" name="totalArea">
            <a-input v-model:value="model.totalArea" placeholder="总面积(m²)" disabled style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="订单总额(元)" name="totalPrice">
            <a-input v-model:value="model.totalPrice" placeholder="订单总额" disabled style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="客户" name="customerId">
            <JSearchSelect v-model:value="model.customerId" :dictOptions="customerOptions" placeholder="请选择" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="地址" name="address">
            <a-input v-model:value="model.address" placeholder="地址" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="下单时间" name="orderTime">
            <a-date-picker
              v-model:value="model.orderTime"
              placeholder="请选择下单时间"
              showTime
              valueFormat="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
              :disabled="!!model.id"
            />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item label="备注" name="remark">
            <a-textarea v-model:value="model.remark" :rows="4" placeholder="请输入备注" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="订单商品" key="jshOrderProduct" :forceRender="true">
        <JVxeTable
          ref="tableRef"
          :class="{ 'is-editable': !formDisabled }"
          keepSource
          :loading="tableLoading"
          :columns="columns"
          :dataSource="dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          rowNumber
          rowSelection
          toolbar
          @removed="handleRemoved"
          @valueChange="handleValueChange"
        >
          <template #toolbarSuffix>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('height')">高-批量设置</a-button>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('width')">宽-批量设置</a-button>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('num')">数量-批量设置</a-button>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('extendNum')">抽/拉/条数量-批量设置</a-button>
          </template>
          <template #action="props">
            <a :disabled="disabled" @click="handleCopy(props)">复制</a>
          </template>
        </JVxeTable>
        <BatchSetSizeModal ref="sizeModalRef" @ok="batchSetSizeModalFormOk" />
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script lang="ts" setup>
  /**
   * 订单录入表单（主表 + 订单商品 JVxeTable）
   * 老：modules/JshOrderForm.vue
   * 计算公式全部在 ./orderCalc.ts（逐行对照老代码，见各函数注释）
   */
  import { computed, nextTick, reactive, ref } from 'vue';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import type { JVxeTableInstance } from '/@/components/jeecg/JVxeTable/types';
  import JSearchSelect from '/@/components/Form/src/jeecg/components/JSearchSelect.vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import BatchSetSizeModal from './BatchSetSizeModal.vue';
  import { createOrderProductVxeColumns, customerOptions, loadCustomerOptions } from './order.data';
  import { orderProductByMainId, orderSaveOrUpdate, productList, productQueryById } from './order.api';
  import {
    ProductInfo,
    buildOrderPayload,
    calculateRowPrice,
    calculateSum,
    extendTypeChangeValues,
    orderToDisplay,
    productRowToDisplay,
  } from './orderCalc';

  const props = defineProps({
    // 表单禁用
    disabled: { type: Boolean, default: false },
  });
  const emit = defineEmits(['ok', 'close']);
  const { createMessage } = useMessage();

  const labelCol = { xs: { span: 24 }, sm: { span: 6 } };
  const wrapperCol = { xs: { span: 24 }, sm: { span: 16 } };
  const validatorRules = {
    customerId: [{ required: true, message: '请选择客户!' }],
    address: [{ required: true, message: '请输入地址!' }],
  };

  const formRef = ref();
  const tableRef = ref<JVxeTableInstance>();
  const sizeModalRef = ref();
  const confirmLoading = ref(false);
  const activeKey = ref('jshOrderProduct');
  const model = reactive<Recordable>({});
  const columns = ref(createOrderProductVxeColumns());
  const dataSource = ref<Recordable[]>([]);
  const tableLoading = ref(false);
  /** 商品 id -> 商品（含 wholesalePrice 与铝材/玻璃尺寸差），老 jshProductPricesMap */
  const productMap: Record<string, ProductInfo> = {};

  const formDisabled = computed(() => props.disabled);

  loadCustomerOptions();
  initProductsMap();

  /** 老：initProductsMap()（JshOrderForm.vue:369-379），同时作为“产品”列下拉选项 */
  function initProductsMap() {
    productList({ pageSize: 5000 })
      .then((res) => {
        const records = (res && res.records) || [];
        records.forEach((item) => {
          productMap[item.id] = item;
        });
        const col = columns.value.find((c) => c.key === 'productId');
        if (col) {
          col.options = records.map((item) => ({ value: `${item.id}`, text: item.name, title: item.name }));
          columns.value = [...columns.value];
        }
      })
      .catch((e) => createMessage.error(e?.message || '商品加载失败'));
  }

  function getXTable() {
    return tableRef.value!.getXTable();
  }

  /** 表格当前所有行（真实行对象，含新增行） */
  function getAllRows(): Recordable[] {
    return getXTable().getTableData().fullData;
  }

  /**
   * 单行价格计算并回写
   * 老：calculateCurrentRowPrice(row)（JshOrderForm.vue:391-438）
   */
  function calculateCurrentRowPrice(row: Recordable) {
    const res = calculateRowPrice(row, productMap[row.productId]);
    if ('fixExtendNum' in res) {
      // 抽的数量重置为 3 的倍数后再算一次（老代码靠 setValues 触发的 valueChange 重算）
      tableRef.value!.setValues([{ rowKey: row.id, values: { extendNum: res.fixExtendNum } }]);
      row.extendNum = res.fixExtendNum;
      calculateCurrentRowPrice(row);
      return;
    }
    tableRef.value!.setValues([{ rowKey: row.id, values: res.values }]);
  }

  /**
   * 汇总写回主表
   * 老：calculateSumPrice()（JshOrderForm.vue:440-454）
   */
  function calculateSumPrice(rows?: Recordable[]) {
    const sum = calculateSum(rows ?? getAllRows());
    model.totalArea = sum.totalArea;
    model.totalPrice = sum.totalPrice;
  }

  /**
   * 老：updatePrices({ row })（JshOrderForm.vue:456-467）
   * 无 row 时重算所有行（老代码只遍历初始 dataSource，新增行不会被重算；这里遍历表格全部行）
   */
  function updatePrices(row?: Recordable) {
    if (row) {
      calculateCurrentRowPrice(row);
    } else {
      getAllRows().forEach((item) => calculateCurrentRowPrice(item));
    }
    calculateSumPrice();
  }

  /** 当选项被改变时，联动其他组件 —— 老：handleValueChange（JshOrderForm.vue:470-553） */
  async function handleValueChange(event) {
    // setValues 引发的 valueChange 由调用方自己处理后续计算，这里忽略，避免递归
    if (event.isSetValues) return;
    const { col, row, value, oldValue } = event;
    if (!col || !row) return;
    if (value === oldValue) return;
    switch (col.key) {
      case 'productId': {
        // 查询商品详情
        if (!value) return;
        if (!productMap[value]) {
          try {
            productMap[value] = await productQueryById({ id: value });
          } catch (e: any) {
            createMessage.error(e?.message || '商品查询失败');
            return;
          }
        }
        updatePrices(row);
        break;
      }
      case 'height':
      case 'width':
      case 'num': {
        updatePrices(row);
        break;
      }
      case 'extendType': {
        const values = extendTypeChangeValues(value, row);
        if (values) {
          Object.assign(row, values);
          tableRef.value!.setValues([{ rowKey: row.id, values }]);
        }
        updatePrices(row);
        break;
      }
      case 'extendNum': {
        updatePrices(row);
        break;
      }
      default: {
      }
    }
  }

  /** 老：@remove="calculateSumPrice"；删除事件先于真正删除触发，这里排除被删行再汇总 */
  function handleRemoved(event) {
    const deleteRows: Recordable[] = event?.deleteRows || [];
    calculateSumPrice(getAllRows().filter((r) => !deleteRows.includes(r)));
  }

  /** 老：handleCopy（JshOrderForm.vue:385-389） */
  async function handleCopy({ row }) {
    if (props.disabled) return;
    const copy = { ...row };
    delete copy.id;
    await tableRef.value!.addRows([copy], { setActive: false });
    calculateSumPrice();
  }

  /** 老：batchSetSize（JshOrderForm.vue:555-564） */
  function batchSetSize(type: string) {
    const rows = tableRef.value!.getSelectedData();
    if (rows.length <= 0) {
      createMessage.error('请选中需要批量设置的行');
      return;
    }
    sizeModalRef.value.add(type);
  }

  /** 老：batchSetSizeModalFormOk（JshOrderForm.vue:566-576）：值 = size - 0，然后重算价格 */
  function batchSetSizeModalFormOk(size, batchType: string) {
    const rowKeys = tableRef.value!.getSelectedData().map((r) => r.id);
    tableRef.value!.setValues(
      rowKeys.map((rowKey) => ({
        rowKey,
        values: { [batchType]: (size as any) - 0 },
      }))
    );
    updatePrices();
  }

  function resetModel(record: Recordable) {
    Object.keys(model).forEach((k) => delete model[k]);
    Object.assign(model, record);
  }

  /** 新增：老 add() -> edit(modelDefault)，不默认添加空行 */
  function add() {
    edit({});
  }

  /** 编辑：老 edit(record)（JshOrderForm.vue:593-626） */
  function edit(record: Recordable) {
    formRef.value?.clearValidate?.();
    activeKey.value = 'jshOrderProduct';
    resetModel({ ...record });
    dataSource.value = [];
    if (model.customerId !== null && model.customerId !== undefined && model.customerId !== '') {
      model.customerId = `${model.customerId}`;
    }
    if (model.id) {
      orderToDisplay(model);
      tableLoading.value = true;
      orderProductByMainId({ id: model.id })
        .then((result) => {
          let list: Recordable[] = [];
          if (result) {
            if (Array.isArray(result)) {
              list = result;
            } else if (Array.isArray(result.records)) {
              list = result.records;
            }
          }
          dataSource.value = list.map((item) => {
            productRowToDisplay(item);
            // 下拉选项 value 为字符串
            if (item.productId !== null && item.productId !== undefined) item.productId = `${item.productId}`;
            return item;
          });
        })
        .finally(() => {
          tableLoading.value = false;
        });
    }
  }

  /** ATab 选项卡切换事件 */
  function handleChangeTabs() {
    nextTick(() => tableRef.value?.resetScrollTop());
  }

  /** 确定按钮 —— 老：handleOk + classifyIntoFormData + request（JshOrderForm.vue:647-769） */
  async function handleOk() {
    // 重新计算一次价格
    updatePrices();
    try {
      await formRef.value.validate();
    } catch (e) {
      return;
    }
    const errMap = await tableRef.value!.validateTable();
    if (errMap) {
      activeKey.value = 'jshOrderProduct';
      return;
    }
    let formData: Recordable;
    try {
      const tableData = tableRef.value!.getTableData();
      formData = buildOrderPayload({ ...model }, tableData, productMap);
    } catch (e) {
      console.error(e);
      createMessage.error('信息有误');
      return;
    }
    confirmLoading.value = true;
    try {
      // 新增 POST /add，编辑 PUT /edit；成功提示由 defHttp 弹出后端 message
      await orderSaveOrUpdate(formData, !!model.id);
      emit('ok');
    } finally {
      confirmLoading.value = false;
    }
  }

  defineExpose({ add, edit, handleOk });
</script>

<style lang="less" scoped>
  // 可编辑单元格未激活时画一个输入位底块，空值显示占位字，避免看起来像只读
  .is-editable :deep(.vxe-body--column.col--edit:not(.col--active)) {
    position: relative;
    cursor: text;

    &::before {
      content: '';
      position: absolute;
      inset: 6px 4px;
      pointer-events: none;
      background: #f3f7fd;
      border-radius: 4px;
      box-shadow: inset 0 0 0 1px #e1e9f5;
      transition: box-shadow 0.15s, background 0.15s;
    }

    &:hover::before {
      background: #fff;
      box-shadow: inset 0 0 0 1px @primary-color;
    }

    .vxe-cell {
      position: relative;
      z-index: 1;
    }

    .j-vxe-reload-effect-span:empty::after {
      content: '点击填写';
      color: #b4bcc8;
    }
  }
</style>
