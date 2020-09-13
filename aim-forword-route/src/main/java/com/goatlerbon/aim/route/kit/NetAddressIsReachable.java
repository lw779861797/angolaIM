package com.goatlerbon.aim.route.kit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NetAddressIsReachable {
    /**
     * 检查 IP 和端口号
     *
     * @param address
     * @param port
     * @param timeout
     * @return 尝试连接 如果连接成功则返回成功
     */
    public static boolean checkAddressReachable(String address, int port, int timeout) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            int num = 0;
            boolean flag = false;
            while(num < 3){
                flag = socketChannel.connect(new InetSocketAddress(address, port));
                if(flag){
                   return true;
                }
                num++;
            }
            return false;

        }catch (IOException e){
            return false;
        }

    }
}
