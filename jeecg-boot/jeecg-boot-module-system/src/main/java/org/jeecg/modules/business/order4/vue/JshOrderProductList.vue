<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">

    <!-- 查询区域 begin -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域 end -->

    <!-- 操作按钮区域 begin -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('jsh_order_product')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            <span>删除</span>
          </a-menu-item>
        </a-menu>
        <a-button>
          <span>批量操作</span>
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>
    <!-- 操作按钮区域 end -->

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
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :loading="loading"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :expandedRowKeys="expandedRowKeys"
        :rowSelection="{selectedRowKeys, onChange: onSelectChange}"
        @expand="handleExpand"
        @change="handleTableChange"
      >

        <!-- 内嵌table区域 begin -->
        <template slot="expandedRowRender" slot-scope="record">
          <a-tabs tabPosition="top">
            <a-tab-pane tab="jsh_order_product_detail" key="jshOrderProductDetail" forceRender>
              <jsh-order-product-detail-sub-table :record="record"/>
            </a-tab-pane>
            <a-tab-pane tab="jsh_order_product_extend" key="jshOrderProductExtend" forceRender>
              <jsh-order-product-extend-sub-table :record="record"/>
            </a-tab-pane>
          </a-tabs>
        </template>
        <!-- 内嵌table区域 end -->

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>

        <template slot="imgSlot" slot-scope="text">
          <div style="font-size: 12px;font-style: italic;">
            <span v-if="!text">无图片</span>
            <img v-else :src="getImgView(text)" alt="" style="max-width:80px;height:25px;"/>
          </div>
        </template>


        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            ghost
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)"
          >
            <span>下载</span>
          </a-button>
        </template>

        <template slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">
              <span>更多 <a-icon type="down"/></span>
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>

        </template>

      </a-table>
    </div>
    <!-- table区域 end -->

    <!-- 表单区域 -->
    <jsh-order-product-modal ref="modalForm" @ok="modalFormOk"/>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JshOrderProductModal from './modules/JshOrderProductModal'
  import JshOrderProductDetailSubTable from './subTables/JshOrderProductDetailSubTable'
  import JshOrderProductExtendSubTable from './subTables/JshOrderProductExtendSubTable'
  import '@/assets/less/TableExpand.less'

  export default {
    name: 'JshOrderProductList',
    mixins: [JeecgListMixin],
    components: {
      JshOrderProductModal,
      JshOrderProductDetailSubTable,
      JshOrderProductExtendSubTable,
    },
    data() {
      return {
        description: 'jsh_order_product列表管理页面',
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
            title: '删除状态:0未删除,1删除',
            align: 'center',
            dataIndex: 'delFlag',
          },
          {
            title: '客户id',
            align: 'center',
            dataIndex: 'customerId',
          },
          {
            title: '订单ID',
            align: 'center',
            dataIndex: 'orderId',
          },
          {
            title: '产品id',
            align: 'center',
            dataIndex: 'productId',
          },
          {
            title: '宽',
            align: 'center',
            dataIndex: 'width',
          },
          {
            title: '高',
            align: 'center',
            dataIndex: 'height',
          },
          {
            title: '数量',
            align: 'center',
            dataIndex: 'num',
          },
          {
            title: '方向:0其他,1双开,2左开,3右开',
            align: 'center',
            dataIndex: 'direction',
          },
          {
            title: '颜色',
            align: 'center',
            dataIndex: 'color',
          },
          {
            title: '单价',
            align: 'center',
            dataIndex: 'price',
          },
          {
            title: '总价',
            align: 'center',
            dataIndex: 'totalPrice',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            width:147,
            scopedSlots: { customRender: 'action' },
          },
        ],
        // 字典选项
        dictOptions: {},
        // 展开的行test
        expandedRowKeys: [],
        url: {
          list: '/business/order/jshOrderProduct/list',
          delete: '/business/order/jshOrderProduct/delete',
          deleteBatch: '/business/order/jshOrderProduct/deleteBatch',
          exportXlsUrl: '/business/order/jshOrderProduct/exportXls',
          importExcelUrl: '/business/order/jshOrderProduct/importExcel',
        },
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl() {
        return window._CONFIG['domianURL'] + this.url.importExcelUrl
      }
    },
    methods: {
      initDictConfig() {
      },

      handleExpand(expanded, record) {
        this.expandedRowKeys = []
        if (expanded === true) {
          this.expandedRowKeys.push(record.id)
        }
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'int',value:'delFlag',text:'删除状态:0未删除,1删除',dictCode:''})
        fieldList.push({type:'int',value:'customerId',text:'客户id',dictCode:''})
        fieldList.push({type:'int',value:'orderId',text:'订单ID',dictCode:''})
        fieldList.push({type:'int',value:'productId',text:'产品id',dictCode:''})
        fieldList.push({type:'int',value:'width',text:'宽',dictCode:''})
        fieldList.push({type:'int',value:'height',text:'高',dictCode:''})
        fieldList.push({type:'int',value:'num',text:'数量',dictCode:''})
        fieldList.push({type:'int',value:'direction',text:'方向:0其他,1双开,2左开,3右开',dictCode:''})
        fieldList.push({type:'string',value:'color',text:'颜色',dictCode:''})
        fieldList.push({type:'int',value:'price',text:'单价',dictCode:''})
        fieldList.push({type:'int',value:'totalPrice',text:'总价',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
</style>
