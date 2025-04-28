package org.zks.match.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.openhft.affinity.AffinityThreadFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
@EnableConfigurationProperties(value = DisruptorProperties.class)
public class DisruptorAutoConfiguration {

    private DisruptorProperties disruptorProperties;

    public DisruptorAutoConfiguration(DisruptorProperties disruptorProperties) {
        disruptorProperties=disruptorProperties;
    }

    @Bean
    public EventFactory<OrderEvent> eventFactory() {
        EventFactory<OrderEvent> orderEventEventFactory=new EventFactory<OrderEvent>() {
            @Override
            public OrderEvent newInstance() {
                return new OrderEvent();
            }
        };
        return orderEventEventFactory;


    }

    @Bean
    public ThreadFactory threadFactory() {

        return new AffinityThreadFactory("disruptor-event-thread");
    }

    /**
     * 无锁高效的等待策略
     *
     * @return
     */
    @Bean
    public WaitStrategy waitStrategy() {
        return new YieldingWaitStrategy();
    }

    @Bean
    public RingBuffer<OrderEvent> ringBuffer(EventFactory<OrderEvent> eventFactory, ThreadFactory threadFactory, WaitStrategy waitStrategy, EventHandler<OrderEvent> eventEventHandlers) {
            Disruptor<OrderEvent> disruptor=null;
        ProducerType producerType = ProducerType.SINGLE;
        if(disruptorProperties.isMultiProducer()){
            producerType = ProducerType.MULTI;
        }

        disruptor=new Disruptor<>(eventFactory,disruptorProperties.getRingBufferSize(),threadFactory,producerType,waitStrategy);
        disruptor.setDefaultExceptionHandler(new DisruptorHandlerException());
        disruptor.handleEventsWith(eventEventHandlers);

        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        disruptor.start(); //开始监听


        final Disruptor<OrderEvent> disruptorShutdown = disruptor;

        // 使用优雅的停机
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    disruptorShutdown.shutdown();
                }, "DisruptorShutdownThread"
        ));
        return ringBuffer;

    }


    @Bean
    public DisruptorTemplate disruptorTemplate(RingBuffer<OrderEvent> ringBuffer) {
        return new DisruptorTemplate(ringBuffer);
    }

}
