const path = require('path')
const SpeedMeasurePlugin = require('speed-measure-webpack-plugin')
const CompressionPlugin = require('compression-webpack-plugin')
const { dependencies } = require('../package.json')
// const { HashedModuleIdsPlugin } = require('webpack')

/**
 * List of node_modules to include in webpack bundle
 *
 * Required for specific packages like Vue UI libraries
 * that provide pure *.vue files that need compiling
 * https://simulatedgreg.gitbooks.io/electron-vue/content/en/webpack-configurations.html#white-listing-externals
 */
let whiteListedModules = ['vue', 'ant-design-vue']

console.log(process.env) // remove this after you've confirmed it working

const isProd = process.env.NODE_ENV === 'production' || process.env.BABEL_ENV === 'production' || process.env.BABEL_ENV === 'test'
const isAPP = process.env.IS_ELECTRON

console.log('is prod:', isProd)

// cdn 外部扩展，通过 cdn 引入，不会被webpack打包
// const assetsCDN = {
//   // 模块名称和模块作用域命名（对应window里面挂载的变量名称）
//   externals: {
//     vue: 'Vue',
//     'vue-router': 'VueRouter',
//     vuex: 'Vuex',
//     axios: 'axios',
//     moment: 'moment',
//     'ant-design-vue': 'antd'
//   },
//   // cdn的css链接
//   css: [
//     'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.css'
//   ],
//   // cdn的js链接
//   js: [
//     'https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js', // 必须先引入moment，否则报错“TypeError: Cannot read property 'default' of undefined”
//     'https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/zh-cn.js', // 需同步引入语言包，否则日期选择控件等将默认显示为英文
//     'https://cdn.jsdelivr.net/npm/vue@2.6.11/dist/vue.min.js',
//     'https://cdn.jsdelivr.net/npm/vue-router@3.3.4/dist/vue-router.min.js',
//     'https://cdn.jsdelivr.net/npm/vuex@3.5.1/dist/vuex.min.js',
//     'https://cdn.jsdelivr.net/npm/axios@0.18.1/dist/axios.min.js',
//     'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.js'
//   ]
// }

function resolve(dir) {
  return path.join(__dirname, dir)
}

// vue.config.js
// https://cli.vuejs.org/config/#vue-config-js
module.exports = {
  /*
    Vue-cli3:
    Crashed when using Webpack `import()` #2463
    https://github.com/vuejs/vue-cli/issues/2463
   */
  lintOnSave: undefined,
  runtimeCompiler: true,
  assetsDir: 'public',
  // 打包输出路径
  outputDir: 'dist/web',
  // 打包app时放开该配置
  publicPath: isProd && isAPP ? './' : '/',
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  productionSourceMap: false,
  // 多入口配置
  // pages: {
  //   index: {
  //     // entry for the page
  //     entry: 'src/main.js',
  //     // entry: path.join(__dirname, './src/renderer/main.js'),
  //     // the source template
  //     template: 'public/index.html',
  //     // output as dist/index.html
  //     filename: 'index.html',
  //     // when using title option,
  //     // template title tag needs to be <title><%= htmlWebpackPlugin.options.title %></title>
  //     // title: 'ERP管理系统',
  //     // chunks to include on this page, by default includes
  //     // extracted common chunks and vendor chunks.
  //     // chunks: ['chunk-vendors', 'chunk-common', 'index']
  //   }
  // },
  configureWebpack: config => {
    // Ignore related resources when building with cdn
    // config.externals = isProd ? assetsCDN.externals : {}
    // config.module.noParse = /^(vue|vue-router|vuex|vuex-router-sync|axios|ant-design-vue|moment)$/
    // 生产环境取消 console.log
    // if (isProd) {
    //   config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
    // }
    if (isAPP) {
      config.externals = [...Object.keys(dependencies || {}).filter(d => !whiteListedModules.includes(d))]
    }
  },
  chainWebpack: (config) => {
    // config
    // Interact with entry points
    // .entry('index')
    // .add(resolve('src/main.js'))
    // .end()
    // Modify output settings
    // .output
    // .path('dist')
    // .filename('[name].bundle.js')

    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@config', resolve('src/config'))
      .set('@views', resolve('src/views'))

    // webpack 会默认给 commonChunk 打进 chunk-vendors，所以需要对 webpack 的配置进行 delete
    // config.optimization.delete('splitChunks')
    if (isProd) {
      // 初始化页面的title为配置文件设置的值，public/index.html中的htmlWebpackPlugin.options.title
      // config.plugin('html').tap((args) => {
      //   console.log('args ', args)
      //   if (isProd) {
      //     args[0].cdn = assetsCDN
      //   }
      //   return args
      // })

      // Ignore related resources when building with cdn
      config.optimization.splitChunks({
        // 分割代码块
        cacheGroups: {
          vendor: {
            // 第三方库抽离
            chunks: 'all',
            test: /node_modules/,
            name: 'vendor',
            minChunks: 1, // 在分割之前，这个代码块最小应该被引用的次数
            maxInitialRequests: 5,
            minSize: 0, // 大于 0 个字节
            priority: 100 // 权重
          },
          common: {
            // 公用模块抽离
            chunks: 'all',
            test: /[\\/]src[\\/]js[\\/]/,
            name: 'common',
            minChunks: 2, // 在分割之前，这个代码块最小应该被引用的次数
            maxInitialRequests: 5,
            minSize: 0, // 大于 0 个字节
            priority: 60
          },
          styles: {
            // 样式抽离
            name: 'styles',
            test: /\.(sa|sc|le|c)ss$/,
            chunks: 'all',
            enforce: true
          },
          runtimeChunk: {
            name: 'manifest'
          }
        }
      })

      // 生产环境，开启js\css压缩
      config.plugin('compressionPlugin').use(new CompressionPlugin({
        filename: '[path].gz[query]',
        algorithm: 'gzip',
        test: /\.(js|css|less|json)$/, // 匹配文件名
        threshold: 10240, // 对超过10k的数据压缩
        minRatio: 0.8, // 只有压缩率小于这个值的资源才会被处理
        deleteOriginalAssets: false // 不删除源文件
      }))
    }

    config
      .plugin('speed-measure-webpack-plugin')
      .use(SpeedMeasurePlugin)
      .end()

    // 配置 webpack 识别 markdown 为普通的文件
    config.module
      .rule('markdown')
      .test(/\.md$/)
      .use()
      .loader('file-loader')
      .end()

    // 编译vxe-table包里的es6代码，解决IE11兼容问题
    config.module
      .rule('vxe')
      .test(/\.js$/)
      .include
        .add(resolve('node_modules/vxe-table'))
        .add(resolve('node_modules/vxe-table-plugin-antd'))
        .end()
      .use()
      .loader('babel-loader')
      .end()
  },

  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */
          'primary-color': '#1890FF',
          'link-color': '#1890FF',
          'border-radius-base': '4px'
        },
        javascriptEnabled: true
      }
    }
  },

  devServer: {
    open: !process.argv.includes('electron:serve'),
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
        changeOrigin: true
      }
    }
  },

  // 第三方插件配置
  pluginOptions: {
    // vue-cli-plugin-electron-builder配置
    electronBuilder: {
      // 配置后可以在渲染进程使用ipcRenderer
      nodeIntegration: true,
      // 项目打包参数配置
      builderOptions: {
        // win: {
        //   icon: 'build/electron-icon/icon.ico',
        //   // 图标路径 windows系统中icon需要256*256的ico格式图片，更换应用图标亦在此处
        //   target: [
        //     {
        //       // 打包成一个独立的 exe 安装程序
        //       target: 'nsis',
        //       // 这个意思是打出来32 bit + 64 bit的包，但是要注意：这样打包出来的安装包体积比较大，所以建议直接打32的安装包。
        //       arch: [
        //         // 'x64',
        //         'ia32'
        //       ]
        //     }
        //   ]
        // },
        // dmg: {
        //   contents: [
        //     {
        //       x: 410,
        //       y: 150,
        //       type: 'link',
        //       path: '/Applications'
        //     },
        //     {
        //       x: 130,
        //       y: 150,
        //       type: 'file'
        //     }
        //   ]
        // },
        // linux: {
        //   icon: 'build/electron-icon/icon.png',
        //   target: 'AppImage'
        // },
        mac: {
          icon: 'build/electron-icon/icon.icns'
        },
        productName: 'erp-admin', // 项目名称 打包生成exe的前缀名
        appId: 'com.qing.yang.erp', // 包名
        copyright: 'Copyright © 2023-present QingYang', // 版权
        compression: 'maximum', // store|normal|maximum 打包压缩情况(store速度较快)
        // eslint-disable-next-line
        artifactName: '${productName}-${version}-${platform}-${arch}.${ext}', // 打包后安装包名称
        asar: true, // asar打包
        // files: ['**/*'],
        // files: ['dist/electron/**/*'],
        nsis: {
          // 是否一键安装，建议为 false，可以让用户点击下一步、下一步、下一步的形式安装程序，如果为true，当用户双击构建好的程序，自动安装程序并打开，即：一键安装（one-click installer）
          oneClick: false,
          // 允许请求提升。 如果为false，则用户必须使用提升的权限重新启动安装程序。
          allowElevation: true,
          // 允许修改安装目录，建议为 true，是否允许用户改变安装目录，默认是不允许
          allowToChangeInstallationDirectory: true,
          // 安装图标
          installerIcon: 'build/electron-icon/icon.ico',
          // 卸载图标
          uninstallerIcon: 'build/electron-icon/icon.ico',
          // 安装时头部图标
          installerHeaderIcon: 'build/electron-icon/icon.ico',
          // 创建桌面图标
          createDesktopShortcut: true,
          // 创建开始菜单图标
          createStartMenuShortcut: true
          // 桌面快捷键图标名称
          // shortcutName: 'erp-admin'
        }
      },
      chainWebpackMainProcess: (config) => {
        config.plugin('define').tap((args) => {
          args[0]['IS_ELECTRON'] = true
          return args
        })
      },
      chainWebpackRendererProcess: (config) => {
        config.plugin('define').tap((args) => {
          args[0]['IS_ELECTRON'] = true
          return args
        })
      },
      outputDir: 'dist/electron',
      mainProcessFile: 'electron/main/index.dev.js',
      mainProcessWatch: ['electron/main']
    }
  }
}
