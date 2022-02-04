<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="删除状态:0未删除,1删除" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="delFlag">
              <a-input-number v-model="model.delFlag" placeholder="请输入删除状态:0未删除,1删除" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="客户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
              <a-input-number v-model="model.customerId" placeholder="请输入客户id" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderId">
              <a-input-number v-model="model.orderId" placeholder="请输入订单ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单产品表ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderProductId">
              <a-input-number v-model="model.orderProductId" placeholder="请输入订单产品表ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="产品id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="productId">
              <a-input-number v-model="model.productId" placeholder="请输入产品id" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="类型:1铝材,2玻璃" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <a-input-number v-model="model.type" placeholder="请输入类型:1铝材,2玻璃" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参考宽度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="referenceWidth">
              <a-input-number v-model="model.referenceWidth" placeholder="请输入参考宽度" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参考高度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="referenceHeight">
              <a-input-number v-model="model.referenceHeight" placeholder="请输入参考高度" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="宽" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="width">
              <a-input-number v-model="model.width" placeholder="请输入宽" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="高" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="height">
              <a-input-number v-model="model.height" placeholder="请输入高" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="num">
              <a-input-number v-model="model.num" placeholder="请输入数量" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'JshOrderProductDetailForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
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
           orderProductId: [
              { required: true, message: '请输入订单产品表ID!'},
           ],
           productId: [
              { required: true, message: '请输入产品id!'},
           ],
           type: [
              { required: true, message: '请输入类型:1铝材,2玻璃!'},
           ],
           referenceWidth: [
              { required: true, message: '请输入参考宽度!'},
           ],
           referenceHeight: [
              { required: true, message: '请输入参考高度!'},
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
        },
        url: {
          add: "/business/order/jshOrderProductDetail/add",
          edit: "/business/order/jshOrderProductDetail/edit",
          queryById: "/business/order/jshOrderProductDetail/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }

        })
      },
    }
  }
</script>
