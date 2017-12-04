/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.RegistrationQuery;
import com.apu.auctionclient.controller.Controller;
import com.apu.auctionclient.entity.User;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
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
    private Date date = new Date();

    public Client(String host, int port, int backlog) throws IOException {            
            this.backlog = backlog;
            this.host = host;
            clientSocket = new Socket(host, port);
    }

    public void start() throws IOException {
//            handlerPool = new ConnectionHandlerPool(backlog);
            System.out.println("Client started");         
            int usedId = 0;
//            while (true) {
                System.out.println("Try to connect");
                handleSocket(usedId);               
//            }
    }
    
    private void handleSocket(int userId) {
        try {
            // do anything you need
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
            String line = null;
            AuctionQuery query;
            long packetId = 0;
            
            User user = new User(userId, clientSocket, in, out);
            
            Controller controller = Controller.getInstance();
            controller.setUser(user);
            
            Thread.sleep(500);
            
            query = new RegistrationQuery(packetId, userId, date.toString());
            Gson gson = new Gson();
            line = gson.toJson(query) + "\r\n";
            System.out.println("send:" + line);
            out.write(line); 
            out.flush();
            
            while(true) {
                line = in.readLine(); // ожидаем пока клиент пришлет что-то
                if(line == null)    break;
                System.out.println(line);                
                controller.handle(line);                 
//                break;
            } 
            os.close();
            is.close();
            clientSocket.close();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
