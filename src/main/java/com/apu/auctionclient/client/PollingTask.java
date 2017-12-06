/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.PollQuery;
import com.apu.auctionclient.entity.User;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class PollingTask extends TimerTask {
    
    private User user;
    private BlockingQueue<AuctionQuery> queriesQueue;

    public PollingTask(User user) {
        this.user = user;
    }
    
    public void setQueriesQueue(BlockingQueue<AuctionQuery> queriesQueue) {
        this.queriesQueue = queriesQueue;
    }

    @Override
    public void run() {        
        AuctionQuery query = new PollQuery(user.getUserId());        
        queriesQueue.add(query);
    }   
    
    
}
