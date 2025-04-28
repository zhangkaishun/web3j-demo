package org.zks.match.model;

import org.zks.match.enums.OrderDirection;

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
}
