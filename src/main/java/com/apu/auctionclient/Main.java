/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient;

import com.apu.auctionclient.nw.client.Client;
import com.apu.auctionclient.utils.Log;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class Main {
    
    private static final Log log = Log.getInstance();
    private static final Class classname = Main.class;
    
    static Client client;    
    
    public static void main(String[] args) {
        try {
            if(args.length == 2) {
                Integer userId = Integer.parseInt(args[0]);
                Integer lotId = Integer.parseInt(args[1]);
                log.debug(classname,"UserId = " + userId + ", LotId = " + lotId);
                Client.getInstance().start(userId, lotId);
            }           
        } catch (IOException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
        }
    }
    
}
