<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="title" :width="800" :canFullscreen="true" cancelText="关闭" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { addressFormSchema } from './customer.data';
  import { saveOrUpdateAddress } from './customer.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);
  const title = ref('操作');

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    schemas: addressFormSchema,
    showActionButtonGroup: false,
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  });

  /** data: { record?, isUpdate?, mainId? }；新增时客户默认带当前选中的主表 id */
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    title.value = unref(isUpdate) ? '编辑' : '新增';
    if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record, customerId: data.record?.customerId != null ? String(data.record.customerId) : undefined });
    } else if (data?.mainId) {
      await setFieldsValue({ customerId: String(data.mainId) });
    }
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await saveOrUpdateAddress(values, unref(isUpdate));
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
