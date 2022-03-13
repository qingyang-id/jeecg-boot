import { app, dialog, Menu } from 'electron';
import config from './index';
import global from './global';

const os = require('os');
const isMac = process.platform === 'darwin';

const menuConfig = [{
  label: app.name,
  submenu: [{
    label: '关于',
    accelerator: isMac ? 'Alt+Cmd+I' : 'Alt+Shift+I',
    click: function () {
      info();
    }
  }]
}, {
  label: '设置',
  submenu: [{
    label: '快速重启',
    accelerator: 'CmdOrCtrl+R',
    role: 'reload'
  }, {
    label: '退出',
    accelerator: 'CmdOrCtrl+Q',
    role: 'quit'
  }]
}, {
  label: '开发者设置',
  submenu: [{
    label: '切换到开发者模式',
    accelerator: 'CmdOrCtrl+I',
    role: 'toggledevtools'
  }]
}];

function info() {
  dialog.showMessageBox({
    title: '关于',
    type: 'info',
    message: 'ERP-Admin',
    detail: `版本信息：\nelectron版本：${process.versions.electron}\n当前系统：${os.type()} ${os.arch()} ${os.release()}\n当前版本：${global.globalConfig.NODE_ENV}，${global.globalConfig.VUE_APP_VERSION}`,
    noLink: true,
    buttons: ['确定']
  });
}

function setMenu(win) {
  let menu;
  if (config.devToolsShow) {
    menu = Menu.buildFromTemplate(menuConfig);
    Menu.setApplicationMenu(menu);
    // win.webContents.openDevTools();
  } else {
    if (isMac) {
      menu = Menu.buildFromTemplate(menuConfig);
      Menu.setApplicationMenu(menu);
    } else {
      Menu.setApplicationMenu(null);
    }
  }
}


export default setMenu;
