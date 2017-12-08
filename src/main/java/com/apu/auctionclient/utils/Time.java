/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author apu
 */
public class Time {
    
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
    
}
