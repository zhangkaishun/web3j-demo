package org.zks.match.model;

import org.zks.match.enums.OrderDirection;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 交易的盘口数据
 */
public class TradePlate {
    //sell 队列： 从小达到
    //buy 队列： 从大到小

    private LinkedList<DepthItemVo> items = new LinkedList<>();

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
        remove(order, order.getAmount().subtract(order.getTradedAmount()));

    }

    public void remove(Order order, BigDecimal amount) {
        if (items.isEmpty()) {
            return;
        }
        if (order.getOrderDirection() != direction) {
            return;
        }
        Iterator<DepthItemVo> iterator = items.iterator();
        while (iterator.hasNext()) {
            DepthItemVo next = iterator.next();
            if (next.getPrice().equals(order.getPrice())) {
                next.setVolume(next.getVolume().subtract(amount));
                if (next.getVolume().compareTo(BigDecimal.ZERO) == 0) {
                    iterator.remove();
                }
            }
        }
    }

    public void add(Order order) {
        if (order.getOrderDirection() != direction) {
            return;
        }

        int i;
        for (i = 0; i < items.size(); i++) {
            DepthItemVo depthItemVo = items.get(i);
                //sell 队列： 从小达到
                //buy 队列： 从大到小
            if (
                    (direction == OrderDirection.BUY && order.getPrice().compareTo(depthItemVo.getPrice()) == -1) //买盘 订单价格比 当前盘口的某个价格小
                            ||
                            (direction == OrderDirection.SELL && order.getPrice().compareTo(depthItemVo.getPrice()) == 1) //卖盘 叮当价格比 当前盘口的某个价格大

            ) {
                continue;
            } else if (depthItemVo.getPrice().compareTo(order.getPrice()) == 0) {
                depthItemVo.setVolume(depthItemVo.getVolume().add(order.getAmount().subtract(order.getTradedAmount())));
            } else {
                break; // 我就想插入 当前我就在第 i
            }


        }
        if (i < maxDepth) {
            DepthItemVo depthItemVo = new DepthItemVo();
            depthItemVo.setPrice(order.getPrice());
            depthItemVo.setVolume(order.getAmount().subtract(order.getTradedAmount()));
            items.add(i, depthItemVo);
        }


    }
}
