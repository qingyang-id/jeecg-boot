<template>
  <a-modal
    :title="title"
    :width="width"
    :open="visible"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    :style="{ top: '5%' }"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <iframe :src="billPrintUrl" width="100%" :height="height" frameborder="0" scrolling="no"></iframe>
  </a-modal>
</template>
<script lang="ts" setup>
  /**
   * 三联打印预览（iframe）
   * 老：dialog/OrderPrintIframe.vue —— show(record, billPrintUrl, billPrintHeight)
   * 老页面里该入口（OrderDetail.handlePrint）已被注释，这里保持功能等价，供后续启用
   */
  import { ref } from 'vue';

  const emit = defineEmits(['close']);
  const title = ref('三联打印预览');
  const width = '1550px';
  const visible = ref(false);
  const billPrintUrl = ref('');
  const height = ref<string | number>('');
  const model = ref<Recordable>({});

  function show(record: Recordable, url: string, billPrintHeight: string | number) {
    height.value = billPrintHeight;
    billPrintUrl.value = url;
    visible.value = true;
    model.value = Object.assign({}, record);
  }

  function handleCancel() {
    emit('close');
    visible.value = false;
  }

  defineExpose({ show, title });
</script>
