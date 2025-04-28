package org.zks.match.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import org.zks.match.model.Order;

public class DisruptorTemplate {
        private static final EventTranslatorOneArg<OrderEvent, Order> TRANSLATOR=new EventTranslatorOneArg<OrderEvent, Order>() {
                @Override
                public void translateTo(OrderEvent orderEvent, long l, Order order) {
                        orderEvent.setSource(order);
                }
        };

        private final RingBuffer<OrderEvent> ringBuffer;


        public DisruptorTemplate(RingBuffer<OrderEvent> ringBuffer) {
                this.ringBuffer = ringBuffer;
        }

        public void publish(Order order) {
                ringBuffer.publishEvent(TRANSLATOR, order);
        }

}
