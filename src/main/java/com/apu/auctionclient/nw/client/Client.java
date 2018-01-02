/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw.client;

import com.apu.auctionclient.nw.Network;
import com.apu.auctionclient.nw.entity.Message;
import com.apu.auctionclient.nw.entity.User;
import com.apu.auctionclient.utils.Log;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class Client {
    private static final Log log = Log.getInstance();
    private final Class classname = Client.class;
    private Socket clientSocket; 
    private static Network network;
    private static final int CONNECTION_PORT = 5050;
    private static final String CONNECTION_HOST = "127.0.0.1";
    private final int SOCKET_RECEIVE_TIMEOUT = 1000;
    final int MESSAGE_QUEUE_SIZE = 10;
    private BlockingQueue<Message> messagesQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);
    private Thread networkThread;
    
    private static ClientState clientState = ClientState.NOT_CONNECTED;
    
    private static Client instance;

    private Client() throws IOException {            
        
    }
    
    public static Client getInstance() throws IOException {
        if(instance == null)
            instance = new Client();
        return instance;
    }

    public static synchronized ClientState getClientState() {
        return clientState;
    }

    public static synchronized void setClientState(ClientState state) {
        clientState = state;
    }   

    public void start(int userId, int lotId) throws IOException {
        clientState = ClientState.NOT_CONNECTED;
        messagesQueue.clear();
        clientSocket = new Socket(CONNECTION_HOST, CONNECTION_PORT);
        clientSocket.setSoTimeout(SOCKET_RECEIVE_TIMEOUT);
        log.debug(classname, "Client started");         
//        int usedId = 1;
        log.debug(classname, "Try to connect");
        handleSocket(userId, lotId);               
    }
    
    public void stop() throws IOException {
        messagesQueue.add(new Message("Disconnect"));
        while(networkThread.isAlive()){};
        clientSocket = null;
        log.debug(classname, "Client finished");
    }
    
    public void newRateQuery(int lotId, int newRate) {
        network.addNewRate(lotId, newRate);
    }
    
    public void loadLotsQuery() {
        network.loadAuctionLots();
    }
    
    private void handleSocket(int userId, int lotId) {
        User user = new User(userId, clientSocket);
        user.addLotToObservableList(lotId);
        network = new Network(user, clientSocket, messagesQueue, false);
        networkThread = new Thread(network);
        networkThread.start();
    }

}
