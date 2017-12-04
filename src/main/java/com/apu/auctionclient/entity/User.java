/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 *
 * @author apu
 */
public class User {
    private int userId;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public User(int userId, Socket socket, BufferedReader in, BufferedWriter out) {
        this.userId = userId;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public int getUserId() {
        return userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public BufferedWriter getOut() {
        return out;
    }
    
    
}
