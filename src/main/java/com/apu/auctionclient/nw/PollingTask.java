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
import java.util.concurrent.BlockingQueue;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author apu
 */
public class PollingTask implements Runnable {
    
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
        while(true) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            log.debug(classname,ExceptionUtils.getStackTrace(ex));
        }
        if(queriesQueue.remainingCapacity() > queriesQueue.size() - 1) { 
            queriesQueue.add(new PollQuery(user.getUserId()));
            log.debug(classname, "PollingTask Thread. Put to queue.");
        } else {
            log.debug(classname, "PollingTask Thread. Queue is full.");
//            messagesQueue.add(new Message("Error"));
        }
        }   
    }   
    
    
}
