<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="title" :width="800" :showOkBtn="!isDetail" cancelText="关闭" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './product.data';
  import { saveOrUpdate } from './product.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);
  const isDetail = ref(false);

  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !!data?.isDetail;
    setProps({ disabled: isDetail.value });
    if (data?.record) {
      const record = { ...data.record };
      // 库里存分，表单显示元
      if (record.purchasePrice) record.purchasePrice = record.purchasePrice / 100;
      if (record.wholesalePrice) record.wholesalePrice = record.wholesalePrice / 100;
      await setFieldsValue(record);
    }
  });

  const title = computed(() => (isDetail.value ? '详情' : isUpdate.value ? '编辑' : '新增'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      // 元 -> 分（四舍五入避免浮点误差，如 19.99*100）
      const params = {
        ...values,
        purchasePrice: Math.round(values.purchasePrice * 100),
        wholesalePrice: Math.round(values.wholesalePrice * 100),
      };
      // defHttp 默认弹出后端 message（与老页面 $message.success(res.message) 一致）
      await saveOrUpdate(params, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
