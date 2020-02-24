package org.yaml.lee;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class FTPCopy {
    @Test
    public void client() throws IOException {
        SocketChannel clientChannel =
                SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
        clientChannel.configureBlocking(false);//切换非阻塞模式

        FileChannel fileChannel = FileChannel.open(Paths.get("src/main/resources/file/source.txt"),
                StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (fileChannel.read(buffer) >= 0) {
            buffer.flip();
            clientChannel.write(buffer);//将buffer写入channel
            buffer.clear();
        }
        //文末加入回车
        buffer.put("\r\n".getBytes());
        buffer.flip();
        clientChannel.write(buffer);
        buffer.clear();

        fileChannel.close();
        clientChannel.close();
    }

    @Test
    public void server() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);//切换非阻塞模式
        serverChannel.bind(new InetSocketAddress(9000));//绑定端口
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);//注册到selector，指定状态为“监听接收事件”
        //准备写入本地的通道和缓冲区
        FileChannel outChannel = FileChannel.open(Paths.get("src/main/resources/file/TCP.txt"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        ByteBuffer buffer = ByteBuffer.allocate(512);
        //为程序添加进程，当程序意外终止时，能做些释放资源等操作 对kill -9无效
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                outChannel.close();
                serverChannel.close();
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        //轮询获取选择器上已经准备就绪的事件
//        while (true) {
//            if (selector.select() == 0) continue;
        //select()具有阻塞唤醒机制，返回0是会进入阻塞状态，直到selector中注册到通道有可读写会重新唤醒此处
        while (selector.select() > 0) {//此句与注释掉的while(true) continue两句效果相同
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                if (selectionKey.isAcceptable()) {
                    SocketChannel acceptChannel = serverChannel.accept();//获取可接收的客户端通道
                    acceptChannel.configureBlocking(false);
                    acceptChannel.register(selector, SelectionKey.OP_READ);//注册到selector，状态为“读就绪”
                }
                if (selectionKey.isReadable()) {
                    SocketChannel readableChannel = (SocketChannel) selectionKey.channel();//获取注册的通道
                    while (readableChannel.read(buffer) >= 0) {
                        buffer.flip();
                        outChannel.write(buffer);
                        buffer.clear();
                    }
                }
                iterator.remove();//取消SelectionKey
            }
        }
    }
}