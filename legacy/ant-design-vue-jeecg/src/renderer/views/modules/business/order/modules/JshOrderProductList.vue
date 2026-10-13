<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper" v-show="search">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单编码">
              <a-input placeholder="请输入订单编码" v-model="queryParam.orderCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId" :dictOptions="customerDictOptions"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="12" :lg="14" :md="16" :sm="24">
              <a-form-item label="订单日期">
                <a-range-picker
                    style="width:100%"
                    v-model="queryParam.orderTimeRange"
                    format="YYYY-MM-DD"
                    :placeholder="['开始时间', '结束时间']"
                    @change="onOrderTimeChange"
                />
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('订单明细')">导出</a-button>
    </div>
    <!-- table区域 begin -->
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
        @change="handleTableChange">
    </a-table>
    <!-- table区域 end -->

  </a-card>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction } from '@/api/manage';

import '@/assets/less/TableExpand.less';

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
    },
    search: {
      type: Boolean,
      default: true,
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
          title: '规格(高*宽*数量)',
          align: "left",
          dataIndex: 'height',
          customRender: function (t, r) {
            return `${r.height} * ${r.width} * ${r.num}`;
          },
        },
        {
          title: '方向',
          align: "center",
          dataIndex: 'direction_dictText',
          customRender: function (t, r) {
            if (!r.direction) return '';
            return t;
          },
        },
        {
          title: '抽/拉/条',
          align: "left",
          dataIndex: 'extendType_dictText',
          customRender: function (t, r) {
            if (!r.extendNum) return '';
            return `${r.extendType_dictText} * ${r.extendNum}`;
          },
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
          title: '附加(元)',
          align: "center",
          dataIndex: 'extendPrice',
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
      customerDictOptions: [],
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
    initDictConfig() {
      //初始化字典 - 客户
      getAction('/business/customer/jshCustomer/list?status=1&pageSize=5000&column=rank&order=asc').then((res) => {
        if (res.success) {
          this.customerDictOptions = res.result.records
              .sort((a, b) => a.rank - b.rank)
              .map(item => ({
                value: `${item.id}`,
                text: item.name,
                title: item.name,
              }));
        }
      });
    },
  }
};
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
