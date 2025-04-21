package org.zks.contract;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.zks.DemoApplication;
import org.zks.contracts.MyToken;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = DemoApplication.class)
public class ContractTest {

    @Autowired
    private Web3j web3j;;

    private static final Credentials credentials ;

    static {
        credentials = Credentials.create("0x6376ddbd009fba8fbde7d2dffc257830e4bd8890cbb34658e921abbbb9eda921");
    }

    @Test
    public void testDeploy() throws ExecutionException, InterruptedException {

        MyToken myToken = MyToken.deploy(web3j, credentials, new DefaultGasProvider(), BigInteger.valueOf(10000)).sendAsync().get();
        System.out.printf(JSONObject.toJSONString(myToken));

    }

    @Test
    public void testGetBalance() throws Exception {
        MyToken myToken = MyToken.load("0x50d7afe57a4cc2c5db9ce428787bc1b909000e73", web3j, credentials, new DefaultGasProvider());
        BigInteger bigInteger = myToken.balanceOf("0xA7D99625bafea059c6Ee80558C645eb0364FBeEA").send();
        System.out.printf(bigInteger.toString());
    }
}
