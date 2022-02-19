console.log(process.env) // remove this after you've confirmed it working
console.log(process.args) // remove this after you've confirmed it working

const isProd = process.env.NODE_ENV === 'production' || process.env.BABEL_ENV === 'production' || process.env.BABEL_ENV === 'test'
const isAPP = process.env.IS_ELECTRON

console.log('is prod:', isProd)
console.log('is app:', isAPP)

module.exports = {
  transpileDependencies: true,
  // 打包输出路径
  outputDir: 'dist/web',

  configureWebpack: {
    resolve: {
      alias: {
        '@$': '@',
        '@api': '@/api',
        '@assets': '@/assets',
        '@comp': '@/components',
        '@config': '@/config',
        '@views': '@/views',
      }
    }
  },

  chainWebpack: (config) => {
    console.log('fallback ', config.resolve.fallback)
    // config.resolve.fallback = {
    //   fallback
    // }
    // config.resolve.alias
    //   .set('@$', resolve('src'))
    //   .set('@api', resolve('src/api'))
    //   .set('@assets', resolve('src/assets'))
    //   .set('@comp', resolve('src/components'))
    //   .set('@config', resolve('src/config'))
    //   .set('@views', resolve('src/views'))
    config.resolve.alias
      .set('@$', '@src')
      .set('@api', 'src/api')
      .set('@assets', 'src/assets')
      .set('@comp', 'src/components')
      .set('@config', 'src/config')
      .set('@views', 'src/views')
  },

  css: {
    // 是否使用css分离插件 ExtractTextPlugin
    extract: isProd,
    // 开启 CSS source maps?
    sourceMap: !isProd,
    // css预设器配置项
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

  // 第三方插件配置
  pluginOptions: {
    // vue-cli-plugin-electron-builder配置
    electronBuilder: {
      outputDir: 'dist/electron',
      mainProcessFile: 'electron/main/index.js', // 主进程入口文件
      // rendererProcessFile: 'src/main.js', // 渲染进程入口文件
      mainProcessWatch: ['electron/main'], // 检测主进程文件在更改时将重新编译主进程并重新启动
      // 项目打包参数配置
      builderOptions: {
        productName: 'erp-admin', // 项目名称 打包生成exe的前缀名
        appId: 'com.qing.yang.erp', // 包名
        files: ['**/*'],
        extraFiles: [
          {
            from: './resource/*', // 项目资源
            to: './resource' // 打包后输出到的按照目录资源
          }
        ],
      },
      chainWebpackMainProcess: (config) => {
        config.plugin('define').tap((args) => {
          args[0].IS_ELECTRON = true
          return args
        })
      },
      chainWebpackRendererProcess: (config) => {
        config.plugin('define').tap((args) => {
          args[0].IS_ELECTRON = true
          return args
        })
      },
    }
  }
}
