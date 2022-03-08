'use strict';

import { app, protocol, BrowserWindow, nativeTheme, ipcMain } from 'electron';
import { createProtocol } from 'vue-cli-plugin-electron-builder/lib';
import installExtension, { VUEJS_DEVTOOLS } from 'electron-devtools-installer';
import {
  isWindows,
  isMac,
  isLinux,
  isDevelopment,
  isCreateTray,
  isCreateMpris,
} from './util/platform';
import { initIpcMain, exitAsk } from './ipcMain.js';
import AppAutoUpdate from './module/appAutoUpdate.js';
// const AppTray = require('./module/appTray')

const isDebug = process.env.IS_DEBUG;
let mainWindow;
let tray;

// Scheme must be registered before the app is ready
protocol.registerSchemesAsPrivileged([
  { scheme: 'app', privileges: { secure: true, standard: true } }
]);

function handleKeepSingleApp() {
  // 限制只可以打开一个应用
  if (!app.requestSingleInstanceLock()) return app.quit();
  app.on('second-instance', (e, cl, wd) => {
    if (mainWindow) {
      console.log('show...');
      if (mainWindow.isMinimized()) {
        console.log('restore...');
        mainWindow.restore();
      }
      console.log('focus...');
      mainWindow.focus();
      mainWindow.show();
    }
  });
}

async function createWindow() {
  // Create the browser window.
  global.mainWindow = mainWindow = new BrowserWindow({
    width: 1366,
    height: 768,
    minWidth: 960,
    minHeight: 540,
    title: isWindows ? "ERP Admin" : "",
    // icon: previewIcon,
    titleBarStyle: "hidden",
    frame: !isWindows,
    show: true,
    hasShadow: isMac,
    webPreferences: {
      // Use pluginOptions.nodeIntegration, leave this alone
      // See nklayman.github.io/vue-cli-plugin-electron-builder/guide/security.html#node-integration for more info
      webSecurity: false,
      nodeIntegration: true,
      enableRemoteModule: true,
      contextIsolation: false,
    },
    backgroundColor: nativeTheme.shouldUseDarkColors ? '#222' : '#fff',
  });

  // tray = new AppTray(this.mainWindow)

  // Make sure the app is singleton.
  handleKeepSingleApp();

  // hide menu bar on Microsoft Windows and Linux
  // mainWindow.setMenuBarVisibility(false);

  mainWindow.on("close", (event) => {
    if (isMac) {
      exitAsk(event, mainWindow);
    }
  });

  mainWindow.on("closed", () => {
    global.mainWindow = mainWindow = null;
  });

  // init ipcMain
  initIpcMain(mainWindow);

  // init auto update
  AppAutoUpdate.updateHandle(mainWindow);

  if (process.env.WEBPACK_DEV_SERVER_URL) {
    // Load the url of the dev server if in development mode
    await mainWindow.loadURL(process.env.WEBPACK_DEV_SERVER_URL);
    if (!process.env.IS_TEST || isDebug) mainWindow.webContents.openDevTools();
  } else {
    createProtocol('app');
    // 主进程加载时的loading过渡，避免白屏
    await mainWindow.loadURL('file://./loading.html');
    // Load the index.html when not in development
    await mainWindow.loadURL('app://./index.html');
    if (isDebug) mainWindow.webContents.openDevTools();
  }

  // mainWindow.once("ready-to-show", () => {
  //   // 设置任务栏操作和缩略图
  //   if (isWindows) {
  //     mainWindow.setThumbarButtons([
  //       {
  //         tooltip: "刷新",
  //         icon: path.join(__dirname, "./img/icons/exit.png"),
  //         click() {
  //           mainWindow.reload();
  //         },
  //       }
  //     ]);
  //     mainWindow.setThumbnailClip({ x: 0, y: 0, width: 180, height: 50 });
  //   }
  // });
}

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', async () => {
  if (isDevelopment && !process.env.IS_TEST || isDebug) {
    // Install Vue Devtools
    try {
      await installExtension(VUEJS_DEVTOOLS);
    } catch (e) {
      console.error('Vue Devtools failed to install:', e.toString());
    }
  }
  await createWindow();
});

// Exit cleanly on request from parent process in development mode.
if (isDevelopment) {
  if (process.platform === 'win32') {
    process.on('message', (data) => {
      if (data === 'graceful-exit') {
        app.quit();
      }
    });
  } else {
    process.on('SIGTERM', () => {
      app.quit();
    });
  }
}
