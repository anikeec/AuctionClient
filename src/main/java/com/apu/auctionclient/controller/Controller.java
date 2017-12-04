/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.controller;

import com.apu.auctionapi.AnswerQuery;
import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.DisconnectQuery;
import com.apu.auctionapi.PingQuery;
import com.apu.auctionapi.PollQuery;
import com.apu.auctionclient.entity.User;
import com.apu.auctionclient.utils.Coder;
import com.apu.auctionclient.utils.Decoder;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author apu
 */
public class Controller {
    
    private final Decoder decoder = Decoder.getInstance();
    private final Coder coder = Coder.getInstance();
    private Date date = null;
    
    private User user;

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
    
    public void handle(String queryStr) throws IOException, Exception {
        AuctionQuery query = decoder.decode(queryStr);
                
        if(query instanceof AnswerQuery) {
            handle((AnswerQuery)query);
        } else if(query instanceof DisconnectQuery) {
            handle((DisconnectQuery)query);
        } else if(query instanceof PingQuery) { 
            handle((PingQuery)query);
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
        
        date = new Date();
        String time = date.toString();
        AnswerQuery answer = 
            new AnswerQuery(query.getPacketId(), user.getUserId(), time, "Ping ask");
        String str = coder.code(answer);
        getUser().getOut().write(str);
        getUser().getOut().flush();
    }
    
    public void handle(PollQuery query) {
        
    }   
    
}
