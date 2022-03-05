const { defineConfig } = require('@vue/cli-service');
const path = require('path');

const isProd = process.env.NODE_ENV === 'production' || process.env.BABEL_ENV === 'production';
const isWeb = !process.argv.find(item => item.indexOf('electron') >= 0);

const appSuffix = isProd ? '' : '-test';

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = defineConfig({
  transpileDependencies: false,
  // 打包输出路径
  outputDir: 'dist/web',
  // 多入口配置
  pages: {
    index: {
      // entry for the page
      entry: 'src/renderer/main.js',
      // entry: path.join(__dirname, './src/renderer/main.js'),
      // the source template
      template: 'public/index.html',
      // output as dist/index.html
      filename: 'index.html',
      // when using title option,
      // template title tag needs to be <title><%= htmlWebpackPlugin.options.title %></title>
      title: process.env.VUE_APP_PLATFORM_NAME
      // chunks to include on this page, by default includes
      // extracted common chunks and vendor chunks.
      // chunks: ['chunk-vendors', 'chunk-common', 'index']
    }
  },

  chainWebpack: (config) => {
    if (isWeb) {
      // 修改参数 解决无法打包web项目问题
      config.plugin('define').tap((args) => {
        // eslint-disable-next-line no-param-reassign
        args[0]['process.env']['IS_ELECTRON'] = false;
        return args;
      });
    }
    // 设置别名
    config.resolve.alias
      .set('@', resolve('src/renderer'))
      .set('@api', resolve('src/renderer/api'))
      .set('@assets', resolve('src/renderer/assets'))
      .set('@comp', resolve('src/renderer/components'))
      .set('@config', resolve('src/renderer/config'))
      .set('@views', resolve('src/renderer/views'));
  },

  css: {
    // 开启 CSS source maps?
    sourceMap: !isProd,
    // css预设器配置项
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */
          'primary-color': '#1890FF',
          'link-color': '#1890FF',
          'border-radius-base': '4px',
        },
        javascriptEnabled: true,
      },
    },
  },

  devServer: {
    open: isWeb,
    port: 3000,
    proxy: {
      /* '/api': {
         target: 'https://mock.ihx.me/mock/5baf3052f7da7e07e04a5116/antd-pro', //mock API接口系统
         ws: false,
         changeOrigin: true,
         pathRewrite: {
           '/jeecg-boot': ''  //默认所有请求都加了jeecg-boot前缀，需要去掉
         }
       }, */
      '/portal': {
        target: 'http://localhost:8080', // 请求本地 需要jeecg-boot后台项目
        ws: false,
        changeOrigin: true,
      },
    },
  },

  // 第三方插件配置
  pluginOptions: {
    // vue-cli-plugin-electron-builder配置
    electronBuilder: {
      // 配置后可以在渲染进程使用ipcRenderer
      nodeIntegration: true,
      // 项目打包参数配置
      builderOptions: {
        win: {
          icon: 'build/icons/icon.ico',
          // 图标路径 windows系统中icon需要256*256的ico格式图片，更换应用图标亦在此处
          target: [
            {
              // 打包成一个独立的 exe 安装程序
              target: 'portable',
              // 这个意思是打出来32 bit + 64 bit的包，但是要注意：这样打包出来的安装包体积比较大，所以建议直接打32的安装包。
              arch: [
                'x64',
                // 'ia32'
              ]
            },
            {
              target: 'nsis',
              arch: ['x64'],
            },
          ]
        },
        dmg: {
          contents: [
            {
              x: 410,
              y: 150,
              type: 'link',
              path: '/Applications'
            },
            {
              x: 130,
              y: 150,
              type: 'file'
            }
          ]
        },
        mac: {
          icon: 'build/icons/mac/icon.icns',
        },
        productName: `erp-admin${appSuffix}`, // 项目名称 打包生成exe的前缀名
        appId: `com.qing.yang.erp${appSuffix}`, // 包名
        copyright: 'Copyright © 2023 QingYang', // 版权
        compression: 'maximum', // store|normal|maximum 打包压缩情况(store速度较快)
        // eslint-disable-next-line
        artifactName: '${productName}-${version}-${platform}-${arch}.${ext}', // 打包后安装包名称
        asar: true, // asar打包
        files: ['**/*'],
        // files: ['dist/electron/**/*'],
        nsis: {
          // eslint-disable-next-line max-len
          // 是否一键安装，建议为 false，可以让用户点击下一步、下一步、下一步的形式安装程序，如果为true，当用户双击构建好的程序，自动安装程序并打开，即：一键安装（one-click installer）
          oneClick: false,
          // 允许请求提升。 如果为false，则用户必须使用提升的权限重新启动安装程序。
          allowElevation: true,
          // 允许修改安装目录，建议为 true，是否允许用户改变安装目录，默认是不允许
          allowToChangeInstallationDirectory: true,
          // 安装图标
          installerIcon: 'build/icons/icon.ico',
          // 卸载图标
          uninstallerIcon: 'build/icons/icon.ico',
          // 安装时头部图标
          installerHeaderIcon: 'build/icons/icon.ico',
          // 创建桌面图标
          createDesktopShortcut: true,
          // 创建开始菜单图标
          createStartMenuShortcut: true,
          // 桌面快捷键图标名称
          shortcutName: `erp-admin${appSuffix}`
        },
      },
      chainWebpackMainProcess: (config) => {
        config.plugin('define').tap((args) => {
          // eslint-disable-next-line no-param-reassign
          args[0].IS_ELECTRON = true;
          return args;
        });
      },
      chainWebpackRendererProcess: (config) => {
        config.plugin('define').tap((args) => {
          // eslint-disable-next-line no-param-reassign
          args[0].IS_ELECTRON = true;
          return args;
        });
      },
      outputDir: 'dist/electron',
      mainProcessFile: isProd ? 'src/main/index.js' : 'src/main/index.dev.js', // 主进程入口文件
      // rendererProcessFile: 'src/renderer/main.js', // 渲染进程入口文件
      mainProcessWatch: ['src/main'], // 检测主进程文件在更改时将重新编译主进程并重新启动
    },
  },
});
