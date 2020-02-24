package org.yaml.lee;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ThreadsCopy {

    @Test
    public void test() throws IOException {
        Pipe pipe = Pipe.open();

        //发送端
        Pipe.SinkChannel sinkChannel = pipe.sink();

        FileChannel inChannel =
                FileChannel.open(Paths.get("src/main/resources/file/source.txt"), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) >= 0) {
            buffer.flip();
            sinkChannel.write(buffer);
            buffer.clear();
        }
        //文末加入回车
        buffer.put("\r\n".getBytes());
        buffer.flip();
        sinkChannel.write(buffer);
        buffer.clear();

        inChannel.close();

        //接收端
        Pipe.SourceChannel sourceChannel = pipe.source();

        FileChannel outChannel = FileChannel.open(Paths.get("src/main/resources/file/threads.txt"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        ByteBuffer inBuffer = ByteBuffer.allocate(1024);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                outChannel.close();
                sourceChannel.close();
                sinkChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        while (sourceChannel.read(inBuffer) >= 0) {//linux下，Pipe.SourceChannel.read(Buffer)会阻塞
            inBuffer.flip();
            outChannel.write(inBuffer);
            inBuffer.clear();
        }
    }
}
