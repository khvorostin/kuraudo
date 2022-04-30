package tech.kuraudo.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tech.kuraudo.common.message.Message;
import tech.kuraudo.common.message.AuthMessage;
import tech.kuraudo.common.message.TextMessage;

/**
 * Обработчик входящих сообщений на стороне сервера. Уведомляет клиента о подключении к серверу, а также сообщает клиенту,
 * что сообщения от него получены.
 */
public class ServerHandler extends SimpleChannelInboundHandler< Message > {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TextMessage answer = new TextMessage();
        answer.setText("Successfully connection");
        ctx.writeAndFlush(answer);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println(msg);

        if (msg instanceof TextMessage textMessage) {
            TextMessage answer = new TextMessage();
            answer.setText("Text message [" + textMessage.getText() + "] received");
            ctx.writeAndFlush(msg);
        }

        if (msg instanceof AuthMessage authMessage) {
            TextMessage answer = new TextMessage();
            answer.setText("Auth data for " + authMessage.getLogin() + " received");
            ctx.writeAndFlush(answer);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnect");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
