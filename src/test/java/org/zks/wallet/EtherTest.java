package org.zks.wallet;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.zks.DemoApplication;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = DemoApplication.class)

public class EtherTest
{

    @Autowired
    private Web3j web3j;;

    private static final Credentials credentials ;

    static {
        //地址 0xA7D99625bafea059c6Ee80558C645eb0364FBeEA
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



    /**
     *
     * 转账
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TransactionException
     * @throws IOException
     */

    @Test
    public void testTransferEth() throws ExecutionException, InterruptedException, TransactionException, IOException {
        //✅ TransactionReceipt 是已经上链后的交易结果
        //当你调用 .send() 或 .sendAsync().get() 拿到 TransactionReceipt 时，Web3j 会自动等待交易被打包进区块，并返回对应结果，所以状态已经是最终的，不需要再轮询状态。
        TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j,
                credentials,
                "0x6CCBB96e7DdCd1f3EbE4E3a5D2e3a75e9A241Fd6",
                BigDecimal.valueOf(0.001)
                , Convert.Unit.ETHER).sendAsync().get();
        if (transactionReceipt.isStatusOK()) {
            System.out.println("转账成功 ✅");
        } else {
            System.out.println("转账失败 ❌，状态码: " + transactionReceipt.getStatus());
        }

        System.out.printf(JSONObject.toJSONString(transactionReceipt));

    }

    /**
     * 完全手动组装参数，发送交易
     *
     *  eth_sendTransaction
     * 功能：​发送一个未签名的交易对象。
     *
     * 适用场景：​当私钥由 Ethereum 客户端（如 Geth 或 Parity）控制时，客户端会自动对交易进行签名。
     *
     * 优点：​简化了交易过程，适合快速开发和测试。
     *
     * 缺点：​私钥必须由客户端管理，可能不适用于所有安全要求。​
     *
     * 🔹 eth_sendRawTransaction
     * 功能：​发送一个已签名的交易数据（Hex 格式）。
     *
     * 适用场景：​当私钥由外部工具或硬件钱包控制时，交易需要在客户端外部签名。
     *
     * 优点：​提供更高的安全性，私钥不暴露于客户端。
     *
     * 缺点：​需要手动签名交易，过程相对复杂。
     *
     *
     */
    @Test
    public void testTransferEther2() throws IOException, InterruptedException {
        // 获取当前 nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount("0xA7D99625bafea059c6Ee80558C645eb0364FBeEA", DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.printf("EthGetTransactionCount: %s\n", nonce);

        //获取当前gas price
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        System.out.println("EthGasPrice: " + gasPrice);

        //设置gas limit
        BigInteger gasLimit = BigInteger.valueOf(21000);

        //设置转账金额
        BigInteger value = Convert.toWei("0.0001", Convert.Unit.ETHER).toBigInteger();
        //构造交易
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, "0x6CCBB96e7DdCd1f3EbE4E3a5D2e3a75e9A241Fd6", value);
        //对交易签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        // 将签名后的交易编码为十六进制字符串
        String hexValue = Numeric.toHexString(signedMessage);

        //发送交易
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        // 获取交易哈希
        String transactionHash = ethSendTransaction.getTransactionHash();
        // 获取交易回执
        TransactionReceipt transactionReceipt = waitForTransactionReceipt(web3j, transactionHash);

        // 输出交易结果
        System.out.println("交易哈希: " + transactionHash);
        System.out.println("区块编号: " + transactionReceipt.getBlockNumber());
        System.out.println("交易状态: " + transactionReceipt.getStatus());
        System.out.println("Gas 使用量: " + transactionReceipt.getGasUsed());

        // 判断交易是否成功
        if ("0x1".equals(transactionReceipt.getStatus())) {
            System.out.println("✅ 转账成功");
        } else {
            System.out.println("❌ 转账失败");
        }

    }

    /**
     * 等待交易回执
     * @param web3j
     * @param transactionHash 交易哈希
     * @return
     */

    private TransactionReceipt waitForTransactionReceipt(Web3j web3j, String transactionHash) throws InterruptedException, IOException {
            TransactionReceipt transactionReceipt = null;

            while (transactionReceipt==null) {
                System.out.printf("Waiting for transaction hash: %s\n", transactionHash);
                Thread.sleep(3000);
                EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
                if (ethGetTransactionReceipt.getTransactionReceipt().isPresent()) {
                    transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().get();
                }
            }
            return transactionReceipt;

    }

    /**
     * EIP-1559 是以太坊在 2021 年 8 月伦敦硬分叉中引入的一项重要升级，旨在优化交易费用机制，提升用户体验，并增强以太坊的经济模型。​
     *
     *引入基础费用（Base Fee）机制
     * 每笔交易需支付由网络自动计算的基础费用，该费用根据网络拥堵程度动态调整。基础费用会被直接销毁（燃烧），不再进入矿工收入，从而减少 ETH 的流通供应。
     *
     * 新增小费（Priority Fee）机制
     * 用户可选择支付额外的小费，以激励矿工优先打包其交易。这部分费用直接支付给矿工，作为其收入来源。
     *
     * 可变区块大小
     * 为应对网络需求波动，EIP-1559 允许区块大小在目标大小的基础上上下浮动，以提高网络的吞吐能力。
     *
     *
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */

    @Test
    public void testTransferEthEIP1559() throws IOException, ExecutionException, InterruptedException {
        BigInteger gasLimit = BigInteger.valueOf(21000);
        //类似消费
        BigInteger maxPriorityFeePerGas = Convert.toWei("2", Convert.Unit.GWEI).toBigInteger(); // 优先费用
        //这是您愿意为每单位 Gas 支付的最高总费用
        BigInteger maxFeePerGas = Convert.toWei("50", Convert.Unit.GWEI).toBigInteger(); // 最大费用
        TransactionReceipt transactionReceipt = Transfer.sendFundsEIP1559(web3j, credentials, "0x6CCBB96e7DdCd1f3EbE4E3a5D2e3a75e9A241Fd6",
                BigDecimal.valueOf(0.0001),
                Convert.Unit.ETHER, gasLimit,
                maxPriorityFeePerGas, maxFeePerGas).sendAsync().get();
        System.out.println(JSONObject.toJSON(transactionReceipt));


    }

    /**
     * 获取特定区块的详细信息。
     * 分析区块中的交易数据。
     *
     * 还有一些其他函数，比如：  ethGetTransactionByHash  获取特定交易所在区块的哈希
     *
     */

    @Test
    public void testGetBlockByHash() throws IOException {
        //true：返回区块中每笔交易的完整详细信息，包括发送者、接收者、交易金额、Gas 使用等。
        //false：仅返回交易哈希列表，节省数据传输和处理时间。
        //获取最新区块信息
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send();
        System.out.printf("ethGetBlockByNumber:");
       // System.out.printf(JSONObject.toJSONString(ethBlock));
        //区块hash
        String hash = ethBlock.getBlock().getHash();
        System.out.println(hash);
        System.out.println(ethBlock.getBlock().getNumber());
        //根据区块hash 获取区块信息
        EthBlock send = web3j.ethGetBlockByHash(hash, true).send();
    }

}
