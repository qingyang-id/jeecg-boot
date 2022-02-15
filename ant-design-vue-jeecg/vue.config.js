const path = require('path')
const SpeedMeasurePlugin = require('speed-measure-webpack-plugin')
const CompressionPlugin = require('compression-webpack-plugin')
const { HashedModuleIdsPlugin } = require('webpack');
console.log(process.env) // remove this after you've confirmed it working

const isProd = process.env.NODE_ENV === 'production' || process.env.BABEL_ENV === 'production'

console.log('is prod:', isProd)

// cdn 外部扩展，通过 cdn 引入，不会被webpack打包
const assetsCDN = {
  // 模块名称和模块作用域命名（对应window里面挂载的变量名称）
  externals: {
    vue: 'Vue',
    'vue-router': 'VueRouter',
    vuex: 'Vuex',
    axios: 'axios',
    moment: 'moment',
    'ant-design-vue': 'antd',
  },
  // cdn的css链接
  css: [
    'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.css'
  ],
  // cdn的js链接
  js: [
    'https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js', // 必须先引入moment，否则报错“TypeError: Cannot read property 'default' of undefined”
    'https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/zh-cn.js', // 需同步引入语言包，否则日期选择控件等将默认显示为英文
    'https://cdn.jsdelivr.net/npm/vue@2.6.11/dist/vue.min.js',
    'https://cdn.jsdelivr.net/npm/vue-router@3.3.4/dist/vue-router.min.js',
    'https://cdn.jsdelivr.net/npm/vuex@3.5.1/dist/vuex.min.js',
    'https://cdn.jsdelivr.net/npm/axios@0.18.1/dist/axios.min.js',
    'https://cdn.jsdelivr.net/npm/ant-design-vue@1.7.8/dist/antd.min.js',
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
  //     // entry for the page
  //     entry: 'src/main.js',
  //     // the source template
  //     template: 'public/index.html',
  //     // output as dist/index.html
  //     filename: 'index.html',
  //     // when using title option,
  //     // template title tag needs to be <title><%= htmlWebpackPlugin.options.title %></title>
  //     // title: 'ERP管理系统',
  //     // chunks to include on this page, by default includes
  //     // extracted common chunks and vendor chunks.
  //     chunks: ['chunk-vendors', 'chunk-common', 'index']
  //   }
  // },
  // 打包app时放开该配置
  // publicPath:'./',
  configureWebpack: config => {
    // Ignore related resources when building with cdn
    // config.externals = isProd ? assetsCDN.externals : {}
    // config.module.noParse = /^(vue|vue-router|vuex|vuex-router-sync|axios|ant-design-vue|moment)$/
    // 生产环境取消 console.log
    // if (isProd) {
    //   config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
    // }
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@views', resolve('src/views'))

    // 压缩图片
    // config.module
    //   .rule('images')
    //   .test(/\.(png|jpe?g|gif|svg)(\?.*)?$/)
    //   .use('image-webpack-loader')
    //   .loader('image-webpack-loader')
    //   .options({ bypassOnDebug: true })

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
            test: /\.(sa|sc|c)ss$/,
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
