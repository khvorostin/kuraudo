package tech.kuraudo.common.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import tech.kuraudo.common.message.Message;

import java.util.List;

public class JsonEncoder extends MessageToMessageEncoder< Message > {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List< Object > out) throws Exception {
        out.add(OBJECT_MAPPER.writeValueAsString(msg));
    }
}
