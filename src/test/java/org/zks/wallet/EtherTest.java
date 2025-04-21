package org.zks.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.zks.DemoApplication;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = DemoApplication.class)

public class EtherTest
{

    @Autowired
    private Web3j web3j;;

    private static final Credentials credentials ;

    static {
        credentials = Credentials.create("0x6376ddbd009fba8fbde7d2dffc257830e4bd8890cbb34658e921abbbb9eda921");
    }

    /**
     * 获取币
     */
    @Test
    public void testGetEther() throws ExecutionException, InterruptedException {
        //获取最新的区块号 ,确保查询余额时使用的是准确的区块状态
        BigInteger blockNumber = web3j.ethBlockNumber().sendAsync().get().getBlockNumber();
        DefaultBlockParameter defaultBlockParameter=DefaultBlockParameter.valueOf(blockNumber);
        EthGetBalance ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), defaultBlockParameter).sendAsync().get();
        System.out.printf("EthGetBalance: %s\n", ethGetBalance.getBalance());


        //默认使用当前区块来查询，但是可以指定查询某个区块上的余额
        EthGetBalance ethGetBalance2 = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        System.out.printf("EthGetBalance: %s\n", ethGetBalance2.getBalance());

    }


}
