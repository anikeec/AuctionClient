/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.controller.NetworkController;
import com.apu.auctionclient.entity.Message;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class ReceivingTask implements Runnable {
    
    private final BlockingQueue<AuctionQuery> sendedQueriesQueue;
    private final BlockingQueue<Message> messagesQueue;
    private final Socket socket;

    public ReceivingTask(BlockingQueue<AuctionQuery> sendedQueriesQueue, 
                        BlockingQueue<Message> messagesQueue,
                        Socket socket) {
        this.sendedQueriesQueue = sendedQueriesQueue;
        this.socket = socket;
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void run() {
        InputStream is = null;
//        OutputStream os = null;
        NetworkController controller = NetworkController.getInstance();
        try {
            try {
                is = socket.getInputStream();
//                os = socket.getOutputStream();
//                BufferedReader in = new BufferedReader(new InputStreamReader(is));
//                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
                String line = null;
                StringBuilder sb = new StringBuilder();;
                byte[] bytes = new byte[1024];
                while(!socket.isClosed()) { 
                    
                    while(true) {
                        if(Thread.currentThread().isInterrupted())    break;
                        if(is.available() == 0) continue;
                        int amount = is.read(bytes, 0, 1024);
                        String str = new String(bytes, 0, amount);
                        sb.append(str);
                        if(sb.toString().contains("\r\n"))  break;
                    }                    
                    //line = in.readLine(); // ожидаем пока клиент пришлет что-то
                    line = sb.toString();
                    sb.delete(0, sb.capacity());
                    if(line != null) {
                        System.out.println(line);
                        controller.handle(line);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ReceivingTask.class.getName()).log(Level.SEVERE, null, ex);
                messagesQueue.add(new Message("Error"));
            } finally { 
//                if(os != null)  os.close();
                if(is != null)  is.close();
                System.out.println("Client closed"); 
                socket.close();                
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);    
        } 
    }
    
}
