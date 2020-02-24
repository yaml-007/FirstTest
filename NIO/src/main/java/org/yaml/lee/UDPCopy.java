package org.yaml.lee;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class UDPCopy {
    @Test
    public void send() throws IOException {
        DatagramChannel sendChannel = DatagramChannel.open();//发送端不需要绑定端口
        sendChannel.configureBlocking(false);

        RandomAccessFile sourceFile = new RandomAccessFile("src/main/resources/file/source.txt", "r");
        FileChannel fileChannel = sourceFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(buffer) >= 0) {
            buffer.flip();
            sendChannel.send(buffer,new InetSocketAddress("127.0.0.1",9000));
            buffer.clear();
        }
        //文末加入回车
        buffer.put("\r\n".getBytes());
        buffer.flip();
        sendChannel.send(buffer,new InetSocketAddress("127.0.0.1",9000));
        buffer.clear();

        fileChannel.close();
        sourceFile.close();
        sendChannel.close();
    }

    @Test
    public void receive() throws IOException {
        Selector selector = Selector.open();

        DatagramChannel receiveChannel = DatagramChannel.open();
        receiveChannel.configureBlocking(false);
        receiveChannel.bind(new InetSocketAddress(9000));
        receiveChannel.register(selector, SelectionKey.OP_READ);

        RandomAccessFile dstFile = new RandomAccessFile("src/main/resources/file/UDP.txt","rw");
        FileChannel fileChannel = dstFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                fileChannel.close();
                dstFile.close();
                receiveChannel.close();
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        while (selector.select()>0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isReadable()){
                    DatagramChannel channel = (DatagramChannel) selectionKey.channel();
                    dstFile.seek(dstFile.getFilePointer());//将position置于文件末尾，达到append的效果
                    receiveChannel.receive(buffer);
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }
                iterator.remove();
            }
        }
    }
}
