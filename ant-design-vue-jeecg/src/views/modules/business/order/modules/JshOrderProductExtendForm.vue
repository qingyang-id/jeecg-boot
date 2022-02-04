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
            <a-form-model-item label="订单产品表ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderProductId">
              <a-input-number v-model="model.orderProductId" placeholder="请输入订单产品表ID" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="类型:1拉,2抽,3条" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <a-input-number v-model="model.type" placeholder="请输入类型:1拉,2抽,3条" style="width: 100%" />
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
    name: 'JshOrderProductExtendForm',
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
           orderProductId: [
              { required: true, message: '请输入订单产品表ID!'},
           ],
           type: [
              { required: true, message: '请输入类型:1拉,2抽,3条!'},
           ],
           num: [
              { required: true, message: '请输入数量!'},
           ],
        },
        url: {
          add: "/business/order/jshOrderProductExtend/add",
          edit: "/business/order/jshOrderProductExtend/edit",
          queryById: "/business/order/jshOrderProductExtend/queryById"
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
