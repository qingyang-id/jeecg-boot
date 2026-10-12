<template>
  <a-modal
    width="90%"
    :open="visible"
    :maskClosable="false"
    :keyboard="false"
    :forceRender="true"
    wrapClassName="ant-modal-cust-warp"
    class="print-modal"
    :dialogStyle="{ top: '5%', maxWidth: '1200px' }"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button type="primary" class="print-btn" :disabled="printDisabled" v-if="printType === '铝材生产单' || printType === '玻璃生产单'" @click="doPrint('#orderProducePrint')">
        普通打印
      </a-button>
      <a-button type="primary" class="print-btn" :disabled="printDisabled" v-if="printType === '订单信息'" @click="doPrint('#orderDetailPrint')">普通打印</a-button>
      <a-button type="primary" class="print-btn" :disabled="printDisabled" v-if="printType === '生产单'" @click="doPrint('#producePrint')">普通打印</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <div class="print-form">
      <!--铝材单/玻璃单打印-->
      <template v-if="printType === '铝材生产单' || printType === '玻璃生产单'">
        <section id="orderProducePrint">
          <a-row class="form-row" justify="center" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col style="font-size: 24px; font-weight: 500">
              {{ title }}
            </a-col>
          </a-row>
          <a-row class="form-row" justify="center" :gutter="24">
            <a-col style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%"> {{ orderDate }} </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%"> {{ model.customerId_dictText }} </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%"> {{ model.address }} </span>
            </a-col>
          </a-row>
          <a-row justify="center">
            <a-table size="middle" bordered rowKey="id" :pagination="false" :columns="columns" :dataSource="dataSource" />
          </a-row>
        </section>
      </template>
      <!--订单打印-->
      <template v-else-if="printType === '订单信息'">
        <!--原尺寸-->
        <section id="orderDetailPrint">
          <a-row justify="center" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col style="font-size: 24px; font-weight: bold">
              {{ title }}
            </a-col>
          </a-row>
          <a-row justify="center" :gutter="24">
            <a-col style="padding: 10px">
              <span>订单:</span>
              <span style="width: 80%"> {{ model.orderCode }} </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%">
                {{ model.customerId_dictText }}
              </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>总面积(m²):</span>
              <span style="width: 80%"> {{ model.totalArea }} </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>订单总额(元):</span>
              <span style="width: 80%"> {{ model.totalPrice }} </span>
            </a-col>
          </a-row>
          <a-row justify="center" :gutter="24">
            <a-col style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%">
                {{ orderDate }}
              </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%">
                {{ model.address }}
              </span>
            </a-col>
            <a-col style="padding: 10px">
              <span>备注:</span>
              <span style="width: 80%"> {{ model.remark }} </span>
            </a-col>
          </a-row>
          <a-row justify="center">
            <a-table size="middle" bordered rowKey="id" :pagination="false" :columns="columns" :dataSource="dataSource" />
          </a-row>
        </section>
      </template>
      <!--生产单：铝材单 + 玻璃单-->
      <template v-if="printType === '生产单'">
        <section id="producePrint" style="min-height: 800px; display: flex; flex-direction: column">
          <section style="flex: 1">
            <a-row class="form-row" justify="center">
              <a-col style="font-size: 24px; font-weight: 500"> 铝材单 </a-col>
            </a-row>
            <a-row class="form-row" justify="center" :gutter="24">
              <a-col style="padding: 10px">
                <span>时间:</span>
                <span style="width: 80%"> {{ orderDate }} </span>
              </a-col>
              <a-col style="padding: 10px">
                <span>客户:</span>
                <span style="width: 80%"> {{ model.customerId_dictText }} </span>
              </a-col>
              <a-col style="padding: 10px">
                <span>地址:</span>
                <span style="width: 80%"> {{ model.address }} </span>
              </a-col>
            </a-row>
            <a-row justify="center">
              <a-table size="middle" bordered rowKey="id" :pagination="false" :columns="aluminumColumns" :dataSource="aluminumDataSource" />
            </a-row>
          </section>
          <section>
            <a-row class="form-row" justify="center" style="margin-top: 30px">
              <a-col style="font-size: 24px; font-weight: 500"> 玻璃单 </a-col>
            </a-row>
            <a-row class="form-row" justify="center" :gutter="24">
              <a-col style="padding: 10px">
                <span>时间:</span>
                <span style="width: 80%"> {{ orderDate }} </span>
              </a-col>
              <a-col style="padding: 10px">
                <span>客户:</span>
                <span style="width: 80%"> {{ model.customerId_dictText }} </span>
              </a-col>
              <a-col style="padding: 10px">
                <span>地址:</span>
                <span style="width: 80%"> {{ model.address }} </span>
              </a-col>
            </a-row>
            <a-row justify="center">
              <a-table size="middle" bordered rowKey="id" :pagination="false" :columns="glassColumns" :dataSource="glassDataSource" />
            </a-row>
          </section>
        </section>
      </template>
    </div>
    <OrderPrint ref="printIframeRef" />
  </a-modal>
</template>

<script lang="ts" setup>
  /**
   * 订单详情 / 打印（铝材生产单、玻璃生产单、订单信息、生产单）
   * 老：dialog/OrderDetail.vue（v-print=vue-print-nb-jeecg），这里复用上游 usePrintJS 的 printNb
   * （同一个 vue-print-nb-jeecg/src/printarea 实现），HTML 结构 / 样式照搬老页面
   */
  import { computed, ref } from 'vue';
  import { printNb } from '/@/hooks/web/usePrintJS';
  import { useMessage } from '/@/hooks/web/useMessage';
  import OrderPrint from './OrderPrint.vue';
  import { orderProductDetailList, orderProductList } from './order.api';

  const emit = defineEmits(['close']);
  const { createMessage } = useMessage();

  const visible = ref(false);
  const title = ref('详情');
  const printType = ref('');
  const model = ref<Recordable>({});
  const loading = ref(false);
  const loadFailed = ref(false);
  const printDisabled = computed(() => loading.value || loadFailed.value);
  const dataSource = ref<Recordable[]>([]);
  const aluminumDataSource = ref<Recordable[]>([]);
  const glassDataSource = ref<Recordable[]>([]);
  const columns = ref<any[]>([]);
  const printIframeRef = ref();

  const orderDate = computed(() => (model.value.orderTime ? String(model.value.orderTime).split(' ')[0] : ''));

  // 打印表格单元格样式（老 thStyle / tdStyle）
  const cellStyle = { style: { 'font-size': '18px', 'font-weight': 'bold', padding: '5px' } };
  const withStyle = (cols: any[]) =>
    cols.map((item) => Object.assign(item, { customHeaderCell: () => cellStyle, customCell: () => cellStyle }));

  const indexRender = ({ index }) => parseInt(index) + 1;
  const specRender = ({ record }) => `${record.height} * ${record.width} * ${record.num}`;
  const directionRender = ({ text, record }) => (!record.direction ? '' : text);
  const extendRender = ({ record }) => (!record.extendNum ? '' : `${record.extendType_dictText} * ${record.extendNum}`);

  // 订单
  const orderColumns = withStyle([
    { title: '#', key: 'rowIndex', width: 60, align: 'center', customRender: indexRender },
    { title: '产品', align: 'left', dataIndex: 'productId_dictText' },
    { title: '铝材颜色', align: 'left', dataIndex: 'color' },
    { title: '玻璃色号', align: 'left', dataIndex: 'glassColor' },
    { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
    { title: '方向', align: 'left', dataIndex: 'direction_dictText', customRender: directionRender },
    { title: '抽/拉/条', align: 'left', dataIndex: 'extendType_dictText', customRender: extendRender },
    { title: '附加(元)', align: 'left', dataIndex: 'extendPrice', customRender: ({ text }) => text / 100 },
    { title: '总面积(m²)', align: 'left', dataIndex: 'totalArea', customRender: ({ text }) => text / 1000000 },
    { title: '总价(元)', align: 'left', dataIndex: 'totalPrice', customRender: ({ text }) => text / 100 },
  ]);
  // 铝材生产单
  const aluminumColumns = withStyle([
    { title: '#', dataIndex: '', key: 'rowIndex', align: 'left', customRender: indexRender },
    { title: '产品', align: 'left', dataIndex: 'productId_dictText', className: 'print-table-row' },
    { title: '颜色', align: 'left', dataIndex: 'color', className: 'print-table-row' },
    {
      title: '原尺寸(高*宽)',
      align: 'left',
      dataIndex: 'referenceHeight',
      customRender: ({ record }) => `${record.referenceHeight} * ${record.referenceWidth}`,
    },
    { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
    { title: '方向', align: 'left', dataIndex: 'direction_dictText', customRender: directionRender },
    { title: '抽/拉/条', align: 'left', dataIndex: 'extendType_dictText', customRender: extendRender },
  ]);
  // 玻璃生产单
  const glassColumns = withStyle([
    { title: '#', dataIndex: '', key: 'rowIndex', align: 'left', customRender: indexRender },
    { title: '产品', align: 'left', dataIndex: 'productId_dictText' },
    { title: '色号', align: 'left', dataIndex: 'color' },
    { title: '规格(高*宽*数量)', align: 'left', dataIndex: 'height', customRender: specRender },
    { title: '抽/拉/条', align: 'left', dataIndex: 'extendType_dictText', customRender: extendRender },
  ]);

  /** 老：show(record, printType)（OrderDetail.vue:448-485） */
  function show(record: Recordable, type: string) {
    dataSource.value = [];
    loadFailed.value = false;
    printType.value = type;
    title.value = type;
    visible.value = true;
    model.value = Object.assign({}, record, {
      totalArea: record.totalArea / 1000000,
      totalPrice: record.totalPrice / 100,
    });
    let api: (p: any) => Promise<any>;
    let params: Recordable = { pageNo: 1, pageSize: 100 };
    if (type === '订单信息') {
      api = orderProductList;
      params = { ...params, orderId: record.id };
      columns.value = orderColumns;
    } else if (type === '铝材生产单') {
      api = orderProductDetailList;
      params = { ...params, orderId: record.id, type: 1 };
      columns.value = aluminumColumns;
    } else if (type === '玻璃生产单') {
      api = orderProductDetailList;
      params = { ...params, orderId: record.id, type: 2 };
      columns.value = glassColumns;
    } else if (type === '生产单') {
      requestProduceData(record.id, params);
      return;
    } else {
      createMessage.error('参数错误');
      close();
      return;
    }
    requestSubTableData(api, params);
  }

  /** 老：requestProduceData（OrderDetail.vue:486-513） */
  function requestProduceData(orderId, params) {
    loading.value = true;
    aluminumDataSource.value = [];
    glassDataSource.value = [];
    Promise.all([
      orderProductDetailList({ orderId, type: 1, ...params }).then((res) => {
        aluminumDataSource.value = (res && res.records) || [];
      }),
      orderProductDetailList({ orderId, type: 2, ...params }).then((res) => {
        glassDataSource.value = (res && res.records) || [];
      }),
    ])
      .catch(() => {
        aluminumDataSource.value = [];
        glassDataSource.value = [];
        loadFailed.value = true;
        createMessage.error('加载打印数据失败');
      })
      .finally(() => {
        loading.value = false;
      });
  }

  /** 老：requestSubTableData（OrderDetail.vue:514-523） */
  function requestSubTableData(api, params) {
    loading.value = true;
    api(params)
      .then((res) => {
        dataSource.value = (res && res.records) || [];
      })
      .catch(() => {
        dataSource.value = [];
        loadFailed.value = true;
        createMessage.error('加载打印数据失败');
      })
      .finally(() => {
        loading.value = false;
      });
  }

  function doPrint(domId: string) {
    printNb(domId);
  }

  function handleCancel() {
    close();
  }

  function close() {
    emit('close');
    visible.value = false;
  }

  /** 老：myHandleDetail(record, type) */
  function myHandleDetail(record: Recordable, type: string) {
    show(record, type);
  }

  /** 三联打印预览（老代码已注释，保留入口） */
  function handlePrint(url: string, height: number | string) {
    printIframeRef.value?.show(model.value, url, height);
  }

  defineExpose({ show, myHandleDetail, handlePrint });
</script>

<style scoped>
  /*打印机打印的字体模糊问题（老页面 scott 20191203）*/
  * {
    color: #000000 !important;
    -webkit-tap-highlight-color: #000000 !important;
  }

  .print-form {
    font-size: 18px;
    font-weight: bold;
  }

  .print-form :deep(*) {
    color: #000000 !important;
  }

  /* 上游全局 design/ant/index.less 把 .ant-col 设为 width:100%，会让抬头信息每项独占一行；
     antdv4 的 Row/Col 又把字号重置为 14px。这里恢复老页面的同行居中、18px 粗体抬头 */
  .print-form :deep(.ant-row),
  .print-form :deep(.ant-col) {
    font-size: inherit;
  }

  .print-form :deep(.ant-col) {
    width: auto;
  }

  .print-btn {
    color: white !important;
    -webkit-tap-highlight-color: white !important;
  }
</style>
