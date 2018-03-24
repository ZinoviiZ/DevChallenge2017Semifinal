package com.dev.challenge.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * Date formats
 */
@Component
public class DateFormat {

    public static final SimpleDateFormat yyyyMMddhhMM = new SimpleDateFormat("yyyy-MM-dd hh:MM");
    public static final SimpleDateFormat yyyyMMddhh = new SimpleDateFormat("yyyy-MM-dd hh");
    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
}
