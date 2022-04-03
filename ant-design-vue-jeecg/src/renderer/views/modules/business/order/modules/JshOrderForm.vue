<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="订单编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderCode">
              <a-input v-model="model.orderCode" placeholder="订单编码" disabled></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="总面积(m²)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalArea">
              <a-input v-model="model.totalArea" placeholder="总面积(m²)" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="订单总额(元)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalPrice">
              <a-input v-model="model.totalPrice" placeholder="订单总额" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="客户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId">
              <j-search-select-tag v-model="model.customerId" dict="jsh_customer,name,id" @change="updateAddresses"/>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <j-search-select-tag v-model="model.address" :dictOptions="addressDictOptions"/>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="下单时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orderTime">
              <j-date placeholder="请选择下单时间" v-model="model.orderTime" :show-time="true"
                      date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-textarea v-model="model.remark" rows="4" placeholder="请输入备注" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="订单商品" :key="refKeys[0]" :forceRender="true">
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
            :toolbar="true"
            :alwaysEdit="true"
            @remove="calculateSumPrice"
            @valueChange="handleValueChange">
          <template v-slot:toolbarSuffix>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('height')">高-批量设置</a-button>
            <a-button :disabled="disabled" style="margin: 0px 0px 8px 0px" @click="batchSetSize('width')">宽-批量设置</a-button>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('num')">数量-批量设置</a-button>
            <a-button :disabled="disabled" style="margin-left: 8px" @click="batchSetSize('extendNum')">抽/拉/条数量-批量设置</a-button>
          </template>
          <template v-slot:action="props">
            <a :disabled="disabled"  @click="handleCopy(props)">复制</a>
          </template>
        </j-vxe-table>
        <!-- 表单区域 -->
        <batch-set-size-modal ref="sizeModalForm" @ok="batchSetSizeModalFormOk"></batch-set-size-modal>
      </a-tab-pane>
    </a-tabs>

  </a-spin>
</template>

<script>
import BigNumber from 'bignumber.js';
import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin.js';
import {
  VALIDATE_FAILED,
  getRefPromise,
  validateFormModelAndTables
} from '@/components/jeecg/JVxeTable/utils/vxeUtils';
import { JVXETypes } from '@/components/jeecg/JVxeTable';
import { getAction, httpAction, putAction } from '@/api/manage';
import BatchSetSizeModal from './BatchSetSizeModal';

export default {
  name: 'JshOrderForm',
  mixins: [JVxeTableModelMixin],
  components: {
    BatchSetSizeModal,
  },
  data() {
    return {
      addressDictOptions: [{ text: '请选择', value: '', disabled: true }],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      model: {},
      // 新增时子表默认添加1行空数据
      addDefaultRowNum: 1,
      // confirmLoading: false,
      validatorRules: {
        customerId: [
          { required: true, message: '请选择客户!' },
        ],
        address: [
          { required: true, message: '请选择地址!' },
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
            type: JVXETypes.select,
            options: [],
            dictCode: 'jsh_product,name,id',
            allowSearch: true,
            width: "10%",
            placeholder: '请选择${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '铝材颜色',
            key: 'color',
            type: JVXETypes.select,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            options: [{
              text: '默认',
              value: ''
            }, {
              text: '黑色',
              value: '黑色'
            }, {
              text: '金色',
              value: '金色'
            }, {
              text: '亮色',
              value: '亮色'
            }, {
              text: '哑光',
              value: '哑光'
            }],
          },
          {
            title: '玻璃色号',
            key: 'glassColor',
            type: JVXETypes.input,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '高(mm)',
            key: 'height',
            type: JVXETypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '宽(mm)',
            key: 'width',
            type: JVXETypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: '',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '数量',
            key: 'num',
            type: JVXETypes.inputNumber,
            width: "6%",
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
            width: "8%",
            placeholder: '请选择${title}',
            defaultValue: '0',
          },
          {
            title: '抽/拉/条',
            key: 'extendType',
            type: JVXETypes.select,
            options: [],
            dictCode: "product_extend",
            width: "10%",
            placeholder: '请选择${title}',
            defaultValue: '0',
          },
          {
            title: '抽/拉/条数量',
            key: 'extendNum',
            type: JVXETypes.inputNumber,
            width: "8%",
            placeholder: '请输入${title}',
            defaultValue: 0,
          },
          {
            title: '单价(元)',
            key: 'price',
            type: JVXETypes.input,
            disabled: true,
            width: "8%",
            placeholder: '${title}',
            defaultValue: '0',
            // formatter({ cellValue }) {
            //   return cellValue ? cellValue / 100 : 0;
            // },
          },
          {
            title: '附加(元)',
            key: 'extendPrice',
            type: JVXETypes.input,
            disabled: true,
            width: "8%",
            placeholder: '${title}',
            defaultValue: '0',
          },
          {
            title: '总面积(m²)',
            key: 'totalArea',
            type: JVXETypes.input,
            disabled: true,
            width: "8%",
            placeholder: '${title}',
            defaultValue: '0',
            // formatter({ cellValue }) {
            //   return cellValue && cellValue / 100 || 0;
            // },
          },
          {
            title: '总价(元)',
            key: 'totalPrice',
            type: JVXETypes.input,
            disabled: true,
            width: "8%",
            placeholder: '${title}',
            defaultValue: '0',
            // formatter({ cellValue }) {
            //   return cellValue && cellValue / 100 || 0;
            // },
          },
          {
            title: '操作',
            key: 'action',
            type: JVXETypes.slot,
            fixed: 'right',
            minWidth: '6%',
            align: 'center',
            slotName: 'action',
          }
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
        jshCustomer: {
          listJshCustomerAddressByMainId: "/business/customer/jshCustomer/listJshCustomerAddressByMainId"
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
      console.log('this.disabled ', this.disabled)
      return this.disabled;
    },
  },
  created() {
    // 备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model));
    this.initProductsMap();
  },
  methods: {
    initProductsMap() {
      getAction(this.url.jshProduct.list)
          .then((res) => {
            if (!res.success) {
              return this.$message.error(res.message);
            }
            res.result.records.forEach(item => {
              this.jshProductPricesMap[item.id] = item;
            });
          });
    },

    addCustomer() {
      this.$router.push('/business/customer/list');
    },

    handleCopy({ row }) {
      console.log('复制 ', row);
      this.$refs.jshOrderProduct.addRows([{ ...row, id: '' }]);
      this.calculateSumPrice();
    },

    resetAddresses() {
      this.addressDictOptions = [{ text: '请先去客户管理-客户信息页面为该用户添加地址', value: '' }];
    },

    updateAddresses(customerId) {
      if (!customerId) {
        this.resetAddresses();
      }
      getAction(this.url.jshCustomer.listJshCustomerAddressByMainId, { customerId })
          .then((res) => {
            if (res.success) {
              if (res.result.records.length) {
                this.addressDictOptions = res.result.records
                    .map(item => ({ text: item.address, value: item.address }));
              } else {
                this.resetAddresses();
              }
            } else {
              this.$message.error(res.message);
              this.resetAddresses();
            }
          });
    },

    calculateCurrentRowPrice(row) {
      let extendPrice = 0;
      switch (Number(row.extendType)) {
        case 1: {
          // 抽 20元/3抽
          if (Number(row.extendNum) && row.extendNum % 3 !== 0) {
            // 重置抽的数量
            console.log('row.extendNum', row.extendNum);
            // 此种设置方法，value change事件无法捕获行信息
            this.$refs.jshOrderProduct.setValues([{
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
      const price = (this.jshProductPricesMap[row.productId] && this.jshProductPricesMap[row.productId].wholesalePrice || 0);
      const totalArea = (row.num || 0) * (row.width || 0) * (row.height || 0);
      const totalPrice = (price * totalArea) + (extendPrice * 1000000);
      // 此种设置方法，value change事件无法捕获行信息
      this.$refs.jshOrderProduct.setValues([{
        rowKey: row.id,
        values: {
          price: new BigNumber(price / 100).toFixed(2, BigNumber.ROUND_UP),
          extendPrice: new BigNumber(extendPrice / 100).toFixed(2, BigNumber.ROUND_UP),
          totalArea: new BigNumber(totalArea / 1000000).toFixed(3, BigNumber.ROUND_UP),
          totalPrice: new BigNumber(totalPrice / 100000000).toFixed(2, BigNumber.ROUND_UP),
        }
      }]);
    },

    calculateSumPrice(arg) {
      console.log('arg', arg);
      // 更新汇总信息
      let values = this.$refs.jshOrderProduct.getTableData();
      console.log('values', values);
      let totalArea = '0';
      let totalPrice = '0';
      values.forEach((item) => {
        totalArea = new BigNumber(totalArea).plus(item.totalArea || 0).toFixed();
        totalPrice = new BigNumber(totalPrice).plus(item.totalPrice || 0).toFixed();
      });
      console.log('price: ', totalPrice, totalArea)
      this.model.totalArea = totalArea;
      this.model.totalPrice = totalPrice;
    },

    updatePrices({ row, target }) {
      console.log('update prices ', row, target);
      if (row) {
        // 更新当列信息
        this.calculateCurrentRowPrice(row);
      } else {
        // 计算所有行数据
        this.jshOrderProductTable.dataSource.forEach(item => this.calculateCurrentRowPrice(item));
      }
      // 更新订单总价信息
      this.calculateSumPrice();
    },

    /** 当选项被改变时，联动其他组件 */
    async handleValueChange(event) {
      console.log('数据变化 ', event);
      const { col, row, value, oldValue, target } = event;
      console.log('值 ', value, oldValue, typeof value, typeof oldValue);
      if (value === oldValue) {
        console.log('值未发生变化 ', value, oldValue);
        return;
      }
      switch (col.key) {
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
        case 'width':
        case 'num': {
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        case 'extendType': {
          if (!value) {
            // 设置抽拉条数量
            // 此种设置方法，value change事件无法捕获行信息
            target.setValues([{
              rowKey: row.id,
              values: Object.assign(row, { extendNum: 0, extendPrice: 0 })
            }]);
          }
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        case 'extendNum': {
          // 更新价格信息
          this.updatePrices(event);
          break;
        }
        default: {
        }
      }
    },

    batchSetSize(type) {
      let rows = this.$refs.jshOrderProduct.selectedRows;
      console.log('values', rows);
      if (rows.length <= 0) {
        this.$message.error('请选中需要批量设置的行');
        return;
      }
      this.$refs.sizeModalForm.add(type);
      this.$refs.sizeModalForm.disableSubmit = false;
    },

    batchSetSizeModalFormOk(size, batchType) {
      const rowKeys = this.$refs.jshOrderProduct.selectedRowIds;
      // 此种设置方法，value change事件无法捕获行信息
      this.$refs.jshOrderProduct.setValues(rowKeys.map(rowKey => ({
        rowKey: rowKey, // 行的id
        values: {
          // 在这里 values 中的 name 是你 columns 中配置的 key
          [batchType]: size - 0,
        }
      })));
    },

    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key));
      return Promise.all(values);
    },

    addBefore() {
      console.log("\n\n\n form add before");
      this.jshOrderProductTable.dataSource = [];
    },

    add() {
      console.log("\n\n\n form add", this.modelDefault);
      this.edit(this.modelDefault);
    },

    edit(record) {
      this.model = Object.assign({}, record);
      this.visible = true;
      // 加载子表数据
      if (this.model.id) {
        let params = { id: this.model.id, };
        this.model.totalArea = this.model.totalArea ? `${this.model.totalArea / 1000000}` : '0';
        this.model.totalPrice = this.model.totalPrice ? `${this.model.totalPrice / 100}` : '0';
        // 更新地址列表
        this.updateAddresses(this.model.customerId);
        // 格式化不起作用
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
          this.jshOrderProductTable.dataSource = dataSource.map(item => Object.assign(item, {
            direction: `${item.direction}`,
            extendType: `${item.extendType}`,
            price: `${item.price / 100}`,
            totalArea: `${item.totalArea / 1000000}`,
            totalPrice: `${item.totalPrice / 100}`,
          }));
        }).finally(() => {
          this.jshOrderProductTable.loading = false;
        });
      }
    },

    /** ATab 选项卡切换事件 */
    handleChangeTabs(key) {
      getRefPromise(this, key).then(editableTable => {
        editableTable.resetScrollTop();
      });
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
      console.log('main before', this.model, allValues.formValue);
      let main = Object.assign(this.model, allValues.formValue);
      console.log('main', main, allValues);
      const jshOrderProductPageList = allValues.tablesValue[0].tableData;
      if (jshOrderProductPageList.length) {
        jshOrderProductPageList.forEach(item => {
          const jshOrderProductDetailList = [];
          const jshOrderProductExtendList = [];
          const productDetail = {
            referenceWidth: Number(item.width),
            referenceHeight: Number(item.height),
            num: Number(item.num),
            color: item.color,
            direction: item.direction,
            extendType: item.extendType,
            extendNum: item.extendNum,
          };
          switch (Number(item.extendType)) {
            case 1: {
              // 抽 高/3 - 2
              Object.assign(productDetail, {
                referenceHeight: Math.ceil(item.height / 3) - 2,
                num: item.num * 3,
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
                num: item.num * 2,
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
          console.log('detail: ', this.jshProductPricesMap, this.jshProductPricesMap[item.productId]);
          jshOrderProductDetailList.push({
            ...productDetail,
            type: 1,
            width: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].aluminumWidthDiff,
            height: productDetail.referenceHeight + this.jshProductPricesMap[item.productId].aluminumHeightDiff,
            color: item.color,
          });
          jshOrderProductDetailList.push({
            ...productDetail,
            type: 2,
            width: productDetail.referenceWidth + this.jshProductPricesMap[item.productId].glassWidthDiff,
            height: productDetail.referenceHeight + this.jshProductPricesMap[item.productId].glassHeightDiff,
            color: item.glassColor,
          });
          Object.assign(item, {
            id: '',
            jshOrderProductExtendList,
            jshOrderProductDetailList,
            price: new BigNumber(item.price).multipliedBy(100).toFixed(),
            extendPrice: new BigNumber(item.extendPrice).multipliedBy(100).toFixed(),
            totalArea: new BigNumber(item.totalArea).multipliedBy(1000000).toFixed(),
            totalPrice: new BigNumber(item.totalPrice).multipliedBy(100).toFixed(),
          });
        });
      }
      console.log('\n\n\n product list ', jshOrderProductPageList);
      return {
        ...main, // 展开
        jshOrderProductPageList,
        totalArea: new BigNumber(main.totalArea).multipliedBy(1000000).toFixed(),
        totalPrice: new BigNumber(main.totalPrice).multipliedBy(100).toFixed(),
      };
    },

    /** 确定按钮点击事件 */
    handleOk() {
      // 重新计算一次价格
      this.updatePrices({});
      /** 触发表单验证 */
      this.getAllTable().then(tables => {
        console.log('tables ', tables);
        /** 一次性验证主表和所有的次表 */
        return validateFormModelAndTables(this.$refs.form, this.model, tables);
      })
          // .then(allValues => {
          //   console.log('allValues ', allValues);
          //   /** 一次性验证一对一的所有子表 */
          //   return this.validateSubForm(allValues);
          // })
          .then(allValues => {
            if (typeof this.classifyIntoFormData !== 'function') {
              throw this.throwNotFunction('classifyIntoFormData');
            }
            console.log("this.classifyIntoFormData", typeof this.classifyIntoFormData);
            let formData = this.classifyIntoFormData(allValues);
            // 发起请求
            return this.request(formData);
          })
          .catch((e) => {
            if (e.error === VALIDATE_FAILED) {
              // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
              this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index];
            } else {
              console.error(e);
              this.$message.error('信息有误');
            }
          });
    },
    validateError(msg) {
      this.$message.error(msg);
    },
    close() {
      console.log('\n\n\nclear order form');
      this.visible = false;
      this.$emit('close');
      this.$refs.form.clearValidate();
    },
  }
};
</script>

<style scoped>
</style>
