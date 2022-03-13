<template>
  <a-modal
      :title="title"
      :width="modalWidth"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      okText="立即重启"
      :ok-button-props="{ props: { disabled: restart } }"
  >
    <a-spin :spinning="confirmLoading">
      <a-row>
        <a-col :span="6">
          下载进度：
        </a-col>
        <a-col :span="18">
          <a-progress :percent="downloadProgress"/>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="6">
          安装进度：
        </a-col>
        <a-col :span="18">
          <a-progress :percent="installProgress"/>
        </a-col>
      </a-row>
    </a-spin>
  </a-modal>
</template>

<script>
import { getAction } from '@/api/manage';

export default {
  name: "Version",
  data() {
    return {
      title: "版本升级",
      version: '1.0.0',
      downloadProgress: 0,
      installProgress: 0,
      modalWidth: 600,
      visible: false,
      confirmLoading: false,
      restart: true,
      updateInstallProgressTimer: undefined,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      url: "/business/version/new",
    };
  },
  created() {
    if (process.env.IS_ELECTRON) {
      this.version = process.env.VUE_APP_VERSION;
      this.$electron.ipcRenderer.on('message', (event, value) => {
        console.log('message ', event, value);
        this.$message.error(value);
      });
      this.$electron.ipcRenderer.on('downloadProgress', (event, value) => {
        console.log('downloadProgress ', event, value);
        this.downloadProgress = value;
      });
      this.$electron.ipcRenderer.on('installProgress', (event, value) => {
        console.log('installProgress ', event, value, typeof value);
        this.installProgress = value;
        this.updateInstallProgress();
      });
      this.$electron.ipcRenderer.on('quitAndUpdate', (event, value) => {
        console.log('quitAndUpdate ', event, value);
        // 修改按钮
        this.restart = false;
      });
    }
  },
  methods: {
    updateInstallProgress() {
      if (this.updateInstallProgressTimer) clearTimeout(this.updateInstallProgressTimer);
      this.updateInstallProgressTimer = setTimeout(() => {
        if (this.installProgress + 1 < 100) {
          this.installProgress += 1;
        }
        this.updateInstallProgress();
      }, 1000);
    },
    show() {
      this.downloadProgress = 0;
      this.installProgress = 0;
      console.log('this.version ', this.version, process.platform);
      const version = this.version || '1.0.0';
      let versionNo = '1';
      version.split('.').forEach(item => {
        versionNo = `${versionNo}${item.padStart(3, '0')}`;
      });
      getAction(this.url, { versionNo, platform: process.platform }).then((res) => {
        if (!res.success) {
          this.$message.error(res.message);
        } else if (res.result.versionNo !== versionNo) {
          // 有新版本
          const that = this;
          this.$confirm({
            title: `新版本${res.result.version}`,
            content: h => (res.result.description.split('\n').map(item => h('p', item))),
            onOk() {
              // 热更新
              console.log('version result ', res.result)
              that.$electron.ipcRenderer.send("hot-update", res.result);
              that.visible = true;
            },
            onCancel() {
              that.close();
            },
          });
        } else {
          this.$message.info("当前已为最新版本!");
          this.close();
        }
      });
    },
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit('close');
      this.visible = false;
      this.disableSubmit = false;
    },
    handleOk() {
      // 重启
      this.$electron.ipcRenderer.send("restart");
      this.close();
    }
  }
};
</script>

<style scoped>

</style>

