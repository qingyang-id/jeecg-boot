<template>
  <div class="action" v-show="this.platform === 'win32'">
    <a-icon
        type="minus"
        class="action"
        @click="setFrame('minimize')"
    />
    <a-icon
        :type="isMaximized ? 'fullscreen-exit' : 'fullscreen'"
        class="action"
        @click="setFrame('maximize')"
    />
    <a-icon
        type="close"
        class="action"
        @click="setFrame('close')"
    />
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  data() {
    return {
      isMaximized: false,
    };
  },
  computed: {
    ...mapState({
      platform: state => state.app.platform,
    }),
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      console.log('init, ', process.env.IS_ELECTRON);
      if (process.env.IS_ELECTRON) {
        this.$electron.ipcRenderer.on('isMaximized', (event, value) => {
          console.log('isMaximized ', event, value);
          this.isMaximized = value;
        });
        this.$electron.ipcRenderer.on('rememberCloseAppOption', (event, value) => {
          console.log('rememberCloseAppOption ', event, value);
          localStorage.setItem('rememberCloseAppOption', value);
        });
      }
    },
    setFrame(action) {
      switch (action) {
        case "minimize":
          this.$electron.ipcRenderer.send("minimize");
          break;
        case "maximize":
          this.$electron.ipcRenderer.send("maximize");
          break;
        case "close":
          const type = localStorage.getItem('rememberCloseAppOption');
          this.$electron.ipcRenderer.send("close", type);
          break;
      }
    },
  },
};
</script>

<style lang="less" scoped>
.frame-actions {
  display: flex;
  align-items: center;
  margin-right: 10px;
  -webkit-app-region: no-drag;

  &::before {
    content: "";
    display: inline-block;
    width: 1px;
    height: 20px;
    margin-right: 8px;
    background: #ddd;
  }

  .item {
    padding: 0 4px;
    line-height: 54px;
    height: 50px;
    vertical-align: unset;
    font-size: 18px;
    cursor: pointer;
  }
}
</style>
