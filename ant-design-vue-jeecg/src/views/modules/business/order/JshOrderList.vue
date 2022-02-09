<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单编码">
              <a-input placeholder="请输入订单编码" v-model="queryParam.orderCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId" dict="jsh_customer,name,id"/>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('jsh_order')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal"
                     @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
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
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys, onChange: onSelectChange, type:'radio'}"
        :customRow="clickThenSelect"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>

    <a-tabs defaultActiveKey="0">
      <a-tab-pane tab="原尺寸" key="0">
        <JshOrderProductList :orderId="selectedMainId"/>
      </a-tab-pane>
      <a-tab-pane tab="铝材" key="1">
        <JshOrderProductDetailList :orderId="selectedMainId" :type="1"/>
      </a-tab-pane>
      <a-tab-pane tab="玻璃" key="2">
        <JshOrderProductDetailList :orderId="selectedMainId" :type="2"/>
      </a-tab-pane>
    </a-tabs>

    <jsh-order-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import JshOrderModal from './modules/JshOrderModal';
import JshOrderProductDetailList from './modules/JshOrderProductDetailList';
import JshOrderProductList from './modules/JshOrderProductList';
import '@/assets/less/TableExpand.less';
import { getAction } from "@api/manage";

export default {
  name: "JshOrderList",
  mixins: [JeecgListMixin],
  components: {
    JshOrderModal, JshOrderProductList, JshOrderProductDetailList
  },
  data() {
    return {
      description: '订单管理页面',
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
          title: '订单编码',
          align: "center",
          sorter: true,
          dataIndex: 'orderCode'
        },
        {
          title: '客户id',
          align: "center",
          dataIndex: 'customerId_dictText'
        },
        {
          title: '订单总额(元)',
          align: "center",
          dataIndex: 'totalPrice',
          customRender: function (t) {
            return t / 100;
          }
        },
        {
          title: '下单时间',
          align: "center",
          dataIndex: 'orderTime',
          customRender: function (text) {
            return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text);
          }
        },
        {
          title: '地址',
          align: "center",
          dataIndex: 'address'
        },
        {
          title: '备注',
          align: "center",
          dataIndex: 'remark'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: "center",
          fixed: "right",
          width: 147,
          scopedSlots: { customRender: 'action' },
        }
      ],
      // 字典选项
      dictOptions: {},
      // 展开的行test
      expandedRowKeys: [],
      url: {
        list: "/business/order/jshOrder/list",
        delete: "/business/order/jshOrder/delete",
        deleteBatch: "/business/order/jshOrder/deleteBatch",
        exportXlsUrl: "/business/order/jshOrder/exportXls",
        importExcelUrl: "/business/order/jshOrder/importExcel",
      },
      selectedMainId: 0, // 选中的ID
      superFieldList: [],
      /* 分页参数 */
      ipagination: {
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '50'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条";
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
    };
  },
  created() {
    this.getSuperFieldList();
  },
  computed: {
    currentId() {
      return this.id;
    },
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
  methods: {
    onOrderTimeChange(value, dateString) {
      console.log(dateString[0], dateString[1]);
      this.queryParam.orderTime_begin = dateString[0];
      this.queryParam.orderTime_end = dateString[1];
    },
    initDictConfig() {
    },
    clickThenSelect(record) {
      return {
        on: {
          click: () => {
            this.onSelectChange([record.id], [record]);
          }
        }
      };
    },
    onClearSelected() {
      this.selectedRowKeys = [];
      this.selectionRows = [];
      this.selectedMainId = 0;
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedMainId = selectedRowKeys[0] || 0;
      this.selectedRowKeys = selectedRowKeys;
      this.selectionRows = selectionRows;
    },
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error("请设置url.list属性!");
        return;
      }
      // 加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      this.onClearSelected();
      const params = this.getQueryParams();//查询条件
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
    getSuperFieldList() {
      let fieldList = [];
      fieldList.push({ type: 'string', value: 'orderCode', text: '订单编码', dictCode: '' });
      fieldList.push({
        type: 'sel_search',
        value: 'customerId',
        text: '客户',
        dictTable: 'jsh_customer',
        dictText: 'name',
        dictCode: 'id'
      });
      fieldList.push({ type: 'double', value: 'totalPrice', text: '订单总额', dictCode: '' });
      fieldList.push({ type: 'date', value: 'orderTime', text: '下单时间' });
      fieldList.push({ type: 'string', value: 'address', text: '地址', dictTable: '' });
      fieldList.push({ type: 'string', value: 'remark', text: '备注', dictCode: '' });
      this.superFieldList = fieldList;
    },
  }
};
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
