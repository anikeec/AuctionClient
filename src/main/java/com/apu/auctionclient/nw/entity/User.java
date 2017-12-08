/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw.entity;

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

    public User(int userId, Socket socket) {
        this.userId = userId;
        this.socket = socket;
    }

    public int getUserId() {
        return userId;
    }

    public Socket getSocket() {
        return socket;
    }    
    
}
