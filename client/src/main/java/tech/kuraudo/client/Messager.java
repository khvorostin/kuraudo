package tech.kuraudo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import tech.kuraudo.common.handlers.InboundLogger;
import tech.kuraudo.common.handlers.JsonDecoder;
import tech.kuraudo.common.handlers.JsonEncoder;
import tech.kuraudo.common.handlers.OutboundLogger;
import tech.kuraudo.common.message.Message;
import tech.kuraudo.common.message.TextMessage;

/**
 * Заготовка класса, отвечающего за сетевые взаимодействия на клиенте. С данными от пользователя работает через
 * пул сообщений {@link MessagePool}.
 */
public class Messager implements Runnable{

    private final String host;
    private final int port;
    private final MessagePool messagePool;

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
                                    if (msg instanceof TextMessage) {
                                        String msgText = ((TextMessage) msg).getText();
                                        if (msgText.equals("Successfully connection")) {
                                            messagePool.put(msg);
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
                Message message = messagePool.get();
                channel.writeAndFlush(message);
            }

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
