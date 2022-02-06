<template>
  <a-table
    rowKey="id"
    size="middle"
    bordered
    :loading="loading"
    :columns="columns"
    :dataSource="dataSource"
    :pagination="false"
  >

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

  </a-table>
</template>

<script>
  import { getAction } from '@api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'JshOrderProductDetailSubTable',
    mixins: [JeecgListMixin],
    props: {
      record: {
        type: Object,
        default: null,
      }
    },
    data() {
      return {
        description: 'jsh_order_product_detail内嵌列表',
        disableMixinCreated: true,
        loading: false,
        dataSource: [],
        columns: [
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
            title: '订单产品表ID',
            align: 'center',
            dataIndex: 'orderProductId',
          },
          {
            title: '产品id',
            align: 'center',
            dataIndex: 'productId',
          },
          {
            title: '类型:1铝材,2玻璃',
            align: 'center',
            dataIndex: 'type',
          },
          {
            title: '参考宽度',
            align: 'center',
            dataIndex: 'referenceWidth',
          },
          {
            title: '参考高度',
            align: 'center',
            dataIndex: 'referenceHeight',
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
        ],
        url: {
          listByMainId: '/business/order/jshOrderProduct/queryJshOrderProductDetailByMainId',
        },
      }
    },
    watch: {
      record: {
        immediate: true,
        handler() {
          if (this.record != null) {
            this.loadData(this.record)
          }
        }
      }
    },
    methods: {

      loadData(record) {
        this.loading = true
        this.dataSource = []
        getAction(this.url.listByMainId, {
          id: record.id
        }).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
          }
        }).finally(() => {
          this.loading = false
        })
      },

    },
  }
</script>

<style scoped>

</style>
