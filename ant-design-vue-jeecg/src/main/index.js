'use strict';

import { app, protocol, BrowserWindow, nativeTheme, ipcMain } from 'electron';
import { createProtocol } from 'vue-cli-plugin-electron-builder/lib';
import installExtension, { VUEJS_DEVTOOLS } from 'electron-devtools-installer';
import {
  isWindows,
  isMac,
  isDevelopment,
} from './util/platform';
import { initIpcMain, exitAsk } from './services/ipcMain.js';
import AppAutoUpdate from './services/appAutoUpdate.js';
import singleInstanceApp from './services/singleInstanceApp';
import Tray from './services/tray';
import Menu from './config/menu';
import config from './config/index';
import global from './config/global';
import createWindow from './services/createWindow';

const isDebug = process.env.IS_DEBUG;
let win = null;
let loaderWin = null;

// Scheme must be registered before the app is ready
protocol.registerSchemesAsPrivileged([
  { scheme: 'app', privileges: { secure: true, standard: true } }
]);

if (config.winSingle) {
  // Make sure the app is singleton.
  singleInstanceApp();
}

async function initWindow() {
  if (config.loading) {
    loaderWin = createWindow(
      {
        width: 1366,
        height: 768,
        minWidth: 960,
        minHeight:
          !isMac && process.env.VUE_APP_ENV === 'production' ? 540 - 20 : 540,
        title: isWindows ? "ERP Admin" : "",
        // icon: previewIcon,
        titleBarStyle: "hidden",
        frame: !isWindows,
        // useContentSize: true,
        show: false,
        hasShadow: isMac,
        webPreferences: {
          // Use pluginOptions.nodeIntegration, leave this alone
          // See nklayman.github.io/vue-cli-plugin-electron-builder/guide/security.html#node-integration for more info
          webSecurity: false,
          nodeIntegration: true,
          enableRemoteModule: true,
          contextIsolation: false,
          scrollBounce: isMac,
        },
        backgroundColor: nativeTheme.shouldUseDarkColors ? '#222' : '#fff',
      },
      'loading.html',
      'loading.html'
    );
    loaderWin.once('ready-to-show', () => {
      loaderWin.show();
    });
    loaderWin.on('closed', () => {
      loaderWin = null;
    });
  }
  // Create the browser window.
  global.sharedObject.win = win = createWindow({
    width: 1366,
    height: 768,
    minWidth: 960,
    minHeight:
      !isMac && process.env.VUE_APP_ENV === 'production' ? 540 - 20 : 540,
    title: isWindows ? "ERP Admin" : "",
    // icon: previewIcon,
    titleBarStyle: "hidden",
    frame: !isWindows,
    // useContentSize: true,
    show: false,
    hasShadow: isMac,
    webPreferences: {
      // Use pluginOptions.nodeIntegration, leave this alone
      // See nklayman.github.io/vue-cli-plugin-electron-builder/guide/security.html#node-integration for more info
      webSecurity: false,
      nodeIntegration: true,
      enableRemoteModule: true,
      contextIsolation: false,
      scrollBounce: isMac,
    },
    backgroundColor: nativeTheme.shouldUseDarkColors ? '#222' : '#fff',
  }, '', 'index.html');


  // hide menu bar on Microsoft Windows and Linux
  // win.setMenuBarVisibility(false);

  // init ipcMain
  initIpcMain(win);
  // init menu
  Menu(win);
  // init tray
  Tray.init(win);
  // init auto update
  AppAutoUpdate.updateHandle(win);

  win.on('enter-full-screen', () => {
    isMac && app.commandLine.appendSwitch('disable-pinch', true);
  });
  win.on('leave-full-screen', () => {
    isMac && app.commandLine.appendSwitch('disable-pinch', false);
  });


  win.once('ready-to-show', () => {
    loaderWin && loaderWin.destroy();
    win.show();
  });

  win.on("close", (event) => {
    if (isMac) {
      exitAsk(event, win);
    }
  });

  win.on("closed", () => {
    global.sharedObject.win = win = null;
  });
}

async function onAppReady() {
  if (isDevelopment && !process.env.IS_TEST || isDebug) {
    // Install Vue Devtools
    try {
      await installExtension(VUEJS_DEVTOOLS);
    } catch (e) {
      console.error('Vue Devtools failed to install:', e.toString());
    }
  }
  if (!process.env.WEBPACK_DEV_SERVER_URL) {
    // createProtocol('app', path.join(resourcesPath, './app.asar.unpacked'))
    createProtocol('app');
  }
  await initWindow();
}

app.setAppUserModelId(config.VUE_APP_APPID);
// app.isReady() ? onAppReady() : app.on('ready', onAppReady);

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', async () => {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (BrowserWindow.getAllWindows().length === 0) {
    await initWindow();
  } else if (win) {
    win.show();
  }
});

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', onAppReady);

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
