<template>
  <j-modal
      width="90%"
      :visible="visible"
      :maskClosable="false"
      :keyboard="false"
      :forceRender="true"
      @cancel="handleCancel"
      wrapClassName="ant-modal-cust-warp"
      class="print-modal"
      :dialog-style="{ top: '5%', maxWidth: '1200px'}">
    <template slot="footer">
      <!--            <a-button v-if="orderPrintFlag" @click="handlePrint">三联打印预览</a-button>-->
      <!--此处为解决缓存问题-->
      <a-button type="primary" class="print-btn" v-if="printType==='铝材生产单' || printType==='玻璃生产单'" v-print="'#orderProducePrint'">普通打印</a-button>
      <a-button type="primary" class="print-btn" v-if="printType==='订单信息'" v-print="'#orderDetailPrint'">普通打印</a-button>
      <a-button type="primary" class="print-btn" v-if="printType==='生产单'" v-print="'#producePrint'">普通打印</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <a-form :form="form" class="print-form">
      <!--铝材单/玻璃单打印-->
      <template v-if="printType==='铝材生产单' || printType==='玻璃生产单'">
        <section ref="print" id="orderProducePrint">
          <a-row class="form-row" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col :p="6" style="font-size: 24px; font-weight: 500">
              {{ title }}
            </a-col>
          </a-row>
          <a-row class="form-row" type="flex" justify="center" :gutter="24">
            <a-form-item handle>
              <a-input v-decorator="['id', {}]" hidden/>
            </a-form-item>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%"> {{ model.orderTime ? model.orderTime.split(" ")[0] : '' }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%"> {{ model.customerId_dictText }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%"> {{ model.address }} </span>
            </a-col>
          </a-row>
          <a-row type="flex" justify="center">
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="columns"
                :dataSource="dataSource">
            </a-table>
          </a-row>
        </section>
      </template>
      <!--订单打印-->
      <template v-else-if="printType==='订单信息'">
        <!--原尺寸-->
        <section ref="print" id="orderDetailPrint">
          <a-row type="flex" justify="center" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col :p="6" style="font-size: 24px; font-weight: bold">
              {{ title }}
            </a-col>
          </a-row>
          <a-row type="flex" justify="center" :gutter="24">
            <a-form-item handle>
              <a-input v-decorator="['id', {}]" hidden/>
            </a-form-item>
            <a-col :span="6" style="padding: 10px">
              <span>订单:</span>
              <span style="width: 80%"> {{ model.orderCode }} </span>
            </a-col>
            <a-col :span="6" style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%">
                {{ model.customerId_dictText }}
              </span>
            </a-col>
            <a-col :span="6" style="padding: 10px">
              <span>总面积(m²):</span>
              <span style="width: 80%"> {{ model.totalArea }} </span>
            </a-col>
            <a-col :span="6" style="padding: 10px">
              <span>订单总额(元):</span>
              <span style="width: 80%"> {{ model.totalPrice }} </span>
            </a-col>
          </a-row>
          <a-row type="flex" justify="center" :gutter="24">
            <a-col :span="6" style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%">
                {{ model.orderTime ? model.orderTime.split(" ")[0] : '' }}
              </span>
            </a-col>
            <a-col :span="8" style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%">
                {{ model.address }}
              </span>
            </a-col>
            <a-col :span="8" style="padding: 10px">
              <span>备注:</span>
              <span style="width: 80%"> {{ model.remark }} </span>
            </a-col>
          </a-row>
          <a-row type="flex" justify="center">
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="columns"
                :dataSource="dataSource">
            </a-table>
          </a-row>
        </section>
      </template>
      <!--铝材单/玻璃单-->
      <template v-if="printType==='生产单'">
        <section ref="print" id="producePrint">
          <a-row class="form-row" type="flex" justify="center">
            <a-col :p="6" style="font-size: 24px; font-weight: 500">
              铝材单
            </a-col>
          </a-row>
          <a-row class="form-row" type="flex" justify="center" :gutter="24">
            <a-form-item handle>
              <a-input v-decorator="['id', {}]" hidden/>
            </a-form-item>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%"> {{ model.orderTime ? model.orderTime.split(" ")[0] : '' }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%"> {{ model.customerId_dictText }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%"> {{ model.address }} </span>
            </a-col>
          </a-row>
          <a-row type="flex" justify="center">
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="aluminumColumns"
                :dataSource="aluminumDataSource">
            </a-table>
          </a-row>

          <a-row class="form-row" type="flex" justify="center" style="margin-top: 50px">
            <a-col :p="6" style="font-size: 24px; font-weight: 500">
              玻璃单
            </a-col>
          </a-row>
          <a-row class="form-row" type="flex" justify="center" :gutter="24">
            <a-form-item handle>
              <a-input v-decorator="['id', {}]" hidden/>
            </a-form-item>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>时间:</span>
              <span style="width: 80%"> {{ model.orderTime ? model.orderTime.split(" ")[0] : '' }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>客户:</span>
              <span style="width: 80%"> {{ model.customerId_dictText }} </span>
            </a-col>
            <a-col type="flex" justify="center" style="padding: 10px">
              <span>地址:</span>
              <span style="width: 80%"> {{ model.address }} </span>
            </a-col>
          </a-row>
          <a-row type="flex" justify="center">
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="glassColumns"
                :dataSource="glassDataSource">
            </a-table>
          </a-row>
        </section>
      </template>
    </a-form>
    <order-print-iframe ref="modalDetail"></order-print-iframe>
  </j-modal>
</template>

<script>
import pick from 'lodash.pick';
import { getAction } from '@/api/manage';
import OrderPrintIframe from './OrderPrintIframe';

export default {
  name: 'OrderDetail',
  components: {
    OrderPrintIframe,
  },
  data() {
    return {
      title: "详情",
      width: '1600px',
      visible: false,
      model: {},
      printType: '',
      orderPrintFlag: false,
      spanCol: {
        xs: { span: 5 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: '80%' },
        sm: { span: '80%' },
      },
      thStyle: { style: { 'font-size': '18px', 'font-weight': "bold", 'padding': "5px" } },
      tdStyle: { style: { 'font-size': '18px', 'font-weight': "bold", 'padding': "5px" } },
      form: this.$form.createForm(this),
      loading: false,
      dataSource: [],
      // 表头
      disableMixinCreated: true,
      // 表头
      columns: [],
      // 订单
      orderColumns: [
        {
          title: '#',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: (t, r, index) => parseInt(index) + 1
        },
        {
          title: '产品',
          align: "left",
          dataIndex: 'productId_dictText'
        },
        {
          title: '铝材颜色',
          align: "left",
          dataIndex: 'color'
        },
        {
          title: '玻璃色号',
          align: "left",
          dataIndex: 'glassColor'
        },
        {
          title: '规格(高*宽*数量)',
          align: "left",
          dataIndex: 'height',
          customRender: function (t, r) {
            return `${r.height} * ${r.width} * ${r.num}`;
          },
        },
        {
          title: '方向',
          align: "left",
          dataIndex: 'direction_dictText'
        },
        {
          title: '抽/拉/条',
          align: "left",
          dataIndex: 'extendType_dictText',
          customRender: function (t, r) {
            return `${r.extendType_dictText} * ${r.extendNum}`;
          },
        },
        {
          title: '附加(元)',
          align: "left",
          dataIndex: 'extendPrice',
          customRender: function (t) {
            return t / 100;
          }
        },
        {
          title: '总面积(m²)',
          align: "left",
          dataIndex: 'totalArea',
          customRender: function (t) {
            return t / 1000000;
          }
        },
        {
          title: '总价(元)',
          align: "left",
          dataIndex: 'totalPrice',
          customRender: function (t) {
            return t / 100;
          }
        },
      ],
      // 铝材生产单
      aluminumColumns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          align: "left",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '产品',
          align: "left",
          dataIndex: 'productId_dictText',
          className: "print-table-row",
        },
        {
          title: '颜色',
          align: "left",
          dataIndex: 'color',
          className: "print-table-row",
        },
        {
          title: '原尺寸(高*宽)',
          align: "left",
          dataIndex: 'referenceHeight',
          customRender: function (t, r) {
            return `${r.referenceHeight} * ${r.referenceWidth}`;
          },
        },
        {
          title: '规格(高*宽*数量)',
          align: "left",
          dataIndex: 'height',
          customRender: function (t, r) {
            return `${r.height} * ${r.width} * ${r.num}`;
          },
        },
        {
          title: '方向',
          align: "left",
          dataIndex: 'direction_dictText',
        },
        {
          title: '抽/拉/条',
          align: "left",
          dataIndex: 'extendType_dictText',
          customRender: function (t, r) {
            return `${r.extendType_dictText} * ${r.extendNum}`;
          },
        },
      ],
      // 玻璃生产单
      glassColumns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          align: "left",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '产品',
          align: "left",
          dataIndex: 'productId_dictText'
        },
        {
          title: '色号',
          align: "left",
          dataIndex: 'color'
        },
        {
          title: '规格(高*宽*数量)',
          align: "left",
          dataIndex: 'height',
          customRender: function (t, r) {
            return `${r.height} * ${r.width} * ${r.num}`;
          },
        },
      ],
      // 铝材数据源
      aluminumDataSource: [],
      // 玻璃数据源
      glassDataSource: [],
      /* 分页参数 */
      ipagination: {
        current: 1,
        pageSize: 50,
        pageSizeOptions: ['5', '10', '50'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条";
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      url: {
        orderProductList: '/business/order/jshOrderProduct/list',
        orderProductDetailList: "/business/order/jshOrderProductDetail/list",
      },
      dictOptions: {},
      superFieldList: [],
    };
  },
  created() {
    this.orderColumns.forEach(item => Object.assign(item, {
      customHeaderCell: () => this.thStyle,
      customCell: () => this.tdStyle
    }));
    this.aluminumColumns.forEach(item => Object.assign(item, {
      customHeaderCell: () => this.thStyle,
      customCell: () => this.tdStyle
    }));
    this.glassColumns.forEach(item => Object.assign(item, {
      customHeaderCell: () => this.thStyle,
      customCell: () => this.tdStyle
    }));
  },
  methods: {
    show(record, printType) {
      this.dataSource = [];
      this.ipagination.current = 1;
      this.printType = printType;
      this.title = printType;
      this.visible = true;
      this.model = Object.assign({}, record, {
        totalArea: record.totalArea / 1000000,
        totalPrice: record.totalPrice / 100,
      });
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'id'));
      });
      let url = '';
      let params = {};
      if (printType === '订单信息') {
        url = this.url.orderProductList;
        params = { orderId: this.model.id };
        this.columns = this.orderColumns;
      } else if (printType === '铝材生产单') {
        url = this.url.orderProductDetailList;
        params = { orderId: record.id, type: 1 };
        this.columns = this.aluminumColumns;
      } else if (printType === '玻璃生产单') {
        url = this.url.orderProductDetailList;
        params = { orderId: record.id, type: 2 };
        this.columns = this.glassColumns;
      } else if (printType === '生产单') {
        this.requestProduceData(record.id);
        return;
      } else {
        this.$message.error('参数错误');
        this.close();
        return;
      }
      this.requestSubTableData(url, params);
    },
    requestProduceData(orderId) {
      this.loading = true;
      Promise.all([
        getAction(this.url.orderProductDetailList, {
          orderId,
          type: 1
        })
            .then(res => {
              if (res && res.code === 200) {
                this.aluminumDataSource = res.result.records;
              }
            }),
        getAction(this.url.orderProductDetailList, {
          orderId,
          type: 2
        })
            .then(res => {
              if (res && res.code === 200) {
                this.glassDataSource = res.result.records;
              }
            }),
      ])
          .finally(() => {
            this.loading = false;
          });
    },
    requestSubTableData(url, params) {
      this.loading = true;
      getAction(url, params).then(res => {
        if (res && res.code === 200) {
          this.dataSource = res.result.records;
        }
      }).finally(() => {
        this.loading = false;
      });
    },
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit('close');
      this.visible = false;
    },
    myHandleDetail(record, type) {
      this.show(record, type);
    },
    //三联打印预览
    handlePrint() {
      // getPlatformConfigByKey({ "platformKey": "order_print_url" }).then((res) => {
      //   if (res && res.code === 200) {
      //     let orderPrintUrl = res.data.platformValue + '?no=' + this.model.number;
      //     let orderPrintHeight = this.dataSource.length * 50 + 600;
      //     this.$refs.modalDetail.show(this.model, orderPrintUrl, orderPrintHeight);
      //     this.$refs.modalDetail.title = this.detailType + "-三联打印预览";
      //   }
      // });
    }
  }
};
</script>

<style scoped>
/*update_begin author:scott date:20191203 for:打印机打印的字体模糊问题 */
* {
  color: #000000 !important;
  -webkit-tap-highlight-color: #000000 !important;
}
/*update_end author:scott date:20191203 for:打印机打印的字体模糊问题 */

.print-form {
  font-size: 18px;
  font-weight: bold;
}
.print-btn {
  color: white !important;
  -webkit-tap-highlight-color: white !important;
}
</style>
