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

    private boolean userAuthorized = false;
    private GateKeeper gateKeeper;

    public ServerHandler() {
        GateKeeper gateKeeper = new GateKeeper();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new LogMessage("Successfully connection"));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof RegMessage regMessage) {
            ctx.writeAndFlush(new LogMessage("Reg data for " + regMessage.getLogin() + " received"));
            if (gateKeeper.checkIfUserExists(regMessage.getLogin())) {
                ctx.writeAndFlush(new ActionResultMessage("Login already exists. Try another one"));
            } else if (gateKeeper.registerUser(regMessage.getLogin(), regMessage.getPassword(), regMessage.getLogin())) {
                ctx.writeAndFlush(new ActionResultMessage("New user register. Please log in to use Kuraudo"));
            } else {
                ctx.writeAndFlush(new ActionResultMessage("Something went wrong. Please, try again"));
            }
        }

        if (msg instanceof AuthMessage authMessage) {
            ctx.writeAndFlush(new LogMessage("Auth data for " + authMessage.getLogin() + " received"));
            if (gateKeeper.authorizeUser(authMessage.getLogin(), authMessage.getPassword())) {
                ctx.writeAndFlush(new ActionResultMessage("Success authorization"));
                userAuthorized = true;
            } else {
                ctx.writeAndFlush(new ActionResultMessage("Something went wrong. Please, try again"));
            }
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

        if (msg instanceof UploadContentMessage uploadContentMessage) {
            File file = new File(uploadContentMessage.getPath());
            final RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            accessFile.seek(uploadContentMessage.getStartPosition());
            accessFile.write(uploadContentMessage.getContent());
            if (uploadContentMessage.isLast()) {
                accessFile.close();
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
