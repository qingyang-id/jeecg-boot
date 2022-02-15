<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('jsh_order_product')">导出</a-button>
    </div>
    <!-- table区域 begin -->
    <div>
      <a-alert type="info" showIcon style="margin-bottom: 16px;">
        <template slot="message">
          <span>已选择</span>
          <a style="font-weight: 600;padding: 0 4px;">{{ selectedRowKeys.length }}</a>
          <span>项</span>
          <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        </template>
      </a-alert>

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
    <!-- table区域 end -->

  </a-card>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin';

import '@/assets/less/TableExpand.less';
import { FormTypes } from "@/utils/JEditableTableUtil";

export default {
  name: 'JshOrderProductList',
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
        this.loadData(1);
      }
    }
  },
  data() {
    return {
      description: 'jsh_order_product列表管理页面',
      // disableMixinCreated: true,
      // 表头
      columns: [
        {
          title: '#',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: (t, r, index) => parseInt(index) + 1
        },
        {
          title: '原尺寸ID',
          align: "center",
          dataIndex: 'id'
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
      // 字典选项
      dictOptions: {},
      url: {
        list: '/business/order/jshOrderProduct/list',
        exportXlsUrl: "/business/order/jshOrderProduct/exportXls",
      },
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
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
