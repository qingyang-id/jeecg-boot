<template>
  <a-card :bordered="false">
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('jsh_order_product_detail')">导出</a-button>
    </div>
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
        :sorter="isorter"
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
      default: 0,
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
        this.queryParam['orderId'] = val || '';
        this.queryParam['type'] = this.type || '';
        this.loadData(1);
      }
    }
  },
  data() {
    return {
      description: 'jsh_order_product管理页面',
      // disableMixinCreated: true,
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
          title: '原尺寸ID',
          align: "center",
          dataIndex: 'orderProductId'
        },
        {
          title: '订单号',
          align: "center",
          dataIndex: 'orderId_dictText'
        },
        {
          title: '产品',
          align: "center",
          dataIndex: 'productId_dictText'
        },
        {
          title: '参考宽度(mm)',
          align: "center",
          dataIndex: 'referenceWidth'
        },
        {
          title: '参考高度(mm)',
          align: "center",
          dataIndex: 'referenceHeight'
        },
        {
          title: '宽(mm)',
          align: "center",
          dataIndex: 'width'
        },
        {
          title: '高(mm)',
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
        pageSizeOptions: ['5', '10', '30', '50'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条";
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      isorter: {
        column: 'id',
        order: 'desc',
      },
      url: {
        list: "/business/order/jshOrderProductDetail/list",
        exportXlsUrl: "/business/order/jshOrderProductDetail/exportXls",
      },
      dictOptions: {},
      superFieldList: [],
    };
  },
  created() {
  },
  computed: {},
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
