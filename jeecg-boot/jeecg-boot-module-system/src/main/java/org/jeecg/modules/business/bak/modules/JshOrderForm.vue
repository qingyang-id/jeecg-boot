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
            <a-form-model-item label="客户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
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
              <j-date placeholder="请选择下单时间" v-model="model.orderTime" :show-time="true"
                      date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <a-input v-model="model.address" placeholder="请输入订单总额" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入备注"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
      <!-- 子表单区域 -->
      <a-tabs v-model="activeKey" @change="handleChangeTabs">
        <a-tab-pane tab="原尺寸" :key="refKeys[0]" :forceRender="true">
          <j-vxe-table
            keep-source
            :ref="refKeys[0]"
            :loading="jshOrderProductTable.loading"
            :columns="jshOrderProductTable.columns"
            :dataSource="jshOrderProductTable.dataSource"
            :maxHeight="300"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true">
          </j-vxe-table>
        </a-tab-pane>
        <a-tab-pane tab="扩展信息(抽/拉/条等)" :key="refKeys[1]" :forceRender="true">
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
        <a-tab-pane tab="铝材" :key="refKeys[2]" :forceRender="true">
          <j-vxe-table
            keep-source
            :ref="refKeys[2]"
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
        <a-tab-pane tab="玻璃" :key="refKeys[3]" :forceRender="true">
          <j-vxe-table
            keep-source
            :ref="refKeys[3]"
            :loading="jshOrderProductDetailTable2.loading"
            :columns="jshOrderProductDetailTable2.columns"
            :dataSource="jshOrderProductDetailTable2.dataSource"
            :maxHeight="300"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
      </a-tabs>

    </j-form-container>

  </a-spin>
</template>

<script>

import { getAction, httpAction } from '@/api/manage';
import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin.js';
import { JVXETypes } from '@/components/jeecg/JVxeTable';
import { getRefPromise, VALIDATE_FAILED } from '@/components/jeecg/JVxeTable/utils/vxeUtils.js';
import { validateDuplicateValue } from '@/utils/util';
import JFormContainer from '@/components/jeecg/JFormContainer';

export default {
  name: 'JshOrderForm',
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
      model: {},
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      // confirmLoading: false,
      validatorRules: {
        customerId: [
          { required: true, message: '请选择客户!' },
        ],
        orderTime: [
          { required: true, message: '请选择下单时间!' },
        ],
        address: [
          { required: true, message: '请输入地址!' },
        ],
      },
      refKeys: ['jshOrderProduct', 'jshOrderProductDetail', 'jshOrderProductExtend',],
      tableKeys: ['jshOrderProduct', 'jshOrderProductDetail', 'jshOrderProductExtend',],
      activeKey: 'jshOrderProduct',
      // jsh_order_product
      jshOrderProductTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '产品',
            key: 'product',
            type: JVXETypes.selectDictSearch,
            width: "200px",
            // 【字典表配置信息】：数据库表名,显示字段名,存储字段名
            dict: 'jsh_product,id,name',
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '产品不能为空' }],
            field: 'name,price',
            orgFields: 'name,price',
            destFields: 'product,product_price'
          },
          {
            title: '宽',
            key: 'width',
            type: JVXETypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '高',
            key: 'height',
            type: JVXETypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: JVXETypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '方向',
            key: 'direction',
            type: JVXETypes.select,
            options: [],
            dictCode: "direction",
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '抽/拉/提',
            key: 'product_extend_type',
            type: JVXETypes.select,
            dictCode: "product_extend",
            width: "50px",
            placeholder: '请选择${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '抽/拉/提数量',
            key: 'la',
            type: JVXETypes.input,
            width: "50px",
            placeholder: '请输入${title}',
            defaultValue: 0,
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '单价',
            key: 'product_price',
            type: JVXETypes.normal,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '总价',
            key: 'totalPrice',
            type: JVXETypes.normal,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            customRender: function (t, r) {
              return r && r.product_price ? r.product_price * r.num / 100 : '';
            }
          },
        ]
      },
      // jsh_order_product_extend
      jshOrderProductExtendTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '原尺寸ID',
            key: 'orderProductId',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
          },
          {
            title: '属性',
            key: 'type',
            type: JVXETypes.select,
            disabled: true,
            options: [],
            dictCode: "product_extend",
            width: "200px",
          },
          {
            title: '数量',
            key: 'num',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
          },
        ]
      },
      // jsh_order_product_detail
      jshOrderProductDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '产品',
            key: 'productId',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考宽度',
            key: 'referenceWidth',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考高度',
            key: 'referenceHeight',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '宽',
            key: 'width',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '高',
            key: 'height',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
        ]
      },
      // jsh_order_product_detail
      jshOrderProductDetailTable2: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '产品',
            key: 'productId',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考宽度',
            key: 'referenceWidth',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '参考高度',
            key: 'referenceHeight',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '宽',
            key: 'width',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '高',
            key: 'height',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: JVXETypes.input,
            disabled: true,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
        ]
      },
      url: {
        add: "/business/order/jshOrder/add",
        edit: "/business/order/jshOrder/edit",
        queryById: "/business/order/jshOrder/queryById",
        getSeqNum: "/business/sequence/num",
        jshOrderProduct: {
          list: '/business/order/jshOrder/queryJshOrderProductByMainId',
          getData: '/business/order/jshOrderProduct/queryJshOrderProductExtendByMainId'
        },
        jshOrderProductDetail: {
          list: '/business/order/jshOrderProduct/queryJshOrderProductDetailByMainId'
        },
        jshOrderProductExtend: {
          list: '/business/order/jshOrderProduct/queryJshOrderProductExtendByMainId'
        },
      }
    };
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
    formDisabled() {
      return this.disabled;
    },
  },
  created() {
  },
  methods: {
    add() {
      this.edit({ orderCode: this.orderCode });
    },
    edit(record) {
      console.log('\n\n\n this.orderCode', this.orderCode, this.model);
      console.log('\n\n\n record', record);
      this.visible = true;
    },
    close() {
      this.$emit('close');
      this.visible = false;
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
              this.orderCode = ''; // reset order code
              that.$message.success(res.message);
              that.$emit('ok');
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
            that.close();
          });

        }
      });
    },
    handleCancel() {
      this.close();
    },
    addBefore() {
      this.jshOrderProductTable.dataSource = [];
      this.jshOrderProductExtendTable.dataSource = [];
      this.jshOrderProductDetailTable.dataSource = [];
      this.jshOrderProductDetailTable2.dataSource = [];
    },
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
        this.requestSubTableData(this.url.jshOrderProduct.list, params, this.jshOrderProductTable);
        this.requestSubTableData(this.url.jshOrderProductExtend.list, params, this.jshOrderProductExtendTable);
        this.requestSubTableData(this.url.jshOrderProductDetail.list, Object.assign(params, { type: 1 }), this.jshOrderProductDetailTable);
        this.requestSubTableData(this.url.jshOrderProductDetail.list, Object.assign(params, { type: 2 }), this.jshOrderProductDetailTable);
      }
    },
    //校验所有一对一子表表单
    validateSubForm(allValues) {
      return new Promise((resolve, reject) => {
        Promise.all([
          this.$refs.jshOrderForm.validate(1),
        ]).then(() => {
          resolve(allValues);
        }).catch(e => {
          if (e.error === VALIDATE_NO_PASSED) {
            // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
            this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index];
          } else {
            console.error(e);
          }
        });
      });
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue);
      return {
        ...main, // 展开
        jshOrderProductList: allValues.tablesValue[0].values,
        jshOrderProductExtendList: allValues.tablesValue[1].tableData,
        jshOrderProductDetailList: allValues.tablesValue[2].tableData,
      };
    },
    validateError(msg) {
      this.$message.error(msg);
    },

  }
};
</script>

<style scoped>
</style>
