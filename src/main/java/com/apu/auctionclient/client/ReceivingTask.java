/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionclient.controller.Controller;
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
    private final Socket socket;
    private long packetId = 0;

    public ReceivingTask(BlockingQueue<AuctionQuery> sendedQueriesQueue, 
                        Socket socket) {
        this.sendedQueriesQueue = sendedQueriesQueue;
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        Controller controller = Controller.getInstance();
        try {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
                String line = null;
                while(!socket.isClosed()) {
                    line = in.readLine(); // ожидаем пока клиент пришлет что-то
                    if(line != null) {
                        System.out.println(line);
                        controller.handle(line);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ReceivingTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ReceivingTask.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
    //            timer.cancel(); 
                if(os != null)  os.close();
                if(is != null)  is.close();
                System.out.println("Client closed"); 
                socket.close();                
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);    
        } 
    }
    
}
