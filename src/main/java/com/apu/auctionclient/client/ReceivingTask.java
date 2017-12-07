/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.controller.NetworkController;
import com.apu.auctionclient.entity.Message;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class ReceivingTask implements Runnable {
    
    private final NetworkController networkController;
    private final BlockingQueue<AuctionQuery> sendedQueriesQueue;
    private final BlockingQueue<Message> messagesQueue;
    private final Socket socket;

    public ReceivingTask(NetworkController networkController,
                        BlockingQueue<AuctionQuery> sendedQueriesQueue, 
                        BlockingQueue<Message> messagesQueue,
                        Socket socket) {
        this.networkController = networkController;
        this.sendedQueriesQueue = sendedQueriesQueue;
        this.socket = socket;
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            try {
                is = socket.getInputStream();
                String line;
                String str;
                int amount;
                StringBuilder sb = new StringBuilder();;
                byte[] bytes = new byte[1024];
                while(!socket.isClosed()) {                    
                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Receiving thread. Interrupted.");
                        break;
                    }
                    if(is.available() == 0) continue;
                    amount = is.read(bytes, 0, 1024);
                    str = new String(bytes, 0, amount);
                    sb.append(str);
                    if(sb.toString().contains("\r\n")) {
                        line = sb.toString();
                        sb.delete(0, sb.capacity());
                        if(line != null) {
                            System.out.println(line);
                            networkController.handle(line);
                        }
                    }                   
                }
            } catch (Exception ex) {
                Logger.getLogger(ReceivingTask.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Receiving thread. Message - Error");
                messagesQueue.add(new Message("Error"));                
            } finally { 
                if(is != null) { 
                    is.close();
                    System.out.println("Receiving thread. Input socket closed");           
                }                    
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);    
        } 
    }
    
}
