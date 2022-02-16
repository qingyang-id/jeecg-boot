<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="客户名字" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入客户名字"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-model="model.sex" type="radioButton" title="性别" dictCode="sex"
                                 placeholder="请选择性别"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="联系地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <a-input v-model="model.address" placeholder="请输入联系地址"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="手机" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="phone">
              <a-input v-model="model.phone" placeholder="请输入手机"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </j-modal>
</template>

<script>
import { httpAction } from '@/api/manage';

export default {
  name: 'JshCustomerModal',
  components: {},
  data() {
    return {
      title: '',
      width: 800,
      visible: false,
      model: {},
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
        name: [
          { required: true, message: '请输入客户名字!' },
        ],
        sex: [
          { required: true, message: '请选择性别!' },
        ],
        address: [
          { required: false, message: '请输入常用地址!' },
        ],
        phone: [
          { required: false, message: '请输入手机!' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!' },
        ],
      },
      url: {
        add: "/business/customer/jshCustomer/add",
        edit: "/business/customer/jshCustomer/edit",
      }
    };
  },
  methods: {
    add() {
      this.edit({ sex: 1 });
    },
    edit(record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    close() {
      this.$emit('close');
      this.visible = false;
      this.$refs.form.clearValidate();
    },
    handleOk() {
      const that = this;
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if (!this.model.id) {
            httpurl += this.url.add;
            method = 'post';
          } else {
            httpurl += this.url.edit;
            method = 'put';
          }
          httpAction(httpurl, this.model, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('ok');
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
            that.close();
          });
        } else {
          return false;
        }
      });
    },
    handleCancel() {
      this.close();
    },
    popupCallback(value, row) {
      this.model = Object.assign(this.model, row);
    },
  }
};
</script>
