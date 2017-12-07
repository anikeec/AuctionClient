/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.client;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.PollQuery;
import com.apu.auctionclient.entity.Message;
import com.apu.auctionclient.entity.User;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class PollingTask extends TimerTask {
    
    private final User user;
    private BlockingQueue<AuctionQuery> queriesQueue;
    private final BlockingQueue<Message> messagesQueue;

    public PollingTask(User user, BlockingQueue<Message> messagesQueue) {
        this.user = user;
        this.messagesQueue = messagesQueue;
    }
    
    public void setQueriesQueue(BlockingQueue<AuctionQuery> queriesQueue) {
        this.queriesQueue = queriesQueue;
    }

    @Override
    public void run() {        
        AuctionQuery query = new PollQuery(user.getUserId());
        if(queriesQueue.remainingCapacity() > 0)        
            queriesQueue.add(query);
        else {
            System.out.println("PollingTask Thread. Message - Error");
            messagesQueue.add(new Message("Error"));
        }
            
    }   
    
    
}
