<template>
  <a-modal
      :title="title"
      width="90%"
      :visible="visible"
      :destroyOnClose="true"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel">
    <jsh-order-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </a-modal>
</template>

<script>

import JshOrderForm from './JshOrderForm';

export default {
  name: 'JshOrderModal',
  components: {
    JshOrderForm
  },
  data() {
    return {
      title: '',
      width: 800,
      visible: false,
      disableSubmit: false,
    };
  },
  methods: {
    add() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.realForm.add();
      });
    },
    edit(record) {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.realForm.edit(record);
      });
    },
    close() {
      this.$emit('close');
      this.visible = false;
    },
    handleOk() {
      this.$refs.realForm.handleOk();
    },
    submitCallback() {
      this.$emit('ok');
      this.visible = false;
    },
    handleCancel() {
      this.close();
    }
  }
};
</script>

<style scoped>
</style>
