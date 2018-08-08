package com.ximuyi.akkaserver.net.netty;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyServiceTest {
    /*发送数据缓冲区*/
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    private Selector selector;

    @Test
    public void test0() throws IOException, InterruptedException {

        InetSocketAddress address = new InetSocketAddress("localhost", 5000);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(address);
        TimeUnit.SECONDS.sleep(2);
        socketChannel.close();
//        /*
//         * 轮询监听客户端上注册事件的发生
//         */
//        while (true) {
//            selector.select();
//            Set<SelectionKey> keySet = selector.selectedKeys();
//            for (final SelectionKey key : keySet) {
//                handle(key);
//            }
//            keySet.clear();
//        }
    }

    private void handle(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isConnectable()) {
            /*
             * 连接建立事件，已成功连接至服务器
             */
            SocketChannel client = (SocketChannel)selectionKey.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
                System.out.println("connect success !");
                sBuffer.clear();
                sBuffer.put((new Date() + " connected!").getBytes());
                sBuffer.flip();
                client.write(sBuffer);//发送信息至服务器
                /* 原文来自站长网
                 * 启动线程一直监听客户端输入，有信息输入则发往服务器端
                 * 因为输入流是阻塞的，所以单独线程监听
                 */
                new Thread(() -> {
                    while (true) {
                        try {
                            sBuffer.clear();
                            Scanner cin = new Scanner(System.in);
                            String sendText = cin.nextLine();
                            System.out.println(sendText);
                            /*
                             * 未注册WRITE事件，因为大部分时间channel都是可以写的
                             */
                            sBuffer.put(sendText.getBytes("utf-8"));
                            sBuffer.flip();
                            client.write(sBuffer);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }).start();
            }
            //注册读事件
            client.register(selector, SelectionKey.OP_READ);
        }
        else if (selectionKey.isReadable()) {
            /*
             * 读事件触发
             * 有从服务器端发送过来的信息，读取输出到屏幕上后，继续注册读事件
             * 监听服务器端发送信息
             */
            SocketChannel client = (SocketChannel)selectionKey.channel();
            rBuffer.clear();
            int count = client.read(rBuffer);
            if (count > 0) {
                String receiveText = new String(rBuffer.array(), 0, count);
                System.out.println(receiveText);
                client = (SocketChannel)selectionKey.channel();
                client.register(selector, SelectionKey.OP_READ);
            }
        }
    }
}
