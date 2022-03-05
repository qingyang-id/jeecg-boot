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
            <a-form-model-item label="订单ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderId">
              <a-input-number v-model="model.orderId" placeholder="请输入订单ID" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="订单产品表ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderProductId">
              <a-input-number v-model="model.orderProductId" placeholder="请输入订单产品表ID" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <j-dict-select-tag type="list" v-model="model.type" dictCode="product_extend"
                                 placeholder="请选择类型"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="num">
              <a-input-number v-model="model.num" placeholder="请输入数量" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </j-modal>
</template>

<script>

import { httpAction } from '@/api/manage';
import { validateDuplicateValue } from '@/utils/util';

export default {
  name: "JshOrderProductExtendModal",
  components: {},
  props: {
    mainId: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      title: "操作",
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
        orderId: [
          { required: true, message: '请输入订单ID!' },
        ],
        orderProductId: [
          { required: true, message: '请输入订单产品表ID!' },
        ],
        type: [
          { required: true, message: '请输入类型:1拉,2抽,3条!' },
        ],
        num: [
          { required: true, message: '请输入数量!' },
        ],
      },
      url: {
        add: "/order6/jshOrderProduct/addJshOrderProductExtend",
        edit: "/order6/jshOrderProduct/editJshOrderProductExtend",
      }

    };
  },
  created() {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model));
  },
  methods: {
    add() {
      this.edit(this.modelDefault);
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
          this.model['orderProductId'] = this.mainId;
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
  }
};
</script>
