/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class ConnectionHandlerPool {    
    private BlockingQueue<Socket> queue = new ArrayBlockingQueue<>(20,true);
	
    public ConnectionHandlerPool(int backlog) {
            for (int i = 0; i < backlog; i++) {
                    new Thread(new ConnectionHandler(queue)).start();
            }
    }

    public void addConnection(Socket socket) {
            queue.add(socket);
    }
}
