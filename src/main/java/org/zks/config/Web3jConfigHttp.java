package org.zks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfigHttp {


            @Bean
            public Web3j web3j() {
                HttpService httpService = new HttpService(
                        "https://sepolia.infura.io/v3/f62f9fe36cc64e35a7d76e81e6226329"
                );
                return Web3j.build(httpService);

    }
}
