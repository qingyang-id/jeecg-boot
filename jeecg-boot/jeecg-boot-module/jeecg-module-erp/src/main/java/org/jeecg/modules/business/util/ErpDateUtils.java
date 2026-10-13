package org.jeecg.modules.business.util;

import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

/**
 * ERP 自加的时间工具：从 3.0 版本 org.jeecg.common.util.DateUtils 的自加方法与 TimeUtils 原样迁移，仅类名变化。
 */
public class ErpDateUtils {

    public static Date getDateBegin(Date date) {
        Assert.notNull(date, "date cannot be null");
        return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
    }

    public static Date getDateEnd(Date date) {
        Assert.notNull(date, "date cannot be null");
        date = org.apache.commons.lang3.time.DateUtils.setHours(date, 23);
        date = org.apache.commons.lang3.time.DateUtils.setMinutes(date, 59);
        date = org.apache.commons.lang3.time.DateUtils.setSeconds(date, 59);
        date = org.apache.commons.lang3.time.DateUtils.setMilliseconds(date, 999);
        return date;
    }

    public static Date getTodayBegin() {
        return getDateBegin(new Date());
    }

    public static Date getMonthBegin(Date date) {
        Assert.notNull(date, "date cannot be null");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return getDateBegin(calendar.getTime());
    }

    public static Date getMonthEnd(Date date) {
        Assert.notNull(date, "date cannot be null");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return getDateEnd(calendar.getTime());
    }

    public static Date getTodayMonthBegin() {
        return getMonthBegin(new Date());
    }

    /**
     * 原 org.jeecg.common.util.TimeUtils#formatDelayTime
     */
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
