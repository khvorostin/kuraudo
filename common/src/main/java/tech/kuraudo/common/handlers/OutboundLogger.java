package tech.kuraudo.common.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

/**
 * Логгер входящих сообщений. Класс является дочерним по отношению к {@link StringEncoder} и в текущей реализации
 * выводит в консоль исходящее сообщение в виде строки.
 */
public class OutboundLogger extends StringEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List< Object > out) throws Exception {
        if (msg.toString().contains("LogMessage")) {
            System.out.println("OUTBOUND LOGGER: " + msg);
        }

        super.encode(ctx, msg, out);
    }
}
