/**
 * 自动更新
 * https://www.electron.build/auto-update
 */

const { ipcMain, app } = require('electron');
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
console.log('remote app url: ', remoteAppURL);
// windows 本地测试 admin:改为你的用户名
if (isDevelopment) {
  console.log('is develop ', isDevelopment);
  console.log('dirname ', __dirname);
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
  /**
   * 增量更新
   */
  ipcMain.on('hot-update', async (e, msg = {}) => {
    try {
      console.log('download msg ', msg);
      electronMainUtils.sendUpdateMessage(`删除备份:${localResourcePath}.bak`, mainWindow);
      if (fs.existsSync(`${localResourcePath}.bak`)) { // 删除旧备份
        electronMainUtils.deleteDirSync(`${localResourcePath}.bak`);
      }
      electronMainUtils.sendUpdateMessage(`备份:${localResourcePath}.bak`, mainWindow);
      if (fs.existsSync(localResourcePath)) {
        fs.renameSync(localResourcePath, `${localResourcePath}.bak`); // 备份目录
      }
      electronMainUtils.sendUpdateMessage(`下载:${appZipPath}`, mainWindow);
      await electronMainUtils.downloadFile(msg.downloadUrl || remoteAppURL, appZipPath, (evt) => {
        console.log("progressEvent===", evt);
        const percent = parseInt((evt.loaded / evt.total) * 100);
        // 通知渲染进程，进行指定操作 更新进度条
        mainWindow.webContents.send('downloadProgress', percent);
        // mac 程序坞、windows 任务栏显示进度
        mainWindow.setProgressBar(percent);
      });
      electronMainUtils.sendUpdateMessage(`新建目录:${localResourcePath}`, mainWindow);
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
        electronMainUtils.sendUpdateMessage(`ERROR: 下载出错:${error}`, mainWindow);
      }
      console.log('webContents reload完成');
    } catch (error) {
      console.error(`checkForPartUpdatesERROR`, error);
      electronMainUtils.sendUpdateMessage(`ERROR: 更新出错:${error}`, mainWindow);
    } finally {
      if (fs.existsSync(`${localResourcePath}.bak`)) {
        fs.renameSync(`${localResourcePath}.bak`, localResourcePath);
      }
    }
  });

  ipcMain.on("restart", (e, msg) => {
    app.relaunch(); // 重启
    app.exit(0);
  });
}

module.exports = {
  updateHandle,
};
