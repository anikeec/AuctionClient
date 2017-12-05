/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.PollQuery;
import com.apu.auctionclient.entity.User;
import com.apu.auctionclient.utils.Coder;
import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class PollingTask extends TimerTask {
    
    private User user;
    private Long packetId;

    public PollingTask(User user, Long packetId) {
        this.user = user;
        this.packetId = packetId;
    }

    @Override
    public void run() {        
        AuctionQuery query = new PollQuery(packetId, user.getUserId());
        String line = Coder.getInstance().code(query);
        System.out.println("send:" + line);
        try { 
            if(user.getOut() == null) {
                this.cancel();
            }
            user.getOut().write(line);
            user.getOut().flush();
            packetId++;
        } catch (IOException ex) {
            Logger.getLogger(PollingTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    
}
