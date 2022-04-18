package tech.kuraudo.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioEchoServer {

    private final String address = "localhost";
    private final int port = 9000;

    public static void main(String[] args) throws IOException {
        new NioEchoServer().start();
    }

    public void start() throws IOException {

        try (
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ) {
            serverSocketChannel.socket().bind(new InetSocketAddress(address, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Echo server started");

            while (true) {
                selector.select();
                System.out.println("New selector event");
                Set< SelectionKey > selectionKeys = selector.selectedKeys();
                Iterator< SelectionKey > iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        System.out.println("New selector acceptable event");
                        register(selector, serverSocketChannel);
                    }

                    if (selectionKey.isReadable()) {
                        System.out.println("New selector readable event");
                        readMessage(selectionKey);
                    }

                    iterator.remove();
                }
            }
        } finally {
            System.out.println("Echo server is down");
        }
    }

    public void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New client is connected");
    }

    public void readMessage(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        ByteBuffer echoByteBuffer = ByteBuffer.allocate(2048);
        byte nextByte;
        byte nByte = (char)'\n';
        int bytesRead = socketChannel.read(byteBuffer);
        while (bytesRead > -1) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                // читаем побайтово, если получен символ перевода строки,
                // возвращаем собранные данные данны
                nextByte = byteBuffer.get();
                if (nextByte != nByte) {
                    echoByteBuffer.put(nextByte);
                    System.out.print((char)nextByte);
                } else {
                    echoByteBuffer.rewind();
                    socketChannel.write(echoByteBuffer);
                    echoByteBuffer.clear();
                    System.out.println();
                }
            }

            byteBuffer.clear();
            bytesRead = socketChannel.read(byteBuffer);
        }
    }
}
