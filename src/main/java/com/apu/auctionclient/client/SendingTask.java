/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.utils.Coder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class SendingTask implements Runnable {
    
    private final BlockingQueue<AuctionQuery> queriesQueue;
    private final BlockingQueue<AuctionQuery> sendedQueriesQueue;
    private final Socket socket;
    private long packetId = 0;

    public SendingTask(BlockingQueue<AuctionQuery> queriesQueue, 
                        BlockingQueue<AuctionQuery> sendedQueriesQueue, 
                        Socket socket) {
        this.queriesQueue = queriesQueue;
        this.sendedQueriesQueue = sendedQueriesQueue;
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream os = null;
        try {
            AuctionQuery query;
            os = socket.getOutputStream();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
            String line;
            while(true) {
                try {
                    query = queriesQueue.take();
                    query.setPacketId(packetId++);
                    while(sendedQueriesQueue.peek() != null){};
                    sendedQueriesQueue.add(query);
                    line = Coder.getInstance().code(query);
                    System.out.println("send:" + line);
                    out.write(line);
                    out.flush();                   
                } catch (IOException ex) {
                    Logger.getLogger(SendingTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SendingTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SendingTask.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(SendingTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
