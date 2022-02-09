<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-row>
        <a-col :span="24">
          <a-form-model-item label="订单编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderCode">
            <a-input v-model="model.orderCode" placeholder="订单编码" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="客户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
            <j-search-select-tag v-model="model.customerId" dict="jsh_customer,name,id"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="订单总额(元)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalPrice">
            <a-input-number v-model="model.totalPrice" placeholder="订单总额" disabled style="width: 100%"/>
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
      <a-tab-pane tab="订单商品" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="jshOrderProductTable.loading"
          :columns="jshOrderProductTable.columns"
          :dataSource="jshOrderProductTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          @valueChange="handleValueChange">
          <template #buttonAfter>
            <a-button style="margin: 0px 0px 8px 0px" @click="batchSetSize('width')">宽-批量设置</a-button>
            <a-button style="margin-left: 8px" @click="batchSetSize('height')">高-批量设置</a-button>
          </template>
        </j-editable-table>
        <!-- 表单区域 -->
        <batch-set-size-modal ref="sizeModalForm" @ok="batchSetSizeModalFormOk"></batch-set-size-modal>
      </a-tab-pane>
    </a-tabs>

  </a-spin>
</template>

<script>

import { getAction, httpAction } from '@/api/manage';
import { FormTypes, getRefPromise, VALIDATE_NO_PASSED, validateFormModelAndTables } from '@/utils/JEditableTableUtil';
import { JEditableTableModelMixin } from '@/mixins/JEditableTableModelMixin';
import BatchSetSizeModal from './BatchSetSizeModal';

export default {
  name: 'JshOrderForm',
  mixins: [JEditableTableModelMixin],
  components: {
    BatchSetSizeModal,
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
      refKeys: ['jshOrderProduct',],
      tableKeys: ['jshOrderProduct',],
      activeKey: 'jshOrderProduct',
      // jsh_order_product
      jshOrderProductTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '产品',
            key: 'productId',
            type: FormTypes.select,
            options: [],
            dictCode: 'jsh_product,name,id',
            allowSearch: true,
            width: "15%",
            placeholder: '请选择${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '宽(cm)',
            key: 'width',
            type: FormTypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '高(cm)',
            key: 'height',
            type: FormTypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: FormTypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '方向',
            key: 'direction',
            type: FormTypes.select,
            options: [],
            dictCode: "direction",
            width: "8%",
            placeholder: '请选择${title}',
            defaultValue: '',
          },
          {
            title: '抽/拉/条',
            key: 'extendType',
            type: FormTypes.select,
            options: [{
              text: '无',
              value: 0
            }],
            dictCode: "product_extend",
            width: "10%",
            placeholder: '请选择${title}',
            defaultValue: 0,
          },
          {
            title: '抽/拉/条数量',
            key: 'extendNum',
            type: FormTypes.inputNumber,
            width: "10%",
            placeholder: '请输入${title}',
            defaultValue: 0,
          },
          {
            title: '颜色',
            key: 'color',
            type: FormTypes.input,
            width: "10%",
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '单价(元)',
            key: 'price',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "8%",
            placeholder: '${title}',
            defaultValue: 0,
            customRender: function (t, r) {
              console.log('\n\n t', t);
              return r.price ? r.price / 100 : 0;
            }
          },
          {
            title: '总价(元)',
            key: 'totalPrice',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "10%",
            placeholder: '${title}',
            defaultValue: 0,
            customRender: function (t, r) {
              return (r.price || 0) * (r.num || 0) * (r.width || 0) * (r.height || 0) / 1000000;
            }
          },
        ],
      },
      jshProductPricesMap: {},
      url: {
        add: "/business/order/jshOrder/add",
        edit: "/business/order/jshOrder/edit",
        queryById: "/business/order/jshOrder/queryById",
        jshOrderProduct: {
          list: '/business/order/jshOrder/queryJshOrderProductByMainId',
          getData: '/business/order/jshOrderProduct/queryJshOrderProductExtendByMainId'
        },
        jshProduct: {
          list: "/business/product/jshProduct/list",
          queryById: "/business/product/jshProduct/queryById"
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
    getAction(this.url.jshProduct.list)
      .then((res) => {
        res.result.records.forEach(item => {
          this.jshProductPricesMap[item.id] = item;
        });
        console.log(res);
      });
  },
  methods: {
    updatePrices({ row, target }) {
      // 更新当列信息
      let extendPrice = 0;
      switch (Number(row.extendType)) {
        case 1: {
          // 抽 20元/3抽
          if (Number(row.extendNum) && row.extendNum % 3 !== 0) {
            // 重置抽的数量
            console.log('row.extendNum', row.extendNum);
            target.setValues([{
              rowKey: row.id,
              values: { extendNum: Math.floor(row.extendNum / 3) * 3 }
            }]);
            return;
          }
          extendPrice = (row.extendNum || 0) * 2000 / 3;
          break;
        }
        case 2: {
          // 拉 10/拉
          extendPrice = (row.extendNum || 0) * 1000;
          break;
        }
        case 3: {
          // 条 12/条
          extendPrice = (row.extendNum || 0) * 1200;
          break;
        }
        default: {
        }
      }
      console.log('\n\n\n product ', this.jshProductPricesMap[row.productId], row.productId)
      const price = (this.jshProductPricesMap[row.productId] && this.jshProductPricesMap[row.productId].wholesalePrice || 0) + extendPrice;
      const totalPrice = price * (row.num || 0) * (row.width || 0) * (row.height || 0) / 1000000;
      // 设置商品价格和总价
      target.setValues([{
        rowKey: row.id,
        values: { price: price / 100, totalPrice }
      }]);
      // 更新汇总信息
      let { values } = target.getValuesSync({ validate: false });
      console.log('row', row.extendNum, values);

      let sum = 0;
      values.forEach(item => sum += (item.totalPrice || 0) * 100);
      this.model.totalPrice = sum / 100;
    },
    /** 当选项被改变时，联动其他组件 */
    async handleValueChange(event) {
      const { row, column, value, target } = event;
      switch (column.key) {
        case 'productId': {
          // 查询商品详情
          if (!value) return;
          if (!this.jshProductPricesMap[value]) {
            const response = await getAction(this.url.jshProduct.queryById, { id: value });
            if (!response.success) {
              this.$message.error(response.message);
              return;
            }
            // 设置商品ID和价格
            this.jshProductPricesMap[value] = response.result;
          }
          this.updatePrices(event);
          break;
        }
        case 'height':
        case 'weight':
        case 'num': {
          if (!value) return;
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        case 'extendType': {
          if (!value) {
            // 设置抽拉条数量
            target.setValues([{
              rowKey: row.id,
              values: { extendNum: 0 }
            }]);
            return;
          }
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        case 'extendNum': {
          if (!value || !row.extendType) return;
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        default: {
        }
      }
    },

    batchSetSize(type) {
      const rows = this.$refs.jshOrderProduct.getSelection();
      if (rows.length <= 0) {
        this.$message.error('请选中需要批量设置的行');
        return;
      }
      this.$refs.sizeModalForm.add(type);
      this.$refs.sizeModalForm.disableSubmit = false;
    },
    batchSetSizeModalFormOk(size, batchType) {
      const rowKeys = this.$refs.jshOrderProduct.getSelection();
      this.$refs.jshOrderProduct.setValues(rowKeys.map(rowKey => ({
        rowKey: rowKey, // 行的id
        values: {
          // 在这里 values 中的 name 是你 columns 中配置的 key
          [batchType]: size - 0,
        }
      })));
    },
    addBefore() {
      this.jshOrderProductTable.dataSource = [];
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
        // id bug
        // this.requestSubTableData(this.url.jshOrderProduct.list, params, this.jshOrderProductTable);
        this.jshOrderProductTable.loading = true;
        getAction(this.url.jshOrderProduct.list, params).then(res => {
          let { result } = res;
          let dataSource = [];
          if (result) {
            if (Array.isArray(result)) {
              dataSource = result;
            } else if (Array.isArray(result.records)) {
              dataSource = result.records;
            }
          }
          this.jshOrderProductTable.dataSource = dataSource.map(item => Object.assign(item, { id: undefined }));
        }).finally(() => {
          this.jshOrderProductTable.loading = false;
        });
        console.log('\n\n\n jsh', this.jshOrderProductTable);
      }
    },
    // 校验所有一对一子表表单
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
      const jshOrderProductPageList = allValues.tablesValue[0].values;
      const jshOrderProductDetailList = [];
      const jshOrderProductExtendList = [];
      if (jshOrderProductPageList.length) {
        jshOrderProductPageList.forEach(item => {
          const productDetail = {
            referenceWidth: Number(item.width),
            referenceHeight: Number(item.height),
            num: Number(item.num),
            color: item.color,
          };
          switch (Number(item.extendType)) {
            case 1: {
              // 抽 高/3 - 2
              Object.assign(productDetail, {
                referenceHeight: Math.ceil(item.height / 3) - 2,
                num: item.num * 3
              });
              jshOrderProductExtendList.push({
                type: item.extendType,
                num: item.extendNum,
              });
              break;
            }
            case 2: {
              // 拉 原尺寸高/2 - 2
              Object.assign(productDetail, {
                referenceHeight: Math.ceil(item.height / 2) - 2,
                num: item.num * 2
              });
              jshOrderProductExtendList.push({
                type: item.extendType,
                num: item.extendNum,
              });
              break;
            }
            case 3: {
              jshOrderProductExtendList.push({
                extendType: item.extendType,
                extendNum: item.extendNum,
              });
              break;
            }
          }
          jshOrderProductDetailList.push({
            ...productDetail,
            type: 1,
            width: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].aluminumWidthDiff,
            height: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].aluminumHeightDiff,
          });
          jshOrderProductDetailList.push({
            ...productDetail,
            type: 2,
            width: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].grassWidthDiff,
            height: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].grassHeightDiff,
          });
          Object.assign(item, {
            id: '',
            jshOrderProductExtendList,
            jshOrderProductDetailList,
            price: item.price * 100,
            totalPrice: item.totalPrice * 100,
          });
        });
      }
      console.log('\n\n\n product list ', jshOrderProductPageList);
      return {
        ...main, // 展开
        totalPrice: main.totalPrice * 100,
        jshOrderProductPageList,
      };
    },

    /** 确定按钮点击事件 */
    handleOk() {
      /** 触发表单验证 */
      this.getAllTable().then(tables => {
        console.log('tables ', tables);
        return validateFormModelAndTables(this.$refs.form, this.model, tables);
      }).then(allValues => {
        console.log('allValues ', allValues);
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
          this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index];
        } else {
          this.$message.error('信息有误');
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
