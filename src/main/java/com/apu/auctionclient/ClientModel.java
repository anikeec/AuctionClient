/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.auctionclient;

/**
 *
 * @author apu
 */
public class ClientModel {
    private String lastTimeUpdate = "";
    private String state = "";
    private Integer lotId = 0;
    private String lotName = "";
    private Integer startPrice = 0;
    private Integer currentRate = 0;
    private String currentWinner = "";
    private Integer amountObservers = 0;
    private String answerTime = "";
    
    private static ClientModel instance;
    
    private ClientModel() {        
    }   
    
    public static ClientModel getInstance() {
        if(instance == null)
            instance = new ClientModel();
        return instance;
    }
    
    public final String getLastTimeUpdate() { 
        return this.lastTimeUpdate; 
    }

    public final void setLastTimeUpdate(String value) { 
        this.lastTimeUpdate = value; 
    }
    
    public final String getState() { 
        return this.state; 
    }

    public final void setState(String value) { 
        this.state = value; 
    }
    
    public final Integer getLotId() { 
        return this.lotId; 
    }

    public final void setLotId(Integer value) { 
        this.lotId = value; 
    }
    
    public final String getLotName() { 
        return this.lotName; 
    }

    public final void setLotName(String value) { 
        this.lotName = value; 
    }
    
    public final Integer getStartPrice() { 
        return this.startPrice; 
    }

    public final void setStartPrice(Integer value) { 
        this.startPrice = value; 
    }
    
    public final Integer getCurrentRate() { 
        return this.currentRate; 
    }

    public final void setCurrentRate(Integer value) { 
        this.currentRate = value; 
    }
    
    public final String getCurrentWinner() { 
        return this.currentWinner; 
    }

    public final void setCurrentWinner(String value) { 
        this.currentWinner = value; 
    }
    
    public final Integer getAmountObservers() { 
        return this.amountObservers; 
    }

    public final void setAmountObservers(Integer value) { 
        this.amountObservers = value; 
    }

    public void setAnswerTime(String time) {
        this.answerTime = time;
    }  
    
}
