// 配置编译环境和线上环境之间的切换
const env = process.env;

const globalConfig = {
  ...env
};

export default globalConfig;
