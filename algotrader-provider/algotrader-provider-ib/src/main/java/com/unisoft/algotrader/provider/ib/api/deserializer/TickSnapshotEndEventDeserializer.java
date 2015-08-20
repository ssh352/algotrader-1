package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.IBConstants;
import com.unisoft.algotrader.provider.ib.api.IBSession;
import com.unisoft.algotrader.provider.ib.api.IncomingMessageId;

import java.io.InputStream;
import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.*;

/**
 * Created by alex on 8/13/15.
 */
public class TickSnapshotEndEventDeserializer extends Deserializer {


    public TickSnapshotEndEventDeserializer(int serverCurrentVersion){
        super(IncomingMessageId.TICK_SNAPSHOT_END, serverCurrentVersion);
    }

    @Override
    public void consumeVersionLess(InputStream inputStream, IBSession ibSession) {
        final int requestId = readInt(inputStream);
        
        ibSession.onTickSnapshotEndEvent(requestId);
    }
}