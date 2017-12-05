/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient.utils;

import com.apu.auctionapi.AuctionQuery;
import com.apu.auctionapi.query.DisconnectQuery;
import com.apu.auctionapi.query.NewRateQuery;
import com.apu.auctionapi.query.PingQuery;
import com.apu.auctionapi.query.PollQuery;
import com.apu.auctionapi.query.RegistrationQuery;
import com.google.gson.Gson;

/**
 *
 * @author apu
 */
public class Coder {

    private Gson gson = new Gson();
    private static Coder instance;
    
    private Coder() {
    }
    
    public static Coder getInstance() {
        if(instance == null)
            instance = new Coder();
        return instance;
    }
    
    public String code(AuctionQuery object) {
        String ret = null;

        if(object == null)  
            return ret;
        object.setTime(Time.getTime()); 
        ret = gson.toJson(object) + "\r\n";
        
//        if(object instanceof DisconnectQuery) {
//            ret = code((DisconnectQuery)object);
//        } else if(object instanceof NewRateQuery) {
//            ret = code((NewRateQuery)object);
//        } else if(object instanceof PingQuery) { 
//            ret = code((PingQuery)object);
//        } else if(object instanceof PollQuery) { 
//            ret = code((PollQuery)object);
//        } else if(object instanceof RegistrationQuery) {
//            ret = code((RegistrationQuery)object);
//        } else {
//            
//        }
        
        return ret;
    }
    
    private String code(DisconnectQuery object) {
        String ret = null;
        
        return ret;
    }
    
    private String code(NewRateQuery object) {
        String ret = null;
        
        return ret;
    }
    
    private String code(PingQuery object) {
        String ret = null;
        if(object != null)
            ret = gson.toJson(object);
        return ret;
    }
    
    private String code(PollQuery object) {
        String ret = null;
        
        return ret;
    }
    
    private String code(RegistrationQuery object) {
        String ret = null;
        
        return ret;
    }
    
}
