package tech.kuraudo.server;

import io.netty.channel.*;
import tech.kuraudo.common.message.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Обработчик входящих сообщений на стороне сервера. Уведомляет клиента о подключении к серверу, а также сообщает клиенту,
 * что сообщения от него получены.
 */
public class ServerHandler extends SimpleChannelInboundHandler< Message > {

    private RandomAccessFile accessFile;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new LogMessage("Successfully connection"));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof AuthMessage authMessage) {
            ctx.writeAndFlush(new LogMessage("Auth data for " + authMessage.getLogin() + " received"));
        }

        if (msg instanceof FileRequestMessage fileRequestMessage) {
            String filePath = fileRequestMessage.getPath();
            ctx.writeAndFlush(new LogMessage("File request for " + filePath + " received"));
            final File file = new File(filePath);
            if (!file.exists()) {
                ctx.writeAndFlush(new LogMessage("File " + filePath + " + doesn't exists"));
            } else {
                accessFile = new RandomAccessFile(file, "r");
                sendFile(ctx);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnect");
        if (accessFile != null) {
            accessFile.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendFile(ChannelHandlerContext ctx) throws IOException {
        if (accessFile == null) {
            return;
        }

        final byte[] fileContent;
        final long available = accessFile.length() - accessFile.getFilePointer();

        if (available > 64 * 1024) {
            fileContent = new byte[64 * 1024];
        } else {
            fileContent = new byte[(int) available];
        }

        final long startPosition = accessFile.getFilePointer();
        accessFile.read(fileContent);
        final boolean last = (accessFile.getFilePointer() == accessFile.length());
        FileContentMessage message = new FileContentMessage(fileContent, startPosition, last);
        ctx.channel().writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (!last) {
                sendFile(ctx);
            }
        });

        if (last) {
            accessFile.close();
            accessFile = null;
        }
    }
}
