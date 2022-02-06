<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('jsh_order_product')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
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

    <jshOrderProduct-modal ref="modalForm" @ok="modalFormOk"></jshOrderProduct-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JshOrderProductModal from './modules/JshOrderProductModal'
  import '@/assets/less/TableExpand.less'

  export default {
    name: "JshOrderProductList",
    mixins:[JeecgListMixin],
    components: {
      JshOrderProductModal
    },
    data () {
      return {
        description: 'jsh_order_product管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'删除状态:0未删除,1删除',
            align:"center",
            dataIndex: 'delFlag'
          },
          {
            title:'客户id',
            align:"center",
            dataIndex: 'customerId'
          },
          {
            title:'订单ID',
            align:"center",
            dataIndex: 'orderId'
          },
          {
            title:'产品id',
            align:"center",
            dataIndex: 'productId'
          },
          {
            title:'宽',
            align:"center",
            dataIndex: 'width'
          },
          {
            title:'高',
            align:"center",
            dataIndex: 'height'
          },
          {
            title:'数量',
            align:"center",
            dataIndex: 'num'
          },
          {
            title:'方向:0其他,1双开,2左开,3右开',
            align:"center",
            dataIndex: 'direction'
          },
          {
            title:'颜色',
            align:"center",
            dataIndex: 'color'
          },
          {
            title:'单价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'总价',
            align:"center",
            dataIndex: 'totalPrice'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/business/order/jshOrder/list",
          delete: "/business/order/jshOrder/delete",
          deleteBatch: "/business/order/jshOrder/deleteBatch",
          exportXlsUrl: "/business/order/jshOrder/exportXls",
          importExcelUrl: "/business/order/jshOrder/importExcel",
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
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
<style scoped>
  @import '~@assets/less/common.less';
</style>
