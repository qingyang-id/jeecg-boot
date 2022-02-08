<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="客户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
              <a-input-number v-model="model.customerId"placeholder="请输入客户id" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderId">
              <a-input-number v-model="model.orderId"placeholder="请输入订单ID" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单产品表ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderProductId">
              <a-input-number v-model="model.orderProductId"placeholder="请输入订单产品表ID" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="产品id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="productId">
              <a-input-number v-model="model.productId"placeholder="请输入产品id" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="类型:1铝材,2玻璃" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <a-input-number v-model="model.type"placeholder="请输入类型:1铝材,2玻璃" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参考宽度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="referenceWidth">
              <a-input-number v-model="model.referenceWidth"placeholder="请输入参考宽度" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参考高度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="referenceHeight">
              <a-input-number v-model="model.referenceHeight"placeholder="请输入参考高度" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="宽" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="width">
              <a-input-number v-model="model.width"placeholder="请输入宽" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="高" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="height">
              <a-input-number v-model="model.height"placeholder="请输入高" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="num">
              <a-input-number v-model="model.num"placeholder="请输入数量" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: "JshOrderProductDetailModal",
    components: {
    },
    props:{
      mainId:{
        type:String,
        required:false,
        default:''
      }
    },
    data () {
      return {
        title:"操作",
        width:800,
        visible: false,
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
          add: "/order6/jshOrderProduct/addJshOrderProductDetail",
          edit: "/order6/jshOrderProduct/editJshOrderProductDetail",
        }

      }
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
      close () {
        this.$emit('close');
        this.visible = false;
        this.$refs.form.clearValidate();
      },
      handleOk () {
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
            this.model['orderProductId'] = this.mainId
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }else{
             return false
          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>
