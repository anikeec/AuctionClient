/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class ConnectionThread implements Runnable{
    private Socket socketTemp;
	
    public ConnectionThread(Socket socket) {
        this.socketTemp = socket;
    }

    public void run() {            
        handleSocket(socketTemp);
    }

    private void handleSocket(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
            String line = null;
            while(true) {
                line = in.readLine(); // ожидаем пока клиент пришлет строку текста.
                if(line.contains("Bue")) break;
                System.out.println(line);
                out.write(line + "\n"); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
//                System.out.println();
            }  
            socketTemp.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

