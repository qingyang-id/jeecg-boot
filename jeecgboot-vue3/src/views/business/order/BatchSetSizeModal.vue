<template>
  <a-modal
    :title="title + '-批量设置'"
    :width="500"
    :open="visible"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    :style="{ top: '35%' }"
    destroyOnClose
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form ref="formRef" :model="model" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-form-item :label="title" name="number" :rules="[{ required: true, message: '请输入尺寸!' }]">
        <a-input v-model:value.trim="model.number" :placeholder="'请输入' + title" @pressEnter="handleOk" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
  /**
   * 批量设置 高 / 宽 / 数量 / 抽拉条数量
   * 老：modules/BatchSetSizeModal.vue —— emit('ok', number, batchType)
   */
  import { reactive, ref } from 'vue';

  const emit = defineEmits(['ok', 'close']);
  const visible = ref(false);
  const title = ref('批量设置');
  const batchType = ref('');
  const model = reactive<{ number: string }>({ number: '' });
  const formRef = ref();
  const labelCol = { xs: { span: 24 }, sm: { span: 5 } };
  const wrapperCol = { xs: { span: 24 }, sm: { span: 16 } };

  const TITLES = { height: '高', width: '宽', num: '数量', extendNum: '抽/拉/条数量' };

  function add(type: string) {
    batchType.value = type;
    if (TITLES[type]) {
      title.value = TITLES[type];
    }
    model.number = '';
    visible.value = true;
  }

  async function handleOk() {
    try {
      await formRef.value?.validate();
    } catch (e) {
      return;
    }
    emit('ok', model.number, batchType.value);
    visible.value = false;
  }

  function handleCancel() {
    emit('close');
    visible.value = false;
  }

  defineExpose({ add });
</script>
