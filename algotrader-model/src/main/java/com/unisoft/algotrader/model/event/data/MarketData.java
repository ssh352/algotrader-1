package com.unisoft.algotrader.model.event.data;

import com.unisoft.algotrader.model.event.ReuseableEvent;

/**
 * Created by alex on 4/12/15.
 */
public abstract class MarketData<E extends MarketData<? super E>> implements ReuseableEvent<MarketDataHandler, E> {
    public long instId = -1;
    public long dateTime = -1;
    protected MarketData(){

    }
    public MarketData(long instId, long dateTime){
        this.instId = instId;
        this.dateTime = dateTime;
    }

    public void reset(){
        this.instId = -1;
        this.dateTime = -1;
    }
}
