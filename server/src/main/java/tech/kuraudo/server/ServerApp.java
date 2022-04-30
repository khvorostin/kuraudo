package tech.kuraudo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import tech.kuraudo.common.handlers.InboundLogger;
import tech.kuraudo.common.handlers.JsonDecoder;
import tech.kuraudo.common.handlers.JsonEncoder;
import tech.kuraudo.common.handlers.OutboundLogger;

import java.net.InetSocketAddress;

/**
 * Сервер сетевого хранилища.
 */
public class ServerApp {

    private final static int PORT = 9000;

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup acceptor = new NioEventLoopGroup(1);
        NioEventLoopGroup client = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                .group(acceptor, client)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(PORT))
                .childHandler(new ChannelInitializer< NioSocketChannel >() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                            new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                            new LengthFieldPrepender(3),
                            new InboundLogger(),
                            new OutboundLogger(),
                            new JsonDecoder(),
                            new JsonEncoder(),
                            new ServerHandler()
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = serverBootstrap.bind().sync();
            System.out.println("Server started");
            future.channel().closeFuture().sync();
        } finally {
            acceptor.shutdownGracefully();
            client.shutdownGracefully();
        }
    }
}