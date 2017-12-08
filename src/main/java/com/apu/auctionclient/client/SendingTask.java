/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.entity.Message;
import com.apu.auctionclient.utils.Coder;
import com.apu.auctionclient.utils.Log;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class SendingTask implements Runnable {
    
    private static final Log log = Log.getInstance();
    private final Class classname = SendingTask.class;
    
    private final BlockingQueue<AuctionQuery> queriesQueue;
    private final BlockingQueue<AuctionQuery> sendedQueriesQueue;
    private final BlockingQueue<Message> messagesQueue;
    private final Socket socket;
    private long packetId = 0;

    public SendingTask(BlockingQueue<AuctionQuery> queriesQueue, 
                        BlockingQueue<AuctionQuery> sendedQueriesQueue,
                        BlockingQueue<Message> messagesQueue,
                        Socket socket) {
        this.queriesQueue = queriesQueue;
        this.sendedQueriesQueue = sendedQueriesQueue;
        this.socket = socket;
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void run() {
        OutputStream os = null;
        try {
            AuctionQuery query;
            os = socket.getOutputStream();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
            String line;
            while(!socket.isClosed()) {
                if(Thread.currentThread().isInterrupted()) {    
                    throw new InterruptedException();
                }
                query = queriesQueue.peek();
                if(query == null)   continue;
                queriesQueue.remove();
                query.setPacketId(packetId++);
                while(sendedQueriesQueue.peek() != null){
                    if(Thread.currentThread().isInterrupted())     
                        throw new InterruptedException();
                };
                sendedQueriesQueue.add(query);
                line = Coder.getInstance().code(query);
                log.debug(classname, "send:" + line);
                out.write(line);
                out.flush();                   
            }
            log.debug(classname, "Sending thread. Message - Socket closed");
            messagesQueue.add(new Message("Socket closed"));            
        } catch (IOException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
            log.debug(classname, "Sending thread. Message - Error.");
            messagesQueue.add(new Message("Error"));            
        } catch (InterruptedException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
            log.debug(classname, "Sending thread. Interrupted.");
        } finally {            
            try {                
                if(os != null) {
                    os.close();
                    log.debug(classname, "Sending thread. Output socket closed");
                }
            } catch (IOException ex) {
                log.debug(classname,ExceptionUtils.getStackTrace(ex));
            }
        }
    }
    
}
