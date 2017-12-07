/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.RegistrationQuery;
import static com.apu.auctionclient.client.Client.getClientState;
import com.apu.auctionclient.controller.NetworkController;
import com.apu.auctionclient.entity.Message;
import com.apu.auctionclient.entity.User;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class Network implements Runnable {
    final int QUEUE_SIZE = 10;
    final int MESSAGE_QUEUE_SIZE = 10;
    
    private final Socket socket;
    private final User user;
    private boolean isServer = false;
    private BlockingQueue<AuctionQuery> queriesQueue;
    private BlockingQueue<AuctionQuery> sendedQueriesQueue; 
    private BlockingQueue<Message> messagesQueue;
    
    private Timer timer;
    private Thread sendingThread;
    private Thread receivingThread;

    public Network(User user, Socket socket, boolean runAsServer) {
        this.user = user;
        this.socket = socket;
        this.isServer = runAsServer;
    }   
    
    private void init() {
        queriesQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        sendedQueriesQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        messagesQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);
        
        NetworkController networkController = NetworkController.getInstance();
        networkController.setUser(user);
        networkController.setQueriesQueue(queriesQueue);
        networkController.setSendedQueriesQueue(sendedQueriesQueue);
        
        sendingThread = new Thread(new SendingTask(queriesQueue, 
                                                    sendedQueriesQueue,
                                                    messagesQueue,
                                                    socket));
        sendingThread.start(); 

        receivingThread = new Thread(new ReceivingTask(sendedQueriesQueue,
                                                        messagesQueue,
                                                        socket));
        receivingThread.start();
    }
    
    private void stop() throws IOException {        
        try {
            timer.cancel();            
            System.out.println("Network thread. Timer stopped");
            System.out.println("Network thread. Sending thread try to interrupt");
            sendingThread.interrupt();
            System.out.println("Network thread. Receiving thread try to interrupt");
            receivingThread.interrupt();
            System.out.println("Network thread. Sending thread wait");
            sendingThread.join();
            System.out.println("Network thread. Sending thread interrupted");
            System.out.println("Network thread. Receiving thread wait");
            receivingThread.join();
            System.out.println("Network thread. Receiving thread interrupted");
            socket.close();
            System.out.println("Network thread. Socket closed");
        } catch (InterruptedException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        init();
        if(isServer)
            serverRun();
        else
            clientRun();
    }
    
    private void clientRun() {
        AuctionQuery query = new RegistrationQuery(user.getUserId());
        queriesQueue.add(query);

        while(getClientState() == ClientState.NOT_CONNECTED) {};

        PollingTask pollingTask = new PollingTask(user);
        this.timer = new Timer(false);//run not as daemon
        pollingTask.setQueriesQueue(queriesQueue);
        timer.scheduleAtFixedRate(pollingTask, 1000, 1000);
        
        while(true) {
            try {
                Message mess = messagesQueue.take();
                if(mess.getMessage().equals("Error") || 
                   mess.getMessage().equals("Socket closed")) {
                    stop();
                    break;
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void serverRun() {
        
    }    
    
}
