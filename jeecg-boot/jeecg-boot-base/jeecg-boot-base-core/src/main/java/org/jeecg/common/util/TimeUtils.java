package org.jeecg.common.util;

/**
 * @author yq
 * @description 时间工具类
 * @date 2022/4/3 17:51
 */
public class TimeUtils {
    public static String formatDelayTime(Long delayTime) {
        if (delayTime < 60) {
            return String.format("%s秒", delayTime);
        }
        if (delayTime < 3600) {
            return String.format("%s分钟", (int) Math.ceil((double) (delayTime / 60L)));
        }
        if (delayTime < 86400) {
            return String.format("%s小时", (int) Math.ceil((double) (delayTime / 3600L)));
        }
        return String.format("%s天", (int) Math.ceil((double) (delayTime / 86400L)));
    }
}
