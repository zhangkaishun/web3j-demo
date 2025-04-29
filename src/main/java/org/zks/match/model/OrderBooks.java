package org.zks.match.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.zks.match.enums.OrderDirection;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Data
@Slf4j
public class OrderBooks {

    /**
     * 买入的限价交易 价格从高到底
     * eg: 价格越高，越容易买到
     * Key: 价格
     * MergeOrder 同价格的订单，订单按照时间排序
     */
    private TreeMap<BigDecimal, MergeOrder> buyLimitPrice;

    /**
     * 卖出的限价交易，价格从低到高
     * eg: 价格越低，卖出的越容易
     */
    private TreeMap<BigDecimal,MergeOrder> sellLimitPrice;

    /**
     * 交易的币种
     */
    private String symbol;

    /**
     * 交易币种的精度
     */
    private int coinScale;

    /**
     * 基币的精度
     */
    private int baseCoinScale;

    /**
     * 买方的盘口数据
     */
    private TradePlate buyTradePlate;

    /**
     * 卖方的盘口数据
     */
    private TradePlate sellTradePlate;

    /**
     * 日期格式器
     */
    private SimpleDateFormat dateTimeFormat;


    public OrderBooks(String symbol) {
        this(symbol,4,4);

    }
    public OrderBooks(String symbol, int coinScale, int baseCoinScale) {
        this.symbol = symbol;
        this.coinScale = coinScale;
        this.baseCoinScale = baseCoinScale;
        this.initialize();
    }

    public void initialize() {
        log.info("init CoinTrader for symbol {}", symbol);
        buyLimitPrice=new TreeMap<>(Comparator.reverseOrder());
        sellLimitPrice=new TreeMap<>(Comparator.naturalOrder());
        buyTradePlate = new TradePlate(symbol, OrderDirection.BUY);

        sellTradePlate = new TradePlate(symbol, OrderDirection.SELL);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void cancelOrder(Order order) {
        TreeMap<BigDecimal,MergeOrder> currentBuyLimitPrice=getCurrentLimitPrices(order.getOrderDirection());
        MergeOrder mergeOrder = currentBuyLimitPrice.get(order.getPrice());
        if(mergeOrder==null){
            return;
        }
        Iterator<Order> iterator = mergeOrder.iterator();
        while (iterator.hasNext()) {
            Order next = iterator.next();
            if(next.getOrderId().equals(order.getOrderId())){
                iterator.remove();
            }

        }
        if(mergeOrder.size()==0){
            currentBuyLimitPrice.remove(order.getPrice());
        }

        if(order.getOrderDirection()==OrderDirection.BUY){
            buyTradePlate.remove(order);
        }else {
            sellTradePlate.remove(order);
        }



    }

    private TreeMap<BigDecimal, MergeOrder> getCurrentLimitPrices(OrderDirection orderDirection) {
        if(OrderDirection.BUY.equals(orderDirection)){
            return buyLimitPrice;
        }
        return sellLimitPrice;
    }

    public Iterator<Map.Entry<BigDecimal, MergeOrder>> getCurrentLimitPriceIterator(OrderDirection orderDirection) {
        return getCurrentLimitPrices(orderDirection).entrySet().iterator();

    }
}
