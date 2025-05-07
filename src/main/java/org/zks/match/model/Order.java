package org.zks.match.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.zks.match.enums.OrderDirection;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Order implements Serializable {

    /**
     * 本次订单的id
     */
    private String orderId;

    /**
     * 用户id
     */
    private Long user;


    /**
     * 支持的币币交易对
     */
    private String symbol;

    /**
     * 买入或卖出数量
     */
    private BigDecimal amount=BigDecimal.ZERO;

    /**
     * 成交量
     */
    private BigDecimal tradedAmount=BigDecimal.ZERO;

    /**
     * 成交额
     */
    private BigDecimal turnover=BigDecimal.ZERO;

    /**
     * 币单位
     */
    private String coinSymbol;


    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 订单方向
     */
    private OrderDirection  orderDirection;

    /**
     * 挂单价格
     */
    private BigDecimal price=BigDecimal.ZERO;

    /**
     * 挂单时间
     */
    private Long time;

    /**
     * 交易完成时间
     */
    private Long completedTime;

    /**
     * 交易取消时间
     */
    private Long cancelTime;
    private boolean isCancelOrder ;

    /**
     * 订单是否完成
     * @return
     */
    public boolean isCompleted(){
        return amount.compareTo(tradedAmount)<=0;
    }



}
