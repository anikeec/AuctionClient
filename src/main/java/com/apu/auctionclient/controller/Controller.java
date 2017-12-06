/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.controller;

import com.apu.auctionapi.answer.AnswerQuery;
import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.DisconnectQuery;
import com.apu.auctionapi.query.PingQuery;
import com.apu.auctionapi.answer.PollAnswerQuery;
import com.apu.auctionclient.entity.User;
import com.apu.auctionclient.utils.Coder;
import com.apu.auctionclient.utils.Decoder;
import com.apu.auctionclient.utils.Time;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class Controller {
    
    private final Decoder decoder = Decoder.getInstance();
    private final Coder coder = Coder.getInstance();
    
    private User user;
    private BlockingQueue<AuctionQuery> queriesQueue;
    private BlockingQueue<AuctionQuery> sendedQueriesQueue;

    private static Controller instance;
    
    private Controller() {
    }
    
    public static Controller getInstance() {
        if(instance == null)
            instance = new Controller();
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQueriesQueue(BlockingQueue<AuctionQuery> queriesQueue) {
        this.queriesQueue = queriesQueue;
    }
    
    public void setSendedQueriesQueue(BlockingQueue<AuctionQuery> sendedQueriesQueue) {
        this.sendedQueriesQueue = sendedQueriesQueue;
    }
    
    public void handle(String queryStr) throws IOException, Exception {
        AuctionQuery query = decoder.decode(queryStr);
        
        AuctionQuery srcQuery;
        srcQuery = sendedQueriesQueue.peek();
        if(srcQuery != null) {            
            sendedQueriesQueue.remove();
        }
                
        if(query instanceof AnswerQuery) {
            handle((AnswerQuery)query);
        } else if(query instanceof DisconnectQuery) {
            handle((DisconnectQuery)query);
        } else if(query instanceof PingQuery) { 
            handle((PingQuery)query);
        } else if(query instanceof PollAnswerQuery) { 
            handle((PollAnswerQuery)query);
        } else {
            
        }
    }
    
    public void handle(AnswerQuery query) {
        System.out.println("Answer query to controller");
        
    }
    
    public void handle(DisconnectQuery query) {
        System.out.println("Disconnect query to controller");
        
    }  
    
    public void handle(PingQuery query) throws IOException {
        System.out.println("Ping query to controller");
        
        String time = Time.getTime();
        AnswerQuery answer = 
            new AnswerQuery(query.getPacketId(), user.getUserId(), time, "Ping ask");
        queriesQueue.add(answer);
    }
    
    public void handle(PollAnswerQuery query) {
        System.out.println("Poll answer query to controller");
        
    }
    
}
