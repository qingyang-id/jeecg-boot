<template>
  <j-modal>
    <a-spin :spinning="confirmLoading">

      <a-tabs v-model="activeKey" @change="handleChangeTabs">
        <!--主表区域 -->
        <a-tab-pane tab="jsh_order_product" :key="refKeys[0]" :forceRender="true">
          <a-form-model ref="form" :model="model" :rules="validatorRules">
            <a-row>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="删除状态:0未删除,1删除" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="delFlag">
                  <a-input-number v-model="model.delFlag" placeholder="请输入删除状态:0未删除,1删除" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="客户id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
                  <a-input-number v-model="model.customerId" placeholder="请输入客户id" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="订单ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderId">
                  <a-input-number v-model="model.orderId" placeholder="请输入订单ID" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="产品id" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="productId">
                  <a-input-number v-model="model.productId" placeholder="请输入产品id" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="宽" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="width">
                  <a-input-number v-model="model.width" placeholder="请输入宽" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="高" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="height">
                  <a-input-number v-model="model.height" placeholder="请输入高" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="num">
                  <a-input-number v-model="model.num" placeholder="请输入数量" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="方向:0其他,1双开,2左开,3右开" :labelCol="labelCol" :wrapperCol="wrapperCol"
                                   prop="direction">
                  <a-input-number v-model="model.direction" placeholder="请输入方向:0其他,1双开,2左开,3右开" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="颜色" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="color">
                  <a-input v-model="model.color" placeholder="请输入颜色"></a-input>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="单价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="price">
                  <a-input-number v-model="model.price" placeholder="请输入单价" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-form-model-item label="总价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalPrice">
                  <a-input-number v-model="model.totalPrice" placeholder="请输入总价" style="width: 100%"/>
                </a-form-model-item>
              </a-col>

            </a-row>
          </a-form-model>

        </a-tab-pane>
        <!--子表单区域 -->
        <a-tab-pane tab="jsh_order_product_detail" :key="refKeys[1]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[1]"
            :loading="jshOrderProductDetailTable.loading"
            :columns="jshOrderProductDetailTable.columns"
            :dataSource="jshOrderProductDetailTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>

        <a-tab-pane tab="jsh_order_product_extend" :key="refKeys[2]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[2]"
            :loading="jshOrderProductExtendTable.loading"
            :columns="jshOrderProductExtendTable.columns"
            :dataSource="jshOrderProductExtendTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>

      </a-tabs>

    </a-spin>
  </j-modal>
</template>

<script>

import { FormTypes, getRefPromise } from '@/utils/JEditableTableUtil';
import { JEditableTableModelMixin } from '@/mixins/JEditableTableModelMixin';
import { validateDuplicateValue } from '@/utils/util';
import { VALIDATE_NO_PASSED, validateFormModelAndTables } from '@/utils/JEditableTableUtil';

export default {
  name: 'JshOrderProductForml',
  mixins: [JEditableTableModelMixin],
  components: {},
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
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      model: {},
      validatorRules: {
        delFlag: [
          { required: true, message: '请输入删除状态:0未删除,1删除!' },
        ],
        customerId: [
          { required: true, message: '请输入客户id!' },
        ],
        orderId: [
          { required: true, message: '请输入订单ID!' },
        ],
        productId: [
          { required: true, message: '请输入产品id!' },
        ],
        width: [
          { required: true, message: '请输入宽!' },
        ],
        height: [
          { required: true, message: '请输入高!' },
        ],
        num: [
          { required: true, message: '请输入数量!' },
        ],
        direction: [
          { required: true, message: '请输入方向:0其他,1双开,2左开,3右开!' },
        ],
        color: [
          { required: true, message: '请输入颜色!' },
        ],
        price: [
          { required: true, message: '请输入单价!' },
        ],
        totalPrice: [
          { required: true, message: '请输入总价!' },
        ],
      },
      refKeys: ['jshOrderProduct', 'jshOrderProductDetail', 'jshOrderProductExtend',],
      tableKeys: ['jshOrderProductDetail', 'jshOrderProductExtend',],
      activeKey: 'jshOrderProduct',
      // jsh_order_product_detail
      jshOrderProductDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '客户id',
            key: 'customerId',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '订单ID',
            key: 'orderId',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '订单产品表ID',
            key: 'orderProductId',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '产品id',
            key: 'productId',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '类型:1铝材,2玻璃',
            key: 'type',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考宽度',
            key: 'referenceWidth',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考高度',
            key: 'referenceHeight',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '宽',
            key: 'width',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '高',
            key: 'height',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
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
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '订单产品表ID',
            key: 'orderProductId',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '类型:1拉,2抽,3条',
            key: 'type',
            type: FormTypes.select,
            dictCode: "product_extend",
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
        ]
      },
      url: {
        add: "/order11/jshOrderProduct/add",
        edit: "/order11/jshOrderProduct/edit",
        jshOrderProduct: {
          list: '/order11/jshOrderProduct/queryById'
        },
        jshOrderProductDetail: {
          list: '/order11/jshOrderProduct/queryJshOrderProductDetailByMainId'
        },
        jshOrderProductExtend: {
          list: '/order11/jshOrderProduct/queryJshOrderProductExtendByMainId'
        },
      }
    };
  },
  methods: {
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key));
      return Promise.all(values);
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      this.$nextTick(() => {
      });
      // 加载子表数据
      if (this.model.id) {
        let params = { id: this.model.id };
        this.requestSubTableData(this.url.jshOrderProductDetail.list, params, this.jshOrderProductDetailTable);
        this.requestSubTableData(this.url.jshOrderProductExtend.list, params, this.jshOrderProductExtendTable);
      }
    },
    //校验所有一对一子表表单
    validateSubForm(allValues) {
      return new Promise((resolve, reject) => {
        Promise.all([]).then(() => {
          resolve(allValues);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue);
      return {
        ...main, // 展开
        jshOrderProductDetailList: allValues.tablesValue[0].values,
        jshOrderProductExtendList: allValues.tablesValue[1].values,
      };
    },
    /** 确定按钮点击事件 */
    handleOk() {
      /** 触发表单验证 */
      this.getAllTable().then(tables => {
        return validateFormModelAndTables(this.$refs.form, this.model, tables);
      }).then(allValues => {
        /** 一次性验证一对一的所有子表 */
        return this.validateSubForm(allValues);
      }).then(allValues => {
        if (typeof this.classifyIntoFormData !== 'function') {
          throw this.throwNotFunction('classifyIntoFormData');
        }
        console.log("this.classifyIntoFormData", typeof this.classifyIntoFormData);
        let formData = this.classifyIntoFormData(allValues);

        // 发起请求
        return this.request(formData);
      }).catch(e => {
        if (e.error === VALIDATE_NO_PASSED) {
          // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
          this.activeKey = e.index == null ? this.refKeys[0] : this.refKeys[e.index + 1];
        } else {
          console.error(e);
        }
      });
    },
    validateError(msg) {
      this.$message.error(msg);
    },
    close() {
      this.visible = false;
      this.$emit('close');
      this.$refs.form.clearValidate();
    },

  }
};
</script>

<style scoped>
</style>
