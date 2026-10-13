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
              <j-search-select-tag placeholder="请选择客户" v-model="queryParam.customerId"
                                   :dictOptions="customerDictOptions"/>
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
      <a-button type="primary" icon="download" @click="handleExportXls('订单信息')">导出</a-button>
      <!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
      <!--                @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
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
          :sorter="isorter"
          :loading="loading"
          :rowSelection="{selectedRowKeys, onChange: onSelectChange, type:'radio'}"
          :customRow="clickThenSelect"
          @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="printDetail(record,'生产单')">生产单打印</a>
          <a-divider type="vertical"/>
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="printDetail(record, '订单信息')">订单打印</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="printDetail(record,'铝材生产单')">铝材打印</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="printDetail(record,'玻璃生产单')">玻璃打印</a>
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
        <JshOrderProductList :orderId="selectedMainId" :search="false"/>
      </a-tab-pane>
      <a-tab-pane tab="铝材" key="1">
        <JshOrderProductDetailList :orderId="selectedMainId" :type="1"/>
      </a-tab-pane>
      <a-tab-pane tab="玻璃" key="2">
        <JshOrderProductDetailList :orderId="selectedMainId" :type="2"/>
      </a-tab-pane>
    </a-tabs>

    <jsh-order-modal ref="modalForm" @ok="modalFormOk"/>

    <order-detail ref="modalDetail"></order-detail>
  </a-card>
</template>

<script>
import { getAction } from '@/api/manage';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import JshOrderModal from './modules/JshOrderModal';
import JshOrderProductDetailList from './modules/JshOrderProductDetailList';
import JshOrderProductList from './modules/JshOrderProductList';
import OrderDetail from './dialog/OrderDetail';
import '@/assets/less/TableExpand.less';

export default {
  name: "JshOrderList",
  mixins: [JeecgListMixin],
  components: {
    JshOrderModal, JshOrderProductList, JshOrderProductDetailList, OrderDetail
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
          title: '客户',
          align: "center",
          dataIndex: 'customerId_dictText'
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
        {
          title: '下单时间',
          align: "center",
          sorter: true,
          dataIndex: 'orderTime',
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
          title: '更新时间',
          align: "center",
          sorter: true,
          dataIndex: 'updateTime',
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
        column: 'orderCode',
        order: 'desc',
      },
      // 字典选项
      dictOptions: {},
      // 展开的行test
      expandedRowKeys: [],
      url: {
        list: "/business/order/jshOrder/list",
        delete: "/business/order/jshOrder/delete",
        deleteBatch: "/business/order/jshOrder/deleteBatch",
        exportXlsUrl: "/business/order/jshOrder/exportXls",
      },
      selectedMainId: 0, // 选中的ID
      superFieldList: [],
      customerDictOptions: [],
    };
  },
  created() {
    this.getSuperFieldList();
    this.initDictConfig();
  },
  computed: {
    currentId() {
      return this.id;
    },
  },
  methods: {
    printDetail(record, type) {
      this.$refs.modalDetail.myHandleDetail(record, type);
    },
    onOrderTimeChange(value, dateString) {
      console.log(dateString[0], dateString[1]);
      this.queryParam.orderTime_begin = dateString[0];
      this.queryParam.orderTime_end = dateString[1];
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
