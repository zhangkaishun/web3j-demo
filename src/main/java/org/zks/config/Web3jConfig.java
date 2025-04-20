package org.zks.config;

import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

import java.net.URI;

@Configuration
public class Web3jConfig {


    @Bean
    public Web3j web3j() throws Exception {
        //https://developer.metamask.io/ 到这个网站，可获取区块链节点，进行通信

        // 使用 WebSocket 协议连接到 Sepolia 测试网
        String infuraUrl = "wss://sepolia.infura.io/ws/v3/f62f9fe36cc64e35a7d76e81e6226329";

        // 创建 WebSocketClient
        WebSocketClient webSocketClient = new WebSocketClient(new URI(infuraUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket opened");
            }

            @Override
            public void onMessage(String message) {
                // 处理消息
                System.out.println("Received message: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket closed with exit code " + code + " and reason " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };


// 确保在 WebSocket 连接成功后再继续
        webSocketClient.connectBlocking(); // 阻塞直到连接成功
        // 使用 WebSocket 服务初始化 Web3j
        WebSocketService webSocketService = new WebSocketService(webSocketClient,false);

        return Web3j.build(webSocketService);
    }
}
