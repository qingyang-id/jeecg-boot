<template>
  <a-card :bordered="false">
    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">
      </a-table>
    </div>
  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin';

export default {
  name: "JshOrderProductDetailList",
  mixins: [JeecgListMixin],
  components: {},
  props: {
    orderId: {
      type: Number,
      default: '',
      required: false
    },
    type: {
      type: Number,
      default: 0,
      required: false
    }
  },
  watch: {
    orderId: {
      immediate: true,
      handler(val) {
        if (!this.orderId) {
          // this.clearList();
          this.loadData(1);
        } else {
          this.queryParam['orderId'] = val;
          this.queryParam['type'] = this.type;
          console.log(this.queryParam);
          this.loadData(1);
        }
      }
    }
  },
  data() {
    return {
      description: 'jsh_order_product管理页面',
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
          title: '订单ID',
          align: "center",
          dataIndex: 'orderId'
        },
        {
          title: '订单产品表ID',
          align: "center",
          dataIndex: 'orderProductId'
        },
        {
          title: '产品',
          align: "center",
          dataIndex: 'productId_dictText'
        },
        {
          title: '参考宽度',
          align: "center",
          dataIndex: 'referenceWidth'
        },
        {
          title: '参考高度',
          align: "center",
          dataIndex: 'referenceHeight'
        },
        {
          title: '宽',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '高',
          align: "center",
          dataIndex: 'height'
        },
        {
          title: '数量',
          align: "center",
          dataIndex: 'num'
        }
      ],
      url: {
        list: "/business/order/jshOrderProduct/list",
        delete: "/business/order/jshOrderProduct/deleteJshOrderProductDetail",
        deleteBatch: "/business/order/jshOrderProduct/deleteBatchJshOrderProductDetail",
        exportXlsUrl: "/business/order/jshOrderProduct/exportJshOrderProductDetail",
        importUrl: "/business/order/jshOrderProduct/importJshOrderProductDetail",
      },
      dictOptions: {},
      superFieldList: [],
    };
  },
  created() {
  },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`;
    }
  },
  methods: {
    clearList() {
      this.dataSource = [];
      this.selectedRowKeys = [];
      this.ipagination.current = 1;
    },
  }
};
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
