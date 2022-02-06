<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="订单编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderCode">
              <a-input v-model="model.orderCode" placeholder="请输入订单编码" disabled></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="客户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
              <j-search-select-tag v-model="model.customerId" dict="jsh_customer,name,id"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单总额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalPrice">
              <a-input-number v-model="model.totalPrice" placeholder="请输入订单总额" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="下单时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderTime">
              <j-date placeholder="请选择下单时间" v-model="model.orderTime" style="width: 100%"/>
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item label="下单时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderTime">
              <j-date placeholder="请选择下单时间" v-model="model.orderTime" :show-time="true"
                      date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <j-search-select-tag v-model="model.address" dict=""/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入备注"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>

    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="jsh_order_product_detail" :key="refKeys[0]" :forceRender="true">
        <j-vxe-table
          keep-source
          :ref="refKeys[0]"
          :loading="jshOrderProductDetailTable.loading"
          :columns="jshOrderProductDetailTable.columns"
          :dataSource="jshOrderProductDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
          />
      </a-tab-pane>
      <a-tab-pane tab="jsh_order_product_extend" :key="refKeys[1]" :forceRender="true">
        <j-vxe-table
          keep-source
          :ref="refKeys[1]"
          :loading="jshOrderProductExtendTable.loading"
          :columns="jshOrderProductExtendTable.columns"
          :dataSource="jshOrderProductExtendTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
          />
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

  import { getAction } from '@/api/manage'
  import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin.js'
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { getRefPromise,VALIDATE_FAILED} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'

  export default {
    name: 'JshOrderProductForm',
    mixins: [JVxeTableModelMixin],
    components: {
      JFormContainer,
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
        model:{
         },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
           delFlag: [
              { required: true, message: '请输入删除状态:0未删除,1删除!'},
           ],
           customerId: [
              { required: true, message: '请输入客户id!'},
           ],
           orderId: [
              { required: true, message: '请输入订单ID!'},
           ],
           productId: [
              { required: true, message: '请输入产品id!'},
           ],
           width: [
              { required: true, message: '请输入宽!'},
           ],
           height: [
              { required: true, message: '请输入高!'},
           ],
           num: [
              { required: true, message: '请输入数量!'},
           ],
           direction: [
              { required: true, message: '请输入方向:0其他,1双开,2左开,3右开!'},
           ],
           color: [
              { required: true, message: '请输入颜色!'},
           ],
           price: [
              { required: true, message: '请输入单价!'},
           ],
           totalPrice: [
              { required: true, message: '请输入总价!'},
           ],
        },
        refKeys: ['jshOrderProductDetail', 'jshOrderProductExtend', ],
        tableKeys:['jshOrderProductDetail', 'jshOrderProductExtend', ],
        activeKey: 'jshOrderProductDetail',
        // jsh_order_product_detail
        jshOrderProductDetailTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '客户id',
              key: 'customerId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '订单ID',
              key: 'orderId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '订单产品表ID',
              key: 'orderProductId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '产品id',
              key: 'productId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '类型:1铝材,2玻璃',
              key: 'type',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '参考宽度',
              key: 'referenceWidth',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '参考高度',
              key: 'referenceHeight',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '宽',
              key: 'width',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '高',
              key: 'height',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '数量',
              key: 'num',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
          ]
        },
        // jsh_order_product_extend
        jshOrderProductExtendTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '订单ID',
              key: 'orderId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '订单产品表ID',
              key: 'orderProductId',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '属性',
              key: 'type',
              type: JVXETypes.select,
              options:[],
              dictCode:"product_extend",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '数量',
              key: 'num',
               type: JVXETypes.input,
               disabled:true,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
          ]
        },
        url: {
          add: "/business/order/jshOrderProduct/add",
          edit: "/business/order/jshOrderProduct/edit",
          queryById: "/business/order/jshOrderProduct/queryById",
          jshOrderProductDetail: {
            list: '/business/order/jshOrderProduct/queryJshOrderProductDetailByMainId'
          },
          jshOrderProductExtend: {
            list: '/business/order/jshOrderProduct/queryJshOrderProductExtendByMainId'
          },
        }
      }
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
    },
    methods: {
      addBefore(){
        this.jshOrderProductDetailTable.dataSource=[]
        this.jshOrderProductExtendTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.$nextTick(() => {
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.jshOrderProductDetail.list, params, this.jshOrderProductDetailTable)
          this.requestSubTableData(this.url.jshOrderProductExtend.list, params, this.jshOrderProductExtendTable)
        }
      },
      //校验所有一对一子表表单
        validateSubForm(allValues){
            return new Promise((resolve,reject)=>{
              Promise.all([
              ]).then(() => {
                resolve(allValues)
              }).catch(e => {
                if (e.error === VALIDATE_FAILED) {
                  // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
                  this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index]
                } else {
                  console.error(e)
                }
              })
            })
        },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          jshOrderProductDetailList: allValues.tablesValue[0].tableData,
          jshOrderProductExtendList: allValues.tablesValue[1].tableData,
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },

    }
  }
</script>

<style scoped>
</style>
