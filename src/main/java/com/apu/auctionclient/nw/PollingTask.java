/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.PollQuery;
import com.apu.auctionclient.nw.entity.Message;
import com.apu.auctionclient.nw.entity.User;
import com.apu.auctionclient.utils.Log;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class PollingTask extends TimerTask {
    
    private static final Log log = Log.getInstance();
    private final Class classname = PollingTask.class;
    
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
        if(queriesQueue.remainingCapacity() > 0) { 
            queriesQueue.add(new PollQuery(user.getUserId()));
        } else {
            log.debug(classname, "PollingTask Thread. Queue is full.");
//            messagesQueue.add(new Message("Error"));
        }
            
    }   
    
    
}
