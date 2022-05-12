package tech.kuraudo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import tech.kuraudo.common.AppState;
import tech.kuraudo.common.handlers.InboundLogger;
import tech.kuraudo.common.handlers.JsonDecoder;
import tech.kuraudo.common.handlers.JsonEncoder;
import tech.kuraudo.common.handlers.OutboundLogger;
import tech.kuraudo.common.message.*;

import java.io.*;

/**
 * Класс, отвечающий за сетевые взаимодействия на клиенте. С данными от пользователя работает
 * через пул сообщений {@link MessagePool}.
 */
public class Messager implements Runnable{

    private RandomAccessFile accessFile;

    /**
     * Название блока кода, которое используется в пуле сообщений для обозначения получателей сообщений.
     */
    public static final String MODULE_NAME = Messager.class.getName();

    /**
     * Пул сообщений
     */
    private final MessagePool messagePool;

    /**
     * Путь к файлу на клиенту
     */
    String filePath = null;

    private final String host;
    private final int port;

    public Messager(String host, int port, MessagePool messagePool) {
        this.host = host;
        this.port = port;
        this.messagePool = messagePool;
    }

    public void run() {
        final NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer< SocketChannel >() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                            new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                            new LengthFieldPrepender(3),
                            new InboundLogger(),
                            new OutboundLogger(),
                            new JsonDecoder(),
                            new JsonEncoder(),
                            new SimpleChannelInboundHandler<Message>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

                                    if (msg instanceof LogMessage logMessage) {
                                        String log = logMessage.getLog();

                                        if (log.equals("Successfully connection")) {
                                            messagePool.put(Handler.MODULE_NAME, new AppStateMessage(AppState.CONNECTED));
                                        }

                                        if (log.startsWith("Auth data for")) {
                                            messagePool.put(Handler.MODULE_NAME, new AppStateMessage(AppState.AUTHORIZED));
                                        }

                                        if (log.startsWith("File") && log.contains("doesn't exists")) {
                                            messagePool.put(Handler.MODULE_NAME, new AppStateMessage(AppState.FAILURE));
                                        }
                                    }

                                    if (msg instanceof FileContentMessage fileContentMessage && filePath != null) {
                                        try {
                                            File file = new File(filePath);
                                            final RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
                                            accessFile.seek(fileContentMessage.getStartPosition());
                                            accessFile.write(fileContentMessage.getContent());
                                            if (fileContentMessage.isLast()) {
                                                // @todo разобраться:
                                                // @todo файл сохраняется, но пока клиент активен, файл нельзя удалить и превью в проводнике Windows недоступно
                                                // @todo файл как бы закрыт, но ресурс не освобождён
                                                accessFile.close();
                                                messagePool.put(Handler.MODULE_NAME, new AppStateMessage(AppState.SUCCESS));
                                            }
                                        } catch (Exception ex) {
                                            messagePool.put(Handler.MODULE_NAME, new AppStateMessage(AppState.FAILURE));
                                        }
                                    }
                                }
                            }
                        );
                    }
                });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            while (channel.isActive()) {
                Message message = messagePool.get(MODULE_NAME);

                // Данные для авторизации на сервере
                if (message instanceof AuthMessage authMessage) {
                    channel.writeAndFlush(authMessage);
                }

                // Запрос на загрузку файла с сервера
                if (message instanceof RequestToDownload requestToDownload) {
                    // запоминаем путь к файлу на клиенте
                    filePath = requestToDownload.getPathOnClient();
                    // запрашиваем файл с сервера
                    FileRequestMessage fileRequestMessage = new FileRequestMessage(requestToDownload.getPathOnServer());
                    channel.writeAndFlush(fileRequestMessage);
                }

                // Запрос на загрузку файла на сервер
                if (message instanceof RequestToUpload requestToUpload) {
                    final File file = new File(requestToUpload.getPathOnClient());
                    if (file.exists()) {
                        accessFile = new RandomAccessFile(file, "r");
                        sendFile(channel, requestToUpload.getPathOnServer());
                    }
                }
            }

            channel.closeFuture().sync();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    private void sendFile(Channel channel, String pathOnServer) throws IOException {
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
        UploadContentMessage message = new UploadContentMessage(pathOnServer, fileContent, startPosition, last);
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (!last) {
                sendFile(channel, pathOnServer);
            }
        });

        if (last) {
            accessFile.close();
            accessFile = null;
        }
    }
}
