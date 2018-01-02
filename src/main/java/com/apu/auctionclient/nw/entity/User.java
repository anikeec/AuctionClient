/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw.entity;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author apu
 */
public class User {
    private final int userId;
    private final Socket socket;
    private final List<Integer> observableLotIdList = new ArrayList<>();

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
    
    public void addLotToObservableList(int lotId) {
        if(!observableLotIdList.contains(lotId))
            observableLotIdList.add(lotId);
    }

    public List<Integer> getObservableLotIdList() {
        return observableLotIdList;
    }    
    
}
