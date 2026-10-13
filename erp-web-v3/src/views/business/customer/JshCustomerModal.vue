<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="title" :width="800" :canFullscreen="true" cancelText="关闭" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './customer.data';
  import { saveOrUpdate } from './customer.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);
  const title = ref('');

  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  });

  /**
   * data: { record?, isUpdate?, showFooter?, title? }
   * showFooter=false 为「详情」只读模式
   */
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    const showFooter = data?.showFooter !== false;
    isUpdate.value = !!data?.isUpdate;
    title.value = data?.title ?? (unref(isUpdate) ? '编辑' : '新增');
    setModalProps({ confirmLoading: false, showOkBtn: showFooter });
    if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record });
    }
    setProps({ disabled: !showFooter });
  });

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await saveOrUpdate(values, unref(isUpdate));
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
