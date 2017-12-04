/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

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
public class ConnectionHandler implements Runnable{
    private BlockingQueue<Socket> queue;
    private Socket socketTemp;
	
    public ConnectionHandler(BlockingQueue<Socket> queue) {
            this.queue = queue;
    }

    public void run() {
        while(true) {
            socketTemp = null;
            try {
                socketTemp = queue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(socketTemp != null) {
                handleSocket(socketTemp);
            }
        }
    }

    private void handleSocket(Socket socket) {
        try {
            // do anything you need
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
            String line = null;
            while(true) {
                line = in.readLine(); // ожидаем пока клиент пришлет строку текста.
                if(line.contains("Bye.")) break;
                System.out.println(line);
                out.write(line + "\n"); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
//                System.out.println();
            } 
            os.close();
            is.close();
            socketTemp.close();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
