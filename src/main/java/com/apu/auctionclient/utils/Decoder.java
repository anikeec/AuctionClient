/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.utils;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.NewRateQuery;
import com.apu.auctionapi.NotifyQuery;
import com.apu.auctionapi.PingQuery;
import com.apu.auctionapi.PollAnswerQuery;
import com.apu.auctionapi.PollQuery;
import com.apu.auctionapi.QueryType;
import com.apu.auctionapi.RegistrationQuery;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author apu
 */
public class Decoder {
    
    private String query;
    private final JsonParser parser = new JsonParser();
    private static Decoder instance;
    
    private Decoder() {
    }
    
    public static Decoder getInstance() {
        if(instance == null)
            instance = new Decoder();
        return instance;
    }
    
    private void decode(RegistrationQuery result) throws Exception {
        System.out.println("Registration packet");
    }
    
    private void decode(PingQuery result)  throws Exception {
        throw new Exception("Method has not ready yet");        
    }
    
    private void decode(NewRateQuery result)  throws Exception {
        throw new Exception("Method has not ready yet");        
    }
    
    private void decode(NotifyQuery result)  throws Exception {
        throw new Exception("Method has not ready yet");        
    }
    
    private void decode(PollQuery result)  throws Exception {
        throw new Exception("Method has not ready yet");        
    }
    
    private void decode(PollAnswerQuery result)  throws Exception {
         
    }
    
    public AuctionQuery decode(String query) throws Exception {
        this.query = query;       
        
        JsonElement jsonElement = parser.parse(query);
        JsonObject rootObject = jsonElement.getAsJsonObject();

        String queryType = rootObject.get("queryType").getAsString();
        String time = rootObject.get("time").getAsString();
        Long packetId = rootObject.get("packetId").getAsLong();
        Integer userId = rootObject.get("userId").getAsInt();        
        
        AuctionQuery result = null;
        if(queryType.equals(QueryType.NEW_RATE.toString())) {
            result = new NewRateQuery(packetId, userId, time);
            Decoder.this.decode((NewRateQuery)result);
        } else if(queryType.equals(QueryType.NOTIFY.toString())) {
            Decoder.this.decode((NotifyQuery)result);
        } else if(queryType.equals(QueryType.PING.toString())) {
            result = new PingQuery(packetId, userId, time);
            Decoder.this.decode((PingQuery)result);
        } else if(queryType.equals(QueryType.POLL_ANSWER.toString())) {
            result = new PollAnswerQuery(packetId, userId, time);
            Decoder.this.decode((PollAnswerQuery)result);
        } else if(queryType.equals(QueryType.POLL.toString())) {
            Decoder.this.decode((PollQuery)result);
        } else if(queryType.equals(QueryType.REGISTRATION.toString())) {
            result = new RegistrationQuery(packetId, userId, time);
            Decoder.this.decode((RegistrationQuery)result);
        }
        
        return result;
    }
}
