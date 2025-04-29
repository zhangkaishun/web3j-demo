package org.zks.match.model;

import org.zks.match.enums.OrderDirection;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 交易的盘口数据
 */
public class TradePlate {
        private LinkedList<DepthItemVo> items=new LinkedList<>();

        /**
         * 最大支持的深度
         */
        private int maxDepth = 100;

        /**
         * 订单的方向
         */
        private OrderDirection direction;

        /**
         * 交易对
         */
        private String symbol;


        public TradePlate(String symbol, OrderDirection direction) {
            this.symbol = symbol;
            this.direction = direction;
        }

        public void remove(Order order) {
                remove(order,order.getAmount().subtract(order.getTradedAmount()));

        }

        private void remove(Order order, BigDecimal amount) {
                if (items.isEmpty()) {
                        return;
                }
                if (order.getOrderDirection() != direction) {
                        return;
                }
                Iterator<DepthItemVo> iterator = items.iterator();
                while (iterator.hasNext()) {
                        DepthItemVo next = iterator.next();
                        if(next.getPrice().equals(order.getPrice())){
                                next.setVolume(next.getVolume().subtract(amount));
                                if(next.getVolume().compareTo(BigDecimal.ZERO)==0){
                                        iterator.remove();
                                }
                        }
                }


        }
}
