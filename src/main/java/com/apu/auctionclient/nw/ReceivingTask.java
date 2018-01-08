/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.nw.entity.Message;
import com.apu.auctionclient.utils.Log;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class ReceivingTask implements Runnable {
    
    private static final Log log = Log.getInstance();
    private final Class classname = ReceivingTask.class;
    
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
                is = socket.getInputStream();
                String line;
                String str;
                int amount = 0;
                StringBuilder sb = new StringBuilder();;
                byte[] bytes = new byte[1024];
                while(!socket.isClosed()) {
                    try {
                        Thread.sleep(1);
                    } catch(InterruptedException ex) {
                        log.debug(classname, "Thread sleep is interrupted.");
                        break;
                    }
                    if(Thread.currentThread().isInterrupted()) {
                        log.debug(classname, "Receiving thread. Interrupted.");
                        break;
                    }
//                    if(is.available() == 0) continue;
                    try {
//                        log.debug(classname, "Try to read.");
                        amount = is.read(bytes, 0, 1024);
                        log.debug(classname, "Read bytes. Amount: " + amount);
                    } catch (SocketTimeoutException ex) {
                        continue;
                    }
                    if(amount == -1) {
                        log.debug(classname, "Receive end of socket.");
                        break;
                    }
                    if(amount == 0) continue;
                    str = new String(bytes, 0, amount);
                    sb.append(str);
                    if(sb.toString().contains("\r\n")) {
                        line = sb.toString();
                        sb.delete(0, sb.capacity());
                        if(line != null) {
                            log.debug(classname, line);
                            networkController.handle(line);
                        }
                    }                   
                }
            } catch (InterruptedException ex) {
                log.debug(classname,ExceptionUtils.getStackTrace(ex));
                log.debug(classname, "Receiving thread. Message - Error");
                messagesQueue.add(new Message("Error"));                
            } catch (Exception ex) { 
                log.debug(classname,ExceptionUtils.getStackTrace(ex));
        } finally { } 
    }
    
}
