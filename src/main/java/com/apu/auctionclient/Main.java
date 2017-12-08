/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient;

import com.apu.auctionclient.client.Client;
import com.apu.auctionclient.utils.Log;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class Main {
    private static final Log log = Log.getInstance();    
    private static final int CONNECTION_PORT = 5050;
    private static final String CONNECTION_HOST = "localhost";
    static Client client;    
    
    public static void main(String[] args) {
        try {
            client = new Client(CONNECTION_HOST, CONNECTION_PORT);
            client.start();
        } catch (IOException ex) {
            log.debug(Main.class,ExceptionUtils.getStackTrace(ex));
        }
    }
    
}
