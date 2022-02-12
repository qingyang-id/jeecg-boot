<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入名称"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="商品条码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="barcode">
              <a-input v-model="model.barcode" placeholder="请输入商品条码" :disabled="model.id"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="采购价格(元)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="purchasePrice">
              <a-input-number v-model="model.purchasePrice" placeholder="请输入采购价格" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="销售价格(元)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="wholesalePrice">
              <a-input-number v-model="model.wholesalePrice" placeholder="请输入销售价格" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="铝材宽度差(cm)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="aluminumWidthDiff">
              <a-input-number v-model="model.aluminumWidthDiff" placeholder="请输入铝材宽度差" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="铝材高度差(cm)" :labelCol="labelCol" :wrapperCol="wrapperCol"
                               prop="aluminumHeightDiff">
              <a-input-number v-model="model.aluminumHeightDiff" placeholder="请输入铝材高度差" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="玻璃宽度差(cm)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="glassWidthDiff">
              <a-input-number v-model="model.glassWidthDiff" placeholder="请输入玻璃宽度差" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="玻璃高度差(cm)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="glassHeightDiff">
              <a-input-number v-model="model.glassHeightDiff" placeholder="请输入玻璃高度差" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入备注"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-model="model.status" type="radioButton" title="状态" dictCode="status"
                                 placeholder="请选择状态"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

import { httpAction, getAction } from '@/api/manage';
import { validateDuplicateValue } from '@/utils/util';

export default {
  name: 'JshProductForm',
  components: {},
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  data() {
    return {
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
          { required: true, message: '请输入名称!' },
        ],
        purchasePrice: [
          { required: true, message: '请输入采购价格!' },
          { pattern: /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: '请输入正确的金额!' },
        ],
        wholesalePrice: [
          { required: true, message: '请输入采购价格!' },
          { pattern: /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: '请输入正确的金额!' },
        ],
        aluminumWidthDiff: [
          { required: true, message: '请属于铝材宽度差!' },
          { pattern: /^-?\d+$/, message: '请输入正确的数量!' },
        ],
        aluminumHeightDiff: [
          { required: true, message: '请属于铝材高度差!' },
          { pattern: /^-?\d+$/, message: '请输入正确的数量!' },
        ],
        glassWidthDiff: [
          { required: true, message: '请属于玻璃宽度差!' },
          { pattern: /^-?\d+$/, message: '请输入正确的数量!' },
        ],
        glassHeightDiff: [
          { required: true, message: '请属于玻璃高度差!' },
          { pattern: /^-?\d+$/, message: '请输入正确的数量!' },
        ],
      },
      url: {
        add: "/business/product/jshProduct/add",
        edit: "/business/product/jshProduct/edit",
        queryById: "/business/product/jshProduct/queryById"
      }
    };
  },
  computed: {
    formDisabled() {
      return this.disabled;
    },
  },
  created() {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model));
  },
  methods: {
    add() {
      this.modelDefault.status = 1;
      this.edit(this.modelDefault);
    },
    edit(record) {
      this.model = Object.assign({}, record);
      if (this.model.purchasePrice) {
        this.model.purchasePrice = this.model.purchasePrice / 100;
      }
      if (this.model.wholesalePrice) {
        this.model.wholesalePrice = this.model.wholesalePrice / 100;
      }
      this.visible = true;
    },
    submitForm() {
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
          httpAction(httpurl, Object.assign(this.model, {
            purchasePrice: this.model.purchasePrice * 100,
            wholesalePrice: this.model.wholesalePrice * 100
          }), method).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('ok');
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          });
        }

      });
    },
  }
};
</script>
