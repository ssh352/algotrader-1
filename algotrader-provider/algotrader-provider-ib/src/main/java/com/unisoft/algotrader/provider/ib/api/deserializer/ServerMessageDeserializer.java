package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.event.IBEventHandler;
import com.unisoft.algotrader.provider.ib.api.model.system.IncomingMessageId;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.InputStreamUtils.readInt;
import static com.unisoft.algotrader.provider.ib.InputStreamUtils.readString;

/**
 * Created by alex on 8/13/15.
 */
public class ServerMessageDeserializer extends Deserializer {


    public ServerMessageDeserializer(int serverCurrentVersion){
        super(IncomingMessageId.SERVER_MESSAGE, serverCurrentVersion);
    }

    @Override
    public void consumeMessageContent(final int version, final InputStream inputStream, final IBEventHandler eventHandler) {
        if (version < VERSION_2) {
            final String message = readString(inputStream);
            eventHandler.onServerMessageEvent(-1, 0, message);
        } else {
            final int requestId = readInt(inputStream);
            final int code = readInt(inputStream);
            final String message = readString(inputStream);
            eventHandler.onServerMessageEvent(requestId, code, message);
        }
    }
}