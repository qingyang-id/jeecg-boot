/**
 * 自动更新
 * https://www.electron.build/auto-update
 */

const { autoUpdater } = require('electron-updater');
const { ipcMain } = require('electron');
const log = require('electron-log');
autoUpdater.logger = require("electron-log");
autoUpdater.logger.transports.file.level = "info";
const path = require('path');
const electronMainUtils = require('../util/utils');
const { isDevelopment, isWindows, isMac } = require('../util/platform');

/**
 * autoUpdater - 更新操作
 * @param {object} mainWindow 实例
 */
function updateHandle(mainWindow) {
  log.info('enter updateHandle');
  log.info(`mainWindow： ${mainWindow}`);
  autoUpdater.on('error', function (error) {
    electronMainUtils.sendUpdateMessage(`ERROR: 检查更新出错:${error}`, mainWindow);
  });
  autoUpdater.on('checking-for-update', function () {
    log.info(`enter checking-for-update`);
    log.info(`正在检查更新…`);
  });
  autoUpdater.on('update-available', function (UpdateInfo) {
    log.info('UpdateInfo', UpdateInfo);
    electronMainUtils.sendUpdateMessage('检测到新版本，正在下载…', mainWindow);
  });
  autoUpdater.on('update-not-available', function (info) {
    log.info(`现在使用的已经是最新版本`);
    electronMainUtils.sendUpdateMessage('现在使用的已经是最新版本', mainWindow);
  });

  /**
   * 更新下载进度事件
   */
  autoUpdater.on('download-progress', function (progressObj) {
    mainWindow.webContents.send('downloadProgress', progressObj);
  });

  /**
   * 下载更新包完成
   */
  autoUpdater.on('update-downloaded', function (event, releaseNotes, releaseName, releaseDate, updateUrl, quitAndUpdate) {
    // 渲染层回复立即更新，则自动退出当前程序，然后进行程序更新
    ipcMain.on('updateNow', (e, arg) => {
      log.info('开始更新');
      autoUpdater.quitAndInstall();
    });
    // 询问渲染层是否立即更新
    mainWindow.webContents.send('isUpdateNow');
  });
}

if (isWindows) {
  autoUpdater.updateConfigPath = path.join(__dirname, 'app-win-update.yml'); // 远程包配置-win
} else if (isMac) {
  autoUpdater.updateConfigPath = path.join(__dirname, 'app-mac-update.yml'); // 远程包配置-mac
}

/**
 * 检查更新
 */
function checkForUpdates() {
  console.log('enter checkForUpdates');
  if (isDevelopment) {
    autoUpdater.checkForUpdates();
  } else {
    autoUpdater.checkForUpdatesAndNotify();
  }
}

// 通知更新检查(远程版本号大于本地即可拉去更新)
ipcMain.on('checkForUpdates', (e, msg) => {
  console.log('checkForUpdatesMsg', msg);
  checkForUpdates();
});

module.exports = {
  updateHandle,
};
