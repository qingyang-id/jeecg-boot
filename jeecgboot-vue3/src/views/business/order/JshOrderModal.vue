<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="title"
    width="90%"
    destroyOnClose
    :maskClosable="false"
    :showOkBtn="!disableSubmit"
    @ok="handleOk"
  >
    <JshOrderForm ref="realForm" :disabled="disableSubmit" @ok="submitCallback" />
  </BasicModal>
</template>
<script lang="ts" setup>
  /**
   * 订单新增 / 编辑弹窗
   * 老：modules/JshOrderModal.vue
   */
  import { nextTick, ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import JshOrderForm from './JshOrderForm.vue';

  const emit = defineEmits(['success', 'register']);
  const title = ref('');
  const disableSubmit = ref(false);
  const realForm = ref();

  const [registerModal, { closeModal }] = useModalInner(async (data) => {
    disableSubmit.value = !!data?.disableSubmit;
    title.value = data?.isUpdate ? '编辑' : '新增';
    // destroyOnClose：表单组件在弹窗打开后才挂载，等待其就绪
    for (let i = 0; i < 50 && !realForm.value; i++) {
      await nextTick();
      await new Promise((r) => setTimeout(r, 10));
    }
    if (data?.isUpdate) {
      realForm.value?.edit(data.record);
    } else {
      realForm.value?.add();
    }
  });

  function handleOk() {
    realForm.value?.handleOk();
  }

  function submitCallback() {
    emit('success');
    closeModal();
  }
</script>
