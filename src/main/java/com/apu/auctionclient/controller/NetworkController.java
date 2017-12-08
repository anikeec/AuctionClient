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
import com.apu.auctionapi.query.RegistrationQuery;
import com.apu.auctionclient.client.Client;
import com.apu.auctionclient.client.ClientState;
import com.apu.auctionclient.entity.User;
import com.apu.auctionclient.utils.Decoder;
import com.apu.auctionclient.utils.Log;
import com.apu.auctionclient.utils.Time;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author apu
 */
public class NetworkController {
    
    private static final Log log = Log.getInstance();
    private final Class classname = NetworkController.class;
    
    private final Decoder decoder = Decoder.getInstance();
    
    private final User user;
    private final BlockingQueue<AuctionQuery> queriesQueue;
    private final BlockingQueue<AuctionQuery> sendedQueriesQueue;
    
    private int truePacketsValue = 0;

    public NetworkController(User user, 
                            BlockingQueue<AuctionQuery> queriesQueue, 
                            BlockingQueue<AuctionQuery> sendedQueriesQueue) {
        this.user = user;
        this.queriesQueue = queriesQueue;
        this.sendedQueriesQueue = sendedQueriesQueue;
    }
    
    public void handle(String queryStr) throws IOException, Exception {
        AuctionQuery query = decoder.decode(queryStr);      
        AuctionQuery srcQuery = getLastSendedQuery();
        
        if(query instanceof AnswerQuery) {
            if(srcQuery instanceof RegistrationQuery) {
                handle((RegistrationQuery)srcQuery, (AnswerQuery)query);
            } else {
                handle((AnswerQuery)query);
            }
        } else if(query instanceof DisconnectQuery) {
            handle((DisconnectQuery)query);
        } else if(query instanceof PingQuery) { 
            handle((PingQuery)query);
        } else if(query instanceof PollAnswerQuery) { 
            handle((PollAnswerQuery)query);
        } else {
            
        }        
        
        if(srcQuery != null) {
            if(query.getPacketId() == srcQuery.getPacketId()) {
                truePacketsValue++;
            }
            log.debug(classname, "" + truePacketsValue);
            removeLastSendedQuery();
        }
    }
    
    private AuctionQuery getLastSendedQuery() {
        return sendedQueriesQueue.peek();
    }
    
    private void removeLastSendedQuery() {
        sendedQueriesQueue.remove();
    }
    
    public void handle(AnswerQuery query) {
        log.debug(classname, "Answer query to controller");
        
    }
    
    public void handle(DisconnectQuery query) {
        log.debug(classname, "Disconnect query to controller");
        
    }  
    
    public void handle(PingQuery query) throws IOException {
        log.debug(classname, "Ping query to controller");
        
        String time = Time.getTime();
        AnswerQuery answer = 
            new AnswerQuery(query.getPacketId(), user.getUserId(), time, "Ping ask");
        queriesQueue.add(answer);
    }
    
    public void handle(PollAnswerQuery query) {
        log.debug(classname, "Poll answer query to controller");
        
    }
    
    public void handle(RegistrationQuery srcQuery, AnswerQuery answerQuery) {
        log.debug(classname, "Ask for registration query received");
        Client.setClientState(ClientState.CONNECTED);        
    }
    
}
