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
  </a-table>
</template>

<script>
  import { getAction } from '@api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'JshOrderProductSubTable',
    mixins: [JeecgListMixin],
    props: {
      record: {
        type: Object,
        default: null,
      }
    },
    data() {
      return {
        description: '产品列表',
        disableMixinCreated: true,
        loading: false,
        dataSource: [],
        columns: [
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
        url: {
          listByMainId: '/business/order/jshOrder/queryJshOrderProductByMainId',
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
