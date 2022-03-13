/**
 * 自动更新
 * https://www.electron.build/auto-update
 */

const { autoUpdater } = require('electron-updater');
const { ipcMain, app } = require('electron');
const log = require('electron-log');
autoUpdater.logger = require("electron-log");
autoUpdater.logger.transports.file.level = "info";
const path = require('path');
const AdmZip = require('adm-zip');
const fs = require('fs');
const electronMainUtils = require('../util/utils');
const { isDevelopment, isWindows, isMac } = require('../util/platform');

let localResourcePath = ''; // 本地resource/app路径
let resourcePath = ''; // 本地resource 路径
let appZipPath = ''; // app压缩包位置
let yourFileServer = process.env.VUE_APP_API_BASE_URL.match(new RegExp(/(\w+):\/\/([^/:]+)(:\d*)?/))[0]; // 文件服务器
const remoteAppURL = `https://${yourFileServer}/electron/app.zip`; // yourFileServer: 你的远程文件服务器
console.log('remote app url: ', remoteAppURL)
// windows 本地测试 admin:改为你的用户名
if (isDevelopment) {
  console.log('is develop ', isDevelopment)
  console.log('dirname ', __dirname)
  // erp-admin: app名称，一般取自package.json
  if (isWindows) {
    // win 本地安装包路径
    // localResourcePath = `C:/Users/admin/AppData/Local/Programs/${process.env.VUE_APP_NANE}/resources/app`;
    // resourcePath = `C:/Users/admin/AppData/Local/Programs/${process.env.VUE_APP_NANE}/resources/app`;
    // appZipPath = `C:/Users/admin/AppData/Local/Programs/${process.env.VUE_APP_NANE}/resources/app.zip`;
    localResourcePath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app`);
    resourcePath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app`);
    appZipPath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app.zip`);
  } else if (isMac) {
    localResourcePath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app`);
    resourcePath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app`);
    appZipPath = path.join(__dirname, `./${process.env.VUE_APP_NANE}.app/Contents/Resources/app.zip`);
  }
} else if (isWindows) { // win平台
  localResourcePath = `./resources/app`;
  resourcePath = `./resources/app`;
  appZipPath = `./resources/app.zip`;
} else if (isMac) { // mac平台
  localResourcePath = `/Applications/${process.env.VUE_APP_NANE}.app/Contents/Resources/app`;
  resourcePath = `/Applications/${process.env.VUE_APP_NANE}.app/Contents/Resources/app`;
  appZipPath = `/Applications/${process.env.VUE_APP_NANE}.app/Contents/Resources/app.zip`;
}

console.log('local resource path ', localResourcePath);
console.log('resource path ', resourcePath);
console.log('app zip path ', appZipPath);

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

  /**
   * 增量更新
   */
  ipcMain.on('hot-update', async (e, msg = {}) => {
    try {
      console.log('download msg ', msg)
      if (fs.existsSync(`${localResourcePath}.bak`)) { // 删除旧备份
        electronMainUtils.deleteDirSync(`${localResourcePath}.bak`);
      }
      if (fs.existsSync(localResourcePath)) {
        fs.renameSync(localResourcePath, `${localResourcePath}.bak`); // 备份目录
      }
      await electronMainUtils.downloadFile(msg.downloadUrl || remoteAppURL, appZipPath, (evt) => {
        console.log("progressEvent===", evt);
        const percent = parseInt((evt.loaded / evt.total) * 100)
        // 通知渲染进程，进行指定操作 更新进度条
        mainWindow.webContents.send('downloadProgress', percent);
        // mac 程序坞、windows 任务栏显示进度
        mainWindow.setProgressBar(percent);
      });
      if (!fs.existsSync(`${localResourcePath}`)) {
        fs.mkdirSync(localResourcePath); // 创建app来解压用
      }
      // 通知渲染进程，进行指定操作 更新进度条
      mainWindow.webContents.send('installProgress', 1);
      try {
        // 同步解压缩
        const unzip = new AdmZip(appZipPath);
        unzip.extractAllTo(resourcePath, true);
        mainWindow.webContents.send('installProgress', 100);
        console.log('app.zip 解压缩完成');
        // 此处可以: 通知渲染进程，进行指定操作
        mainWindow.webContents.send('quitAndUpdate');
      } catch (error) {
        console.error(`extractAllToERROR: ${error}`);
        electronMainUtils.sendUpdateMessage(`ERROR: 检查更新出错:${error}`, mainWindow);
      }
      console.log('webContents reload完成');
    } catch (error) {
      console.error(`checkForPartUpdatesERROR`, error);
      electronMainUtils.sendUpdateMessage(`ERROR: 检查更新出错:${error}`, mainWindow);
    } finally {
      if (fs.existsSync(`${localResourcePath}.bak`)) {
        fs.renameSync(`${localResourcePath}.bak`, localResourcePath);
      }
    }
  });

  ipcMain.on("restart", (e, msg) => {
      app.relaunch(); // 重启
      app.exit(0);
  })
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
