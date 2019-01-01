package com.github.zgynhqf.rafy4j.utils;

import java.time.*;
import java.util.Date;

/**
 * @author: huqingfang
 * @date: 2018-12-13 03:20
 **/
public class TimeHelper {
    public static LocalDateTime toLocalDateTime(Date time) {
        Instant instant = time.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    public static Date toDate(LocalDate date) {
        LocalDateTime time = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        return toDate(time);
    }

    public static Date toDate(LocalDateTime time) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = time.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }
}
