package com.chloe.io.accept;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ClassName: RedisServer
 * Package: com.chloe.io.accept
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/5 14:37
 * @Version 1.0
 */
public class RedisServer {
    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[1024];
        ServerSocket serverSocket = new ServerSocket(6379);
        while(true) {
            System.out.println("-----111 等待连接");
            Socket socket = serverSocket.accept();
            System.out.println("-----222 成功连接");
        }
    }
}
