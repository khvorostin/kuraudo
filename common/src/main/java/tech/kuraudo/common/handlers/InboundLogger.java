package tech.kuraudo.common.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

/**
 * Логгер входящих сообщений. Класс является дочерним по отношению к {@link StringDecoder} и в текущей реализации
 * выводит в консоль конвертированный в строку полученный массив байтов.
 */
public class InboundLogger extends StringDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List< Object > out) throws Exception {
        super.decode(ctx, msg, out);
        if (out.toString().contains("LogMessage")) {
            System.out.println("INBOUND LOGGER: " + out);
        }
    }
}
