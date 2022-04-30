package tech.kuraudo.common.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import tech.kuraudo.common.message.Message;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List< Object > out) throws Exception {
        Message message = OBJECT_MAPPER.readValue(msg, Message.class);
        out.add(message);
    }
}
