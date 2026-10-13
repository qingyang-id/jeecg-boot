<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="title" :width="800" cancelText="关闭" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  /**
   * 订单商品扩展项弹窗
   * 老：modules/JshOrderProductExtendModal.vue
   * 保存时 orderProductId 固定取 mainId（与老页面一致）
   */
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { orderProductExtendFormSchema } from './order.data';
  import { orderProductExtendSaveOrUpdate } from './order.api';

  const props = defineProps({
    mainId: { type: String, default: '' },
  });
  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);
  const title = ref('操作');

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    schemas: orderProductExtendFormSchema,
    showActionButtonGroup: false,
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    if (data?.record) {
      const record = { ...data.record };
      // 字典下拉按字符串匹配；Long id 保持字符串
      if (record.type !== null && record.type !== undefined) record.type = `${record.type}`;
      if (record.orderId !== null && record.orderId !== undefined) record.orderId = `${record.orderId}`;
      if (record.orderProductId !== null && record.orderProductId !== undefined) record.orderProductId = `${record.orderProductId}`;
      await setFieldsValue(record);
    }
  });

  async function handleSubmit() {
    const values = await validate();
    setModalProps({ confirmLoading: true });
    try {
      const params = { ...values, orderProductId: props.mainId };
      await orderProductExtendSaveOrUpdate(params, !!values.id);
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
      closeModal();
    }
  }
</script>
