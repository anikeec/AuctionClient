/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class Time {
    
    private static final Log log = Log.getInstance();
    private static final Class classname = Time.class;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
    
    public static String getTime() {        
        return dateFormat.format(new Date());
    }
    
    public static Date timeToDate(String time) {
        try {
            return dateFormat.parse(time);
        } catch (ParseException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }
    
    public static String getDelay(String timeAsk, String timeAnswer) {
        try {
            long timeAskLong = dateFormat.parse(timeAsk).getTime();
            long timeAnswerLong = dateFormat.parse(timeAnswer).getTime();
            long delay = timeAnswerLong - timeAskLong;
            return delay + " ms";
        } catch (ParseException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
        }
        return "Count error";
    }
    
}
