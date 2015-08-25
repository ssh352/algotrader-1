package com.unisoft.algotrader.provider.ib.api.event;

import com.unisoft.algotrader.provider.ib.api.IBConstants;

/**
 * Created by alex on 8/26/15.
 */
public class MarketDataTypeEvent extends IBEvent<MarketDataTypeEvent>  {

    public final IBConstants.MarketDataType marketDataType;

    public MarketDataTypeEvent(final String requestId, final IBConstants.MarketDataType marketDataType){
        super(requestId);
        this.marketDataType = marketDataType;
    }

}