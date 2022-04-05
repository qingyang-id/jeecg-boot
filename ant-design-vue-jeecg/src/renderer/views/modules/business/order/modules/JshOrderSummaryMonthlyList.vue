<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId" dict="jsh_customer,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="12" :lg="14" :md="16" :sm="24">
            <a-form-item label="日期">
              <a-range-picker
                  style="width:100%"
                  v-model="queryParam.timeRange"
                  format="YYYY-MM"
                  :placeholder="['开始时间', '结束时间']"
                  @change="onTimeChange"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('订单月数据')">导出</a-button>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal"
                     @handleSuperQuery="handleSuperQuery"></j-super-query>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="id"
          class="j-table-force-nowrap"
          :scroll="{x:true}"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          @change="handleTableChange">
      </a-table>
    </div>
  </a-card>
</template>

<script>
import moment from 'moment';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction } from '@/api/manage';
import '@/assets/less/TableExpand.less';

export default {
  name: "JshOrderSummaryMonthlyList",
  mixins: [JeecgListMixin],
  data() {
    return {
      description: 'jsh_order_summary_monthly管理页面',
      // 表头
      columns: [
        {
          title: '时间',
          align: "center",
          sorter: true,
          dataIndex: 'time',
          customRender: function (text) {
            return !text ? "" : (text.length > 7 ? text.substr(0, 7) : text);
          }
        },
        {
          title: '客户',
          align: "center",
          dataIndex: 'customerId_dictText'
        },
        {
          title: '总订单数',
          align: "center",
          dataIndex: 'totalNum'
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
      url: {
        list: "/business/order/jshOrderSummaryMonthly/list",
        exportXlsUrl: "/business/order/jshOrderSummaryMonthly/exportXls",
      },
      dictOptions: {},
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
      selectedMainId: '',
      superFieldList: [],
    };
  },
  methods: {
    onTimeChange(value, dateString) {
      this.queryParam.time_begin = dateString[0] && moment(dateString[0],'YYYY-MM').startOf("month").format("YYYY-MM-DD") || '';
      this.queryParam.time_end = dateString[1] && moment(dateString[1], 'YYYY-MM').endOf("month").format("YYYY-MM-DD") || '';
    },
    initDictConfig() {
    },
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error("请设置url.list属性!");
        return;
      }
      //加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      var params = this.getQueryParams();//查询条件
      this.loading = true;
      getAction(this.url.list, params).then((res) => {
        if (res.success) {
          this.dataSource = res.result.records;
          this.ipagination.total = res.result.total;
        }
        if (res.code === 510) {
          this.$message.warning(res.message);
        }
        this.loading = false;
      });
    },
  }
};
</script>
<style scoped>
@import '~@assets/less/common.less'
</style>
