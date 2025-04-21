/*
package org.zks.config;

import okhttp3.OkHttpClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Configuration
public class Web3jConfig {



    @Bean
    public Web3j createWeb3j() throws Exception {
        String infuraUrl = "wss://sepolia.infura.io/ws/v3/f62f9fe36cc64e35a7d76e81e6226329";
        WebSocketClient webSocketClient = new WebSocketClient(new URI(infuraUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket opened");
            }

            @Override
            public void onMessage(String message) {
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

        webSocketClient.connectBlocking();
        WebSocketService webSocketService = new WebSocketService(webSocketClient, false);

        return Web3j.build(webSocketService);
    }
}
*/
