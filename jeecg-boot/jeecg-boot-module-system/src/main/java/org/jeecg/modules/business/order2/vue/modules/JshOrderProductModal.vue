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
            <a-form-model-item label="产品id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="productId">
              <a-input-number v-model="model.productId" placeholder="请输入产品id" style="width: 100%" />
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
          <a-col :span="24">
            <a-form-model-item label="方向:0其他,1双开,2左开,3右开" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="direction">
              <a-input-number v-model="model.direction" placeholder="请输入方向:0其他,1双开,2左开,3右开" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="颜色" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="color">
              <a-input v-model="model.color" placeholder="请输入颜色" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="单价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="price">
              <a-input-number v-model="model.price" placeholder="请输入单价" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="总价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalPrice">
              <a-input-number v-model="model.totalPrice" placeholder="请输入总价" style="width: 100%" />
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
    name: "JshOrderProductModal",
    components: {
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
        url: {
          add: "/business/order/jshOrderProduct/add",
          edit: "/business/order/jshOrderProduct/edit",
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
