/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionclient.entity.User;
import com.apu.auctionclient.utils.Log;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author apu
 */
public class Client {
    private static final Log log = Log.getInstance();
    private final Class classname = Client.class;
    private final Socket clientSocket;
    private final String host; 
    
    private static ClientState clientState = ClientState.NOT_CONNECTED;

    public Client(String host, int port) throws IOException {            
            this.host = host;
            clientSocket = new Socket(host, port);
    }

    public static synchronized ClientState getClientState() {
        return clientState;
    }

    public static synchronized void setClientState(ClientState state) {
        clientState = state;
    }   

    public void start() throws IOException {
            log.debug(classname, "Client started");         
            int usedId = 1;
            log.debug(classname, "Try to connect");
            handleSocket(usedId);               
    }
    
    private void handleSocket(int userId) {
        Network network = null;           

                User user = new User(userId, clientSocket);
                
                network = new Network(user, clientSocket, false);
                new Thread(network).start();

    }

}
