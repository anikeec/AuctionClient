/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw;

import com.apu.auctionclient.nw.ReceivingTask;
import com.apu.auctionclient.nw.SendingTask;
import com.apu.auctionclient.nw.PollingTask;
import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.RegistrationQuery;
import com.apu.auctionclient.nw.client.ClientState;
import static com.apu.auctionclient.nw.client.Client.getClientState;
import com.apu.auctionclient.nw.NetworkController;
import com.apu.auctionclient.nw.entity.Message;
import com.apu.auctionclient.nw.entity.User;
import com.apu.auctionclient.utils.Log;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class Network implements Runnable {
    final int QUEUE_SIZE = 10;
    final int MESSAGE_QUEUE_SIZE = 10;
    
    private static final Log log = Log.getInstance();
    private final Class classname = Network.class;
    
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
        
        NetworkController networkController = 
                new NetworkController(user, queriesQueue, sendedQueriesQueue);
        
        sendingThread = new Thread(new SendingTask(queriesQueue, 
                                                    sendedQueriesQueue,
                                                    messagesQueue,
                                                    socket));
        sendingThread.start(); 

        receivingThread = new Thread(new ReceivingTask(networkController,
                                                        sendedQueriesQueue,
                                                        messagesQueue,
                                                        socket));
        receivingThread.start();
    }
    
    private void stop() throws IOException {        
        try {
            timer.cancel();            
            log.debug(classname, "Network thread. Timer stopped");
            log.debug(classname, "Network thread. Sending thread try to interrupt");
            sendingThread.interrupt();
            log.debug(classname, "Network thread. Receiving thread try to interrupt");
            receivingThread.interrupt();
            log.debug(classname, "Network thread. Sending thread wait");
            sendingThread.join();
            log.debug(classname, "Network thread. Sending thread interrupted");
            log.debug(classname, "Network thread. Receiving thread wait");
            receivingThread.join();
            log.debug(classname, "Network thread. Receiving thread interrupted");
            socket.close();
            log.debug(classname, "Network thread. Socket closed");
        } catch (InterruptedException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
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

        PollingTask pollingTask = new PollingTask(user, messagesQueue);
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
                log.debug(classname,ExceptionUtils.getStackTrace(ex));
            }
        }
    }
    
    private void serverRun() {
        
    }    
    
}
