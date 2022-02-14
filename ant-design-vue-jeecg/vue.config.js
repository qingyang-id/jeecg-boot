const path = require('path')
const CompressionPlugin = require('compression-webpack-plugin')

const isProd = process.env.NODE_ENV === 'production'
const needCdn = isProd

// cdn 外部扩展，通过 cdn 引入，不会被webpack打包
const assetsCDN = {
  // 模块名称和模块作用域命名（对应window里面挂载的变量名称）
  externals: {
    moment: 'moment',
    'ant-design-vue': 'antd',
    'bignumber.js': 'bignumber.js'
  },
  // cdn的css链接
  css: [
    'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.css'
  ],
  // cdn的js链接
  js: [
    'https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js', // 必须先引入moment，否则报错“TypeError: Cannot read property 'default' of undefined”
    'https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/zh-cn.js', // 需同步引入语言包，否则日期选择控件等将默认显示为英文
    'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.js',
    'https://cdn.jsdelivr.net/npm/bignumber.js@9.0.2/bignumber.min.js'
  ]
}

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
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  productionSourceMap: false,
  // 多入口配置
  // pages: {
  //   index: {
  //     entry: 'src/main.js',
  //     template: 'public/index.html',
  //     filename: 'index.html',
  //   }
  // },
  // 打包app时放开该配置
  // publicPath:'./',
  configureWebpack: config => {
    // Ignore related resources when building with cdn
    config.externals = needCdn ? assetsCDN.externals : {}
    // 生产环境取消 console.log
    if (isProd) {
      config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
    }
  },
  chainWebpack: (config) => {
    // 初始化页面的title为配置文件设置的值，public/index.html中的htmlWebpackPlugin.options.title
    config.plugin('html').tap((args) => {
      if (needCdn) {
        args[0].cdn = assetsCDN
      }
      return args
    })
    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@views', resolve('src/views'))

    // 生产环境，开启js\css压缩
    if (isProd) {
        config.plugin('compressionPlugin').use(new CompressionPlugin({
          test: /\.(js|css|less)$/, // 匹配文件名
          threshold: 10240, // 对超过10k的数据压缩
          deleteOriginalAssets: false // 不删除源文件
        }))
    }

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
      '/jeecg-boot': {
        target: 'http://localhost:8080', // 请求本地 需要jeecg-boot后台项目
        ws: false,
        changeOrigin: true
      }
    }
  },

  lintOnSave: undefined
}
