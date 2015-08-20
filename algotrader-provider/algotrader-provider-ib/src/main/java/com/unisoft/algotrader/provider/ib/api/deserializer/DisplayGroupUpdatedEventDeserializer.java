package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.IBSession;
import com.unisoft.algotrader.provider.ib.api.IncomingMessageId;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.readInt;
import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.readString;

/**
 * Created by alex on 8/13/15.
 */
public class DisplayGroupUpdatedEventDeserializer extends Deserializer {


    public DisplayGroupUpdatedEventDeserializer(int serverCurrentVersion){
        super(IncomingMessageId.DISPLAY_GROUP_UPDATED, serverCurrentVersion);
    }

    @Override
    public void consumeVersionLess(InputStream inputStream, IBSession ibSession) {
        final int requestId = readInt(inputStream);
        final String contractInfo = readString(inputStream);;
        
        ibSession.onDisplayGroupUpdated(requestId, contractInfo);
    }
}