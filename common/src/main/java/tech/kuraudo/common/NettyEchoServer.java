package tech.kuraudo.common;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.io.ByteArrayOutputStream;

public class NettyEchoServer {

    private final int port;

    public static void main(String[] args) throws InterruptedException {
        new NettyEchoServer(9000).start();
    }

    public NettyEchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup acceptor = new NioEventLoopGroup(1);
        NioEventLoopGroup client = new NioEventLoopGroup(); // кол-во ядер машины * 2
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(acceptor, client)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer< NioSocketChannel >() {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(
                            new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    final ByteBuf message = (ByteBuf) msg;
                                    while (message.isReadable()) {
                                        byte b = message.readByte();
                                        if ((char)b != '\n') {
                                            baos.write(b);
                                        } else {
                                            ByteBuf echo = Unpooled.wrappedBuffer(baos.toByteArray());
                                            baos.reset();
                                            ctx.channel().writeAndFlush(echo);
                                        }
                                    }
                                    ReferenceCountUtil.release(msg); // очистка буфера
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println(cause);
                                }
                            }
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // ограничение очереди заявок серверной очереди
                .childOption(ChannelOption.SO_KEEPALIVE, true); // поддерживать соединение живым

            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("Server started");
            future.channel().closeFuture().sync();
        } finally {
            acceptor.shutdownGracefully();
            client.shutdownGracefully();
        }
    }
}
