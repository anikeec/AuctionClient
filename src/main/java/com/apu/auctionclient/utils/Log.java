/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.utils;

import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author apu
 */
public class Log {    
    static Logger logger = Logger.getLogger(Logger.class);
    private static Log instance;
    private BlockingQueue<String> logQueue;
    
    private Log() {}
    
    public static Log getInstance() {
        if(instance == null)    
            instance = new Log();
        return instance;
    }
    
    public synchronized void debug(Class className, String str) {
        logger.debug("(" + className.getName() + ") - " + editString(str));
    }
    
    public synchronized void warn(Class className, String str) {
        logger.warn("(" + className.getName() + ") - " + str);
    }
    
    public synchronized void error(Class className, String str) {
        logger.debug("(" + className.getName() + ") - " + str);
    }
    
    private String editString(String str) {
        int index = 0;
        int amount = 0;
        while(true) {
            index = str.indexOf("\r\n", index + 1);
            if(index == (-1)) break;
            amount++;
        }
        if(amount<2) {
            return str.replaceAll("\r\n", "");
        }
        return str;
    }
    
}
