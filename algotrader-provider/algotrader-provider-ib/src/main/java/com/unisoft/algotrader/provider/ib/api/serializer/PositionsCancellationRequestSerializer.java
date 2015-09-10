package com.unisoft.algotrader.provider.ib.api.serializer;


import com.unisoft.algotrader.provider.ib.api.model.system.OutgoingMessageId;

/**
 * Created by alex on 8/11/15.
 */
public class PositionsCancellationRequestSerializer extends Serializer{

    private static final int VERSION = 1;

    public PositionsCancellationRequestSerializer(int serverCurrentVersion) {
        super(serverCurrentVersion);
    }

    public byte[] serialize(long orderId) {
        ByteArrayBuilder builder = getByteArrayBuilder();

        builder.append(OutgoingMessageId.CANCEL_POSITIONS_REQUEST.getId());
        builder.append(VERSION);
        return builder.toBytes();
    }
}