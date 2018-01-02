/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.nw;

import com.apu.auctionapi.AuctionLotEntity;
import com.apu.auctionapi.answer.AnswerQuery;
import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.DisconnectQuery;
import com.apu.auctionapi.query.PingQuery;
import com.apu.auctionapi.answer.PollAnswerQuery;
import com.apu.auctionapi.query.RegistrationQuery;
import com.apu.auctionclient.ClientModel;
import com.apu.auctionclient.nw.client.Client;
import com.apu.auctionclient.nw.client.ClientState;
import com.apu.auctionclient.nw.entity.User;
import com.apu.auctionclient.nw.utils.Decoder;
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
        AuctionQuery answer = decoder.decode(queryStr);      
        AuctionQuery query = getLastSendedQuery();
        ClientModel.getInstance()
                .setAnswerTime(Time.getDelay(query.getTime(), Time.getTime()));//answer.getTime()
        
        if(answer instanceof AnswerQuery) {
            if(query instanceof RegistrationQuery) {
                handle((RegistrationQuery)query, (AnswerQuery)answer);
            } else if(query instanceof DisconnectQuery) {
                handle((DisconnectQuery)query, (AnswerQuery)answer);
            } else {
                handle((AnswerQuery)answer);
            }
        } else if(answer instanceof DisconnectQuery) {
            handle((DisconnectQuery)answer);
        } else if(answer instanceof PingQuery) { 
            handle((PingQuery)answer);
        } else if(answer instanceof PollAnswerQuery) { 
            handle((PollAnswerQuery)answer);
        } else {
            
        }        
        
        if(query != null) {
            if(answer.getPacketId() == query.getPacketId()) {
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
        ClientModel model = ClientModel.getInstance();
        if(query.getAuctionLots().size() > 0) {
            AuctionLotEntity lotEntity = query.getAuctionLots().get(0);
            model.setLotId(lotEntity.getLotId());
            model.setLotName(lotEntity.getLotName());
            model.setStartPrice(lotEntity.getStartPrice());        
            model.setCurrentRate(lotEntity.getLastRate()); 
            model.setCurrentWinner("" + lotEntity.getLastRateUserId());
            model.setAmountObservers(lotEntity.getAmountObservers());
        } else {
            model.setLotId(0);            
            model.setLotName("");
            model.setStartPrice(0);
            model.setCurrentRate(model.getStartPrice());        
            model.setCurrentWinner("0");
            model.setAmountObservers(0);
        }
        model.setLastTimeUpdate(query.getTime());
        log.debug(classname, "Poll answer query to controller");        
    }
    
    public void handle(RegistrationQuery srcQuery, AnswerQuery answerQuery) {
        log.debug(classname, "Answer for registration query received");
        Client.setClientState(ClientState.CONNECTED);        
    }
    
    public void handle(DisconnectQuery srcQuery, AnswerQuery answerQuery) {
        log.debug(classname, "Answer for disconnect query received");
        Client.setClientState(ClientState.DISCONNECTED);
    }
    
}
