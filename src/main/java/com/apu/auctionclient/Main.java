/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient;

import com.apu.auctionclient.client.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class Main {
    private static final int CONNECTIONS_MAX = 10;
    private static final int CONNECTION_PORT = 5050;
    private static final String CONNECTION_HOST = "localhost";
    static Client client;
    
    public static void main(String[] args) {
        try {
            client = new Client(CONNECTION_HOST, CONNECTION_PORT, CONNECTIONS_MAX);
            client.start();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
