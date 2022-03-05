<template>
  <j-modal
      :title="title"
      width="90%"
      :visible="visible"
      :maskClosable="false"
      :keyboard="false"
      :forceRender="true"
      @cancel="handleCancel"
      wrapClassName="ant-modal-cust-warp"
      style="top:5%;height: 100%;overflow-y: hidden">
    <template slot="footer">
      <!--            <a-button v-if="orderPrintFlag" @click="handlePrint">三联打印预览</a-button>-->
      <!--此处为解决缓存问题-->
      <a-button v-if="printType==='铝材生产单' || printType==='玻璃生产单'" v-print="'#orderProducePrint'">普通打印</a-button>
      <a-button v-if="printType==='订单信息'" v-print="'#orderDetailPrint'">普通打印</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <a-form :form="form">
      <!--铝材单/玻璃单-->
      <template v-if="printType==='铝材生产单' || printType==='玻璃生产单'">
        <section ref="print" id="orderProducePrint">
          <a-row class="form-row" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col :p="6" style="font-size: 24px;font-weight: 800">
              {{ title }}
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单编号">
                <a-input v-decorator="['id', {}]" hidden/>
                {{ model.orderCode }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单时间">
                {{ model.orderTime }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                {{ model.customerId_dictText }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="地址">
                {{ model.address }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
                {{ model.remark }}
              </a-form-item>
            </a-col>
          </a-row>
<!--          <div :style="tableWidth">-->
          <div>
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="columns"
                :dataSource="dataSource">
            </a-table>
          </div>
        </section>
      </template>
      <template v-else-if="printType==='订单信息'">
        <!--原尺寸-->
        <section ref="print" id="orderDetailPrint">
          <a-row class="form-row" :gutter="24" style="text-align: center; margin-bottom: 10px">
            <a-col :p="6" style="font-size: 24px;font-weight: 800">
              {{ title }}
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单编号">
                <a-input v-decorator="['id', {}]" hidden/>
                {{ model.orderCode }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总面积(m²)">
                {{ model.totalArea }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单总额(元)">
                {{ model.totalPrice }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                {{ model.customerId_dictText }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="地址">
                {{ model.address }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下单时间">
                {{ model.orderTime }}
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
                {{ model.remark }}
              </a-form-item>
            </a-col>
          </a-row>
<!--          <div :style="tableWidth">-->
          <div>
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="columns"
                :dataSource="dataSource">
            </a-table>
          </div>
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
      tableWidth: {
        'width': '1550px'
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
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
          align: "center",
          dataIndex: 'productId_dictText'
        },
        {
          title: '铝材颜色',
          align: "center",
          dataIndex: 'color'
        },
        {
          title: '玻璃色号',
          align: "center",
          dataIndex: 'glassColor'
        },
        {
          title: '高(mm)',
          align: "center",
          dataIndex: 'height'
        },
        {
          title: '宽(mm)',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '数量',
          align: "center",
          dataIndex: 'num'
        },
        {
          title: '方向',
          align: "center",
          dataIndex: 'direction_dictText'
        },
        {
          title: '抽/拉/条',
          align: "center",
          dataIndex: 'extendType_dictText'
        },
        {
          title: '抽/拉/条数量',
          align: "center",
          dataIndex: 'extendNum'
        },
        {
          title: '单价(元)',
          align: "center",
          dataIndex: 'price',
          customRender: function (t) {
            return t / 100;
          }
        },
        {
          title: '总面积(m²)',
          align: "center",
          dataIndex: 'totalArea',
          customRender: function (t) {
            return t / 1000000;
          }
        },
        {
          title: '总价(元)',
          align: "center",
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
          width: 60,
          align: "center",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '产品',
          align: "center",
          dataIndex: 'productId_dictText'
        },
        {
          title: '颜色',
          align: "center",
          dataIndex: 'color'
        },
        {
          title: '参考高度(mm)',
          align: "center",
          dataIndex: 'referenceHeight'
        },
        {
          title: '参考宽度(mm)',
          align: "center",
          dataIndex: 'referenceWidth'
        },
        {
          title: '高(mm)',
          align: "center",
          dataIndex: 'height'
        },
        {
          title: '宽(mm)',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '数量',
          align: "center",
          dataIndex: 'num'
        },
        {
          title: '方向',
          align: "center",
          dataIndex: 'direction_dictText'
        },
        {
          title: '抽/拉/条',
          align: "center",
          dataIndex: 'extendType_dictText'
        },
        {
          title: '抽/拉/条数量',
          align: "center",
          dataIndex: 'extendNum'
        }
      ],
      // 玻璃生产单
      glassColumns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: "center",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '产品',
          align: "center",
          dataIndex: 'productId_dictText'
        },
        {
          title: '色号',
          align: "center",
          dataIndex: 'color'
        },
        {
          title: '高(mm)',
          align: "center",
          dataIndex: 'height'
        },
        {
          title: '宽(mm)',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '数量',
          align: "center",
          dataIndex: 'num'
        }
      ],
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
      } else {
        this.$message.error('参数错误');
        this.close();
        return;
      }
      this.requestSubTableData(url, params);
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

</style>
