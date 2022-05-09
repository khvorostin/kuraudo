package tech.kuraudo.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tech.kuraudo.common.message.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Обработчик входящих сообщений на стороне сервера. Уведомляет клиента о подключении к серверу, а также сообщает клиенту,
 * что сообщения от него получены.
 */
public class ServerHandler extends SimpleChannelInboundHandler< Message > {

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
                try (final RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
                    while (accessFile.getFilePointer() != accessFile.length()) {
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
                        ctx.writeAndFlush(new FileContentMessage(fileContent, startPosition, last));
                    }
                    accessFile.getFilePointer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
