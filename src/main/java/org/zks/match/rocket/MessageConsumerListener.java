package org.zks.match.rocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.zks.match.disruptor.DisruptorTemplate;
import org.zks.match.model.EntrustOrder;
import org.zks.match.model.Order;
import org.zks.match.util.BeanUtils;

@Service
@Slf4j
public class MessageConsumerListener {
    @Autowired
    private DisruptorTemplate disruptorTemplate;

    @StreamListener("order_in")
    public void handleMessage(EntrustOrder entrustOrder) {
        Order order = null;
        if(entrustOrder.getStatus() ==2) {
            //该订单需要取消
            order = new Order();
            order.setOrderId(entrustOrder.getId().toString());
            order.setCancelOrder(true);
        }else {
            order= BeanUtils.entrustOrder2Order(entrustOrder);
        }
        disruptorTemplate.publish(order);

    }
}
