/**
 * 订单金额 / 面积 / 数量计算（纯函数）
 *
 * 逐公式移植自老前端
 *   ant-design-vue-jeecg/src/renderer/views/modules/business/order/modules/JshOrderForm.vue
 * 老代码依赖 bignumber.js；新工程未引入该依赖，这里用 BigInt 实现了老代码用到的
 * 三个运算，语义与 bignumber.js 一致：
 *   new BigNumber(<number>).toFixed(dp, BigNumber.ROUND_UP)  -> bnToFixedUp
 *   new BigNumber(a).plus(b).toFixed()                       -> bnPlus
 *   new BigNumber(a).multipliedBy(10^n).toFixed()            -> bnTimesPow10
 *
 * 单位约定（与后端一致）：单价/附加/总价 存“分”，面积 存“mm²”；
 * 表单上展示“元”与“m²”。
 */

/* ======================= 十进制工具（bignumber.js 等价） ======================= */

interface Dec {
  neg: boolean;
  digits: bigint; // 绝对值的整数尾数
  scale: number; // 值 = digits * 10^-scale
}

// 构建目标为 es2015：不能用 BigInt 字面量（10n）与 ** 运算符（esbuild 会转成 Math.pow，BigInt 下报错）
const ZERO = BigInt(0);
const ONE = BigInt(1);
const TEN = BigInt(10);
function pow10(n: number): bigint {
  let r = ONE;
  for (let i = 0; i < n; i++) r = r * TEN;
  return r;
}

const DEC_RE = /^([+-])?(\d*)(?:\.(\d*))?(?:e([+-]?\d+))?$/i;

/** 与 bignumber.js 构造函数一致：number 先取 String(n)，字符串原样解析 */
function parseDec(v: unknown): Dec | null {
  if (typeof v === 'bigint') v = v.toString();
  if (typeof v === 'number') {
    if (!isFinite(v)) return null;
    v = Object.is(v, -0) ? '0' : String(v);
  }
  if (typeof v !== 'string') return null;
  const m = DEC_RE.exec(v.trim());
  if (!m) return null;
  const intPart = m[2] || '';
  const fracPart = m[3] || '';
  if (!intPart && !fracPart) return null;
  let scale = fracPart.length - (m[4] ? parseInt(m[4], 10) : 0);
  let digits = BigInt((intPart + fracPart).replace(/^0+(?=\d)/, '') || '0');
  if (scale < 0) {
    digits = digits * pow10(-scale);
    scale = 0;
  }
  return { neg: m[1] === '-' && digits !== ZERO, digits, scale };
}

function formatFixed(neg: boolean, digits: bigint, dp: number): string {
  let s = digits.toString();
  if (dp > 0) {
    s = s.padStart(dp + 1, '0');
    s = s.slice(0, s.length - dp) + '.' + s.slice(s.length - dp);
  }
  return (neg ? '-' : '') + s;
}

/** new BigNumber(v).toFixed(dp, BigNumber.ROUND_UP)  —— ROUND_UP = 远离 0 方向进位 */
export function bnToFixedUp(v: unknown, dp: number): string {
  const d = parseDec(v);
  if (!d) return 'NaN';
  let digits = d.digits;
  if (d.scale <= dp) {
    digits = digits * pow10(dp - d.scale);
  } else {
    const div = pow10(d.scale - dp);
    const q = digits / div;
    digits = digits % div === ZERO ? q : q + ONE;
  }
  return formatFixed(d.neg, digits, dp);
}

/** BigNumber#toFixed()（无参）：精确值、普通记数法、去掉末尾 0 */
function decToString(d: Dec): string {
  let { digits, scale } = d;
  while (scale > 0 && digits % TEN === ZERO) {
    digits = digits / TEN;
    scale--;
  }
  return formatFixed(d.neg && digits !== ZERO, digits, scale);
}

/** new BigNumber(a).plus(b).toFixed() */
export function bnPlus(a: unknown, b: unknown): string {
  const x = parseDec(a);
  const y = parseDec(b);
  if (!x || !y) return 'NaN';
  const scale = Math.max(x.scale, y.scale);
  const xv = (x.neg ? -ONE : ONE) * x.digits * pow10(scale - x.scale);
  const yv = (y.neg ? -ONE : ONE) * y.digits * pow10(scale - y.scale);
  const sum = xv + yv;
  return decToString({ neg: sum < ZERO, digits: sum < ZERO ? -sum : sum, scale });
}

/** new BigNumber(a).multipliedBy(10^n).toFixed()  （老代码只乘 100 / 1000000） */
export function bnTimesPow10(a: unknown, n: number): string {
  const x = parseDec(a);
  if (!x) return 'NaN';
  let { digits, scale } = x;
  scale -= n;
  if (scale < 0) {
    digits = digits * pow10(-scale);
    scale = 0;
  }
  return decToString({ neg: x.neg, digits, scale });
}

/* ======================= 业务计算 ======================= */

export interface ProductInfo {
  wholesalePrice?: number;
  aluminumWidthDiff?: number;
  aluminumHeightDiff?: number;
  glassWidthDiff?: number;
  glassHeightDiff?: number;
  [key: string]: any;
}

export type RowPriceResult =
  | { fixExtendNum: number }
  | { values: { price: string; extendPrice: string; totalArea: string; totalPrice: string } };

/**
 * 单行价格计算
 * 老：JshOrderForm.vue:391-438 calculateCurrentRowPrice(row)
 *
 * 抽（extendType=1）数量不是 3 的倍数时，老代码先把数量改成 floor(n/3)*3 并 return，
 * 由 setValues 触发的 valueChange 再算一次价格；这里返回 fixExtendNum 交给调用方
 * 先修正数量再重算，最终结果相同。
 */
export function calculateRowPrice(row: Recordable, product?: ProductInfo | null): RowPriceResult {
  const price = (product && product.wholesalePrice) || 0;
  let extendPrice = 0;
  switch (Number(row.extendType)) {
    case 1: {
      // 抽 20元/3抽
      if (Number(row.extendNum) && row.extendNum % 3 !== 0) {
        return { fixExtendNum: Math.floor(row.extendNum / 3) * 3 };
      }
      extendPrice = ((row.extendNum || 0) * 2000) / 3;
      break;
    }
    case 2: // 拉 10/拉
    case 4: // 调味篮 10/位
    case 6:
    case 7: {
      extendPrice = (row.extendNum || 0) * 1000;
      break;
    }
    default: {
    }
  }
  const totalArea = (row.num || 0) * (row.width || 0) * (row.height || 0);
  const totalPrice = price * totalArea + extendPrice * 1000000;
  return {
    values: {
      price: bnToFixedUp(price / 100, 2),
      extendPrice: bnToFixedUp(extendPrice / 100, 2),
      totalArea: bnToFixedUp(totalArea / 1000000, 3),
      totalPrice: bnToFixedUp(totalPrice / 100000000, 2),
    },
  };
}

/**
 * 订单汇总
 * 老：JshOrderForm.vue:440-454 calculateSumPrice()
 */
export function calculateSum(rows: Recordable[]): { totalArea: string; totalPrice: string } {
  let totalArea = '0';
  let totalPrice = '0';
  rows.forEach((item) => {
    totalArea = bnPlus(totalArea, item.totalArea || 0);
    totalPrice = bnPlus(totalPrice, item.totalPrice || 0);
  });
  return { totalArea, totalPrice };
}

/**
 * 切换“抽/拉/条”类型时的数量联动
 * 老：JshOrderForm.vue:501-540 handleValueChange case 'extendType'
 * 返回需要写回行的值；null 表示不改
 * 注意：老代码 `if (!value)` 中 value 为字符串，'0' 为真值，不会走清零分支，此处保持一致。
 */
export function extendTypeChangeValues(value: any, row: Recordable): Recordable | null {
  if (!value) {
    return { extendNum: 0, extendPrice: 0 };
  } else if (!row.extendNum) {
    switch (Number(value)) {
      case 1:
        // 抽
        return { extendNum: (row.num || 1) * 3 };
      // 免费拉手
      case 5:
      // 付费拉手
      case 2:
        // 拉
        return { extendNum: (row.num || 1) * 2 };
      // 条子
      case 3:
        return { extendNum: row.num || 1 };
    }
  }
  return null;
}

/**
 * 编辑时把后端行（分 / mm²）转换为表格展示值（元 / m²）
 * 老：JshOrderForm.vue:614-621
 */
export function productRowToDisplay(item: Recordable): Recordable {
  return Object.assign(item, {
    direction: `${item.direction}`,
    extendType: `${item.extendType}`,
    price: `${item.price / 100}`,
    extendPrice: `${item.extendPrice / 100}`,
    totalArea: `${item.totalArea / 1000000}`,
    totalPrice: `${item.totalPrice / 100}`,
  });
}

/**
 * 编辑时主表展示值
 * 老：JshOrderForm.vue:599-600
 */
export function orderToDisplay(model: Recordable): Recordable {
  model.totalArea = model.totalArea ? `${model.totalArea / 1000000}` : '0';
  model.totalPrice = model.totalPrice ? `${model.totalPrice / 100}` : '0';
  return model;
}

/**
 * 保存时：原尺寸行 -> 铝材/玻璃明细 + 扩展项，并把元/m² 还原为 分/mm²
 * 老：JshOrderForm.vue:647-734 classifyIntoFormData()
 */
export function buildOrderPayload(main: Recordable, productRows: Recordable[], productMap: Record<string, ProductInfo>): Recordable {
  const jshOrderProductPageList = productRows;
  if (jshOrderProductPageList.length) {
    jshOrderProductPageList.forEach((item) => {
      const jshOrderProductDetailList: Recordable[] = [];
      const jshOrderProductExtendList: Recordable[] = [];
      const productDetail: Recordable = {
        referenceWidth: Number(item.width),
        referenceHeight: Number(item.height),
        num: Number(item.num),
        color: item.color,
        direction: item.direction,
        extendType: item.extendType,
        extendNum: item.extendNum,
      };
      switch (Number(item.extendType)) {
        case 1: {
          // 抽 高/3 - 2
          Object.assign(productDetail, {
            referenceHeight: Math.ceil(item.height / 3) - 2,
            num: item.num * 3,
          });
          jshOrderProductExtendList.push({
            type: item.extendType,
            num: item.extendNum,
          });
          break;
        }
        // 免费拉手
        case 5:
        // 付费拉手
        case 2: {
          // 拉 原尺寸高/2 - 2
          Object.assign(productDetail, {
            referenceHeight: Math.ceil(item.height / 2) - 2,
            num: item.num * 2,
          });
          jshOrderProductExtendList.push({
            type: item.extendType,
            num: item.extendNum,
          });
          break;
        }
        case 3: {
          // 老代码此处 key 为 extendType/extendNum（非 type/num），原样保留
          jshOrderProductExtendList.push({
            extendType: item.extendType,
            extendNum: item.extendNum,
          });
          break;
        }
      }
      // 老代码直接取 jshProductPricesMap[item.productId].xxx，商品缺失时抛错（handleOk 提示“信息有误”）
      const product = productMap[item.productId];
      if (!product) {
        throw new Error(`商品 ${item.productId} 信息未加载`);
      }
      jshOrderProductDetailList.push({
        ...productDetail,
        type: 1,
        width: productDetail.referenceWidth + (product.aluminumWidthDiff as number),
        height: productDetail.referenceHeight + (product.aluminumHeightDiff as number),
        color: item.color,
      });
      jshOrderProductDetailList.push({
        ...productDetail,
        type: 2,
        width: productDetail.referenceWidth + (product.glassWidthDiff as number),
        height: productDetail.referenceHeight + (product.glassHeightDiff as number),
        color: item.glassColor,
      });
      Object.assign(item, {
        id: '',
        jshOrderProductExtendList,
        jshOrderProductDetailList,
        price: bnTimesPow10(item.price, 2),
        extendPrice: bnTimesPow10(item.extendPrice, 2),
        totalArea: bnTimesPow10(item.totalArea, 6),
        totalPrice: bnTimesPow10(item.totalPrice, 2),
      });
    });
  }
  return {
    ...main,
    jshOrderProductPageList,
    totalArea: bnTimesPow10(main.totalArea, 6),
    totalPrice: bnTimesPow10(main.totalPrice, 2),
  };
}
