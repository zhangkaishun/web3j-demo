package org.zks.match.match.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.zks.match.enums.OrderDirection;
import org.zks.match.match.MatchService;
import org.zks.match.match.MatchServiceFactory;
import org.zks.match.match.MatchStrategy;
import org.zks.match.model.ExchangeTrade;
import org.zks.match.model.MergeOrder;
import org.zks.match.model.Order;
import org.zks.match.model.OrderBooks;
import org.zks.match.rocket.Source;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;


/**
 * 限价单匹配
 */
@Service
@Slf4j
public class LimitPriceMatchServiceImpl implements MatchService , InitializingBean {
    @Autowired
    private Source source;

    @Override
    public void afterPropertiesSet() throws Exception {
        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE, this);
    }

    @Override
    public void match(OrderBooks orderBooks, Order order) {
            if(order.isCancelOrder()){
                orderBooks.cancelOrder(order);
                Message<String> message = MessageBuilder.withPayload(order.getOrderId()).build();
                source.cancelOrderOut().send(message);
                return; // 取消单的操作
            }
            if(order.getPrice().compareTo(BigDecimal.ZERO)<=0){
                return;
            }
            Iterator<Map.Entry<BigDecimal, MergeOrder>> markerQueueIterator = null;
            if(order.getOrderDirection()== OrderDirection.BUY){
                markerQueueIterator=orderBooks.getCurrentLimitPriceIterator(OrderDirection.SELL);
            }else {
                markerQueueIterator=orderBooks.getCurrentLimitPriceIterator(OrderDirection.BUY);

            }

            while (markerQueueIterator.hasNext()) {
                Map.Entry<BigDecimal, MergeOrder> entity = markerQueueIterator.next();
                BigDecimal price = entity.getKey();
                MergeOrder mergeOrder = entity.getValue();

                if(order.getOrderDirection()== OrderDirection.BUY && order.getPrice().compareTo(price)<0){
                    //如果订单是买 但是出的价格比目前挂单的价格小，则无法撇皮
                    break;
                }
                if(order.getOrderDirection()== OrderDirection.SELL &&order.getPrice().compareTo(price)>0){
                    //如果订单是卖单，但是卖单的价格，比目前挂的买单的价格大，则无法成交
                    break;
                }

                Iterator<Order> mergeOrderIterator = mergeOrder.iterator();
                while (mergeOrderIterator.hasNext()) {
                    Order maker = mergeOrderIterator.next();
                    ExchangeTrade exchangeTrade=processMath(order,maker,orderBooks);




                }


            }





    }

    /**
     *
     * @param taker 吃单
     * @param marker 挂单
     * @param orderBooks 交易记录
     * @return
     */

    private ExchangeTrade processMath(Order taker, Order marker, OrderBooks orderBooks) {
        //成交的价格
        BigDecimal dealPrice = marker.getPrice();
        //成交数量
        BigDecimal turnoverAmount=BigDecimal.ZERO;

        //计算本次需要的数量
        BigDecimal needAmount=calcTradeAmount(taker);
        //本次提供的数量
        BigDecimal providerAmount=calcTradeAmount(marker);
        turnoverAmount=needAmount.compareTo(providerAmount)>0?providerAmount:turnoverAmount;

        if (turnoverAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null; // 无法成交
        }

        //设置本次吃单的成交数据
        taker.setTradedAmount(taker.getTradedAmount().subtract(turnoverAmount));
        //买方成交金额
        BigDecimal turnoverTaker = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getCoinScale(), BigDecimal.ROUND_HALF_UP);





    }

    private BigDecimal calcTradeAmount(Order order) {
            return order.getAmount().subtract(order.getTradedAmount());
    }
}
