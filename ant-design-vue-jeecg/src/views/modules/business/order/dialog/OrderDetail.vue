<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    :forceRender="true"
    @cancel="handleCancel"
    wrapClassName="ant-modal-cust-warp"
    style="top:5%;height: 100%;overflow-y: hidden">
    <template slot="footer">
      <!--      <a-button v-if="orderPrintFlag" @click="handlePrint">三联打印预览</a-button>-->
      <!--此处为解决缓存问题-->
      <a-button v-print="'#print'">普通打印</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <a-form :form="form">
      <!--销售订单-->
      <section ref="print" id="saleOrderPrint">
        <a-row class="form-row" :gutter="24">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单编号">
              {{ model.orderCode }}
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单时间">
              {{ model.orderTime }}
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
              {{ model.customerId_dictText }}
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="地址">
              {{ model.address }}
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              {{ model.remark }}
            </a-form-item>
          </a-col>
        </a-row>
        <div :style="tableWidth">
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
        <a-row class="form-row" :gutter="24">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label=""
                         style="padding:20px 10px;">
              {{ model.remark }}
            </a-form-item>
          </a-col>
        </a-row>
      </section>
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
      detailType: '',
      orderPrintFlag: false,
      tableWidth: {
        'width': '1550px'
      },
      tableWidthRetail: {
        'width': '1150px'
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
      columns: [
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
          title: '参考宽度(cm)',
          align: "center",
          dataIndex: 'referenceWidth'
        },
        {
          title: '参考高度(cm)',
          align: "center",
          dataIndex: 'referenceHeight'
        },
        {
          title: '宽(cm)',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '高(cm)',
          align: "center",
          dataIndex: 'height'
        },
        {
          title: '颜色',
          align: "center",
          dataIndex: 'color'
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
        pageSize: 10,
        pageSizeOptions: ['5', '10', '50'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条";
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      url: {
        list: "/business/order/jshOrderProductDetail/list",
      },
      dictOptions: {},
      superFieldList: [],
    };
  },
  created() {
  },
  methods: {
    show(record, type) {
      this.dataSource = [];
      this.ipagination.current = 1;
      this.detailType = type;
      this.visible = true;
      this.model = Object.assign({}, record);
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'id'));
      });
      this.requestSubTableData(this.url.list, { orderId: record.id, type: 1 });
    },
    requestSubTableData(url, params) {
      this.loading = true;
      getAction(url, params).then(res => {
        if (res && res.code === 200) {
          this.dataSource = res.result.records;
          console.log('\n\n\n records', res.result.records);
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