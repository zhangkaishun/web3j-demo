package org.zks.match.disruptor;

import com.lmax.disruptor.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.zks.match.match.MatchServiceFactory;
import org.zks.match.match.MatchStrategy;
import org.zks.match.model.Order;
import org.zks.match.model.OrderBooks;

/**
 * 一种OrderEventHandler 处理一种交易对
 */
@Data
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {
    private OrderBooks orderBooks;;

    private String symbol;

    public OrderEventHandler(OrderBooks orderBooks) {
        this.orderBooks=orderBooks;
        symbol=orderBooks.getSymbol();

    }


    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        Order order= (Order)orderEvent.getSource();
        if(!order.getSymbol().equals(symbol)){
            // 我们接收到了一个不属于我们处理的数据,我们不处理
            return;
        }
        //目前写死限价
        MatchServiceFactory.getMatchService(MatchStrategy.LIMIT_PRICE).match(orderBooks ,order);


    }
}
