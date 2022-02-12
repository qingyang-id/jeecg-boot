package org.jeecg.modules.business.sequence.constant;

/**
 * @ClassName:BusinessConstants
 * @Description 业务字典类
 * @Author qiankunpingtai
 * @Date 2019-3-6 17:58
 * @Version 1.0
 **/
public class SequenceConstant {
    /**
     * sequence名称
     */
    // sequence返回字符串的最小长度
    public static final Long SEQ_TO_STRING_MIN_LENGTH = 10000000000L;
    // sequence长度小于基准长度时前追加基础值
    public static final String SEQ_TO_STRING_LESS_INSERT = "0";
    // 单据编号
    public static final String ORDER_NUMBER_SEQ = "order_number_seq";
    // 条码
    public static final String BAR_CODE_NUMBER_SEQ = "bar_code_number_seq";
}
