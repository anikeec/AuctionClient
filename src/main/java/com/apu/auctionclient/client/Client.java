/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.RegistrationQuery;
import com.apu.auctionclient.controller.Controller;
import com.apu.auctionclient.entity.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class Client {
    private Socket clientSocket;
    private ConnectionHandlerPool handlerPool;
    private int backlog;
    private String host; 
    private Timer timer;    

    public Client(String host, int port, int backlog) throws IOException {            
            this.backlog = backlog;
            this.host = host;
            clientSocket = new Socket(host, port);
    }

    public void start() throws IOException {
//            handlerPool = new ConnectionHandlerPool(backlog);
            System.out.println("Client started");         
            int usedId = 1;
            System.out.println("Try to connect");
            handleSocket(usedId);               
    }
    
    private void handleSocket(int userId) {
        final int QUEUE_SIZE = 10;
        Long packetId = (long)0;
        InputStream is = null;
        OutputStream os = null;
        try {
            try {
                // do anything you need
                is = clientSocket.getInputStream();
                os = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));                

                User user = new User(userId, clientSocket, in, out);

                BlockingQueue<AuctionQuery> queriesQueue = 
                            new ArrayBlockingQueue<>(QUEUE_SIZE);
                BlockingQueue<AuctionQuery> sendedQueriesQueue = 
                            new ArrayBlockingQueue<>(QUEUE_SIZE);
                
                Controller controller = Controller.getInstance();
                controller.setUser(user);
                controller.setQueriesQueue(queriesQueue);
                controller.setSendedQueriesQueue(sendedQueriesQueue);
                
                new Thread(new SendingTask(queriesQueue, 
                                            sendedQueriesQueue, 
                                            clientSocket))
                                            .start();                

                AuctionQuery query = new RegistrationQuery(userId);
                queriesQueue.add(query);

                PollingTask pollingTask = new PollingTask(user);
                this.timer = new Timer(true);//run as daemon
                pollingTask.setQueriesQueue(queriesQueue);
                timer.scheduleAtFixedRate(pollingTask, 1000, 2000);

                String line = null;
                while(!clientSocket.isClosed()) {
                    line = in.readLine(); // ожидаем пока клиент пришлет что-то
                    if(line != null) {
                        System.out.println(line);                     
                        controller.handle(line);  
                    }
                }  
            } catch (Exception ex) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);                       
            } finally {
                timer.cancel(); 
                if(os != null)  os.close();
                if(is != null)  is.close();
                System.out.println("Client closed"); 
                clientSocket.close();                
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);    
        }
    }

}
