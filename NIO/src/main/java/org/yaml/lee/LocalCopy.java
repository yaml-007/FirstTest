package org.yaml.lee;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocalCopy {
    @Test
    public void fileCopy() throws IOException {
        FileInputStream inputStream = new FileInputStream("./src/main/resources/file/source.txt");
        FileOutputStream outputStream = new FileOutputStream("./src/main/resources/file/local.txt");

        FileChannel inChannel = inputStream.getChannel();
        FileChannel outChannel = outputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (inChannel.read(buffer) >= 0) {
            buffer.flip();//缓冲区切换读模式！！！
            outChannel.write(buffer);
            buffer.clear();//缓冲区读写完成后需要清空！！！
        }

        outChannel.close();
        inChannel.close();
        outputStream.close();
        inputStream.close();
    }

    @Test
    //直接缓冲区，在物理内存上开辟空间映射文件，资源消耗大但速度快，适用于大文件传输，程序结束时间往往比实际传输时间要长
    public void fileDirectCopy() throws IOException {
        FileChannel inChannel = FileChannel
                .open(Paths.get("src/main/resources/file/source.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("./src/main/resources/file/direct.txt"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        inChannel.close();
        outChannel.close();
    }

    @Test
    public void scatterAndGatherCopy() throws IOException {
        RandomAccessFile inFile = new RandomAccessFile("src/main/resources/file/source.txt", "r");
        RandomAccessFile outFile =
                new RandomAccessFile("src/main/resources/file/ScatterAndGather.txt", "rw");
        FileChannel inChannel = inFile.getChannel();
        FileChannel outChannel = outFile.getChannel();

        ByteBuffer buffer1 = ByteBuffer.allocate(100), buffer2 = ByteBuffer.allocate(1024);
        ByteBuffer[] buffers = {buffer1, buffer2};

        while (inChannel.read(buffers) >= 0) {
            for (ByteBuffer buffer : buffers) {
                buffer.flip();
            }
            outChannel.write(buffers);
            for (ByteBuffer buffer : buffers) {
                buffer.clear();
            }
        }

        inChannel.close();
        outChannel.close();
        inFile.close();
        outFile.close();
    }
}
