package org.zks.disruptortest;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

public class DisruptorTest {
    public static void main(String[] args) {

        LongEventFactory longEventFactory=new LongEventFactory();
        int bufferSize=1024;
        Disruptor<LongEvent> disruptor = new Disruptor<>(longEventFactory,
                bufferSize, Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
        //disruptor 设置多个 handleEventsWith ，默认并行处理
        //返回的 EventHandlerGroup 可用于继续设置后续的处理器依赖关系
        LongEventHandler longEventHandler = new LongEventHandler();
        EventHandlerGroup<LongEvent> longEventEventHandlerGroup = disruptor.handleEventsWith(longEventHandler);


        // 定义事件处理器
        EventHandler<LongEvent> handlerA = (event, sequence, endOfBatch) -> {
            System.out.println("Handler A: " + event.get());
        };

        EventHandler<LongEvent> handlerB = (event, sequence, endOfBatch) -> {
            System.out.println("Handler B: " + event.get());
        };

        //这样设置 LongEventHandler handlerA handlerB 并行处理

        //
        //每次调用 handleEventsWith(...) 方法时：
       // Disruptor 都会新建一个独立的消费者组（EventHandlerGroup）；
        //每个组可以包含多个 EventHandler；
       // 各组之间默认是 并行消费 RingBuffer 中的事件；
       // disruptor.handleEventsWith(handlerA);
       // disruptor.handleEventsWith(handlerB);


        //这样设置 LongEventHandler，handlerA handlerB 并行运行
        //handleEventsWith 将 handlerA 添加到当前组中，与已有 handler 并行执行
       // longEventEventHandlerGroup.handleEventsWith(handlerA);
        //longEventEventHandlerGroup.handleEventsWith(handlerB);

        //TODO 待确认顺序
        //longEventEventHandlerGroup.handleEventsWith(handlerA).handleEventsWith(handlerB);

        // 发布某个sequence 这个事件 必须先让 LongEventHandler 先处理完成，然后handlerA handlerB才能并发执行
        //表示 handlerA 必须 在当前组处理完事件之后串行执行
       // longEventEventHandlerGroup.then(handlerA);
      //  longEventEventHandlerGroup.then(handlerB);

        //longEventHandler先处理事件，handler A 在 longEventHandler处理完事件之后再处理
        //disruptor.after(longEventHandler).handleEventsWith(handlerA);

        //and 连接的两个 EventHandlerGroup  并行处理，并会返回一个新的 EventHandlerGroup
        // longEventHandler 与 handlerA 处理完成后，再执行 handlerB
        EventHandlerGroup<LongEvent> and = longEventEventHandlerGroup.and(disruptor.handleEventsWith(handlerA));
        and.then(handlerB);

        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        for(long l=0;l<1000;l++){
            long next = ringBuffer.next();
            try{
                ringBuffer.get(next).set(l);
            }finally {
                ringBuffer.publish(next);
            }
        }

    }


}

// 1. 定义事件类
class LongEvent {
    private long value;
    public void set(long value) { this.value = value; }
    public long get() { return value; }
}

// 2. 定义事件工厂
class LongEventFactory implements EventFactory<LongEvent> {
    public LongEvent newInstance() {
        return new LongEvent();
    }
}

// 3. 定义事件处理器
class LongEventHandler implements EventHandler<LongEvent> {
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Event: " + event.get());
    }
}


