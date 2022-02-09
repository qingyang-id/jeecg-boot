<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">

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
          console.log(this.queryParam);
          this.loadData(1);
        }
      }
    }
  },
  data() {
    return {
      description: 'jsh_order_product列表管理页面',
      disableMixinCreated: true,
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
          title: '订单ID',
          align: "center",
          dataIndex: 'orderId'
        },
        {
          title: '产品',
          align: "center",
          dataIndex: 'productId_dictText'
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
          title: '颜色',
          align: "center",
          dataIndex: 'color'
        },
        {
          title: '单价',
          align: "center",
          dataIndex: 'price'
        },
        {
          title: '总价',
          align: "center",
          dataIndex: 'totalPrice'
        },
      ],
      // 字典选项
      dictOptions: {},
      url: {
        list: '/business/order/jshOrderProduct/list',
        delete: '/order10/jshOrderProduct/delete',
        deleteBatch: '/order10/jshOrderProduct/deleteBatch',
        exportXlsUrl: '/order10/jshOrderProduct/exportXls',
        importExcelUrl: '/order10/jshOrderProduct/importExcel',
      },
      superFieldList: [],
    };
  },
  created() {
  },
  computed: {
    importExcelUrl() {
      return window._CONFIG['domianURL'] + this.url.importExcelUrl;
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
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>