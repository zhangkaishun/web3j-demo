package org.zks.match.match;

import com.lmax.disruptor.EventHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zks.match.disruptor.OrderEvent;
import org.zks.match.disruptor.OrderEventHandler;
import org.zks.match.model.MatchEngineProperties;
import org.zks.match.model.OrderBooks;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(value = MatchEngineProperties.class)
public class MatchEngineAutoConfiguration {

    private final MatchEngineProperties matchEngineProperties;

    public MatchEngineAutoConfiguration(MatchEngineProperties matchEngineProperties) {
        this.matchEngineProperties = matchEngineProperties;
    }

    @Bean("eventHandlers")
    public EventHandler<OrderEvent>[] getEventHandler() {
        Map<String, MatchEngineProperties.CoinScale> symbols = matchEngineProperties.getSymbols();
        EventHandler<OrderEvent>[] eventHandlers = new EventHandler[symbols.size()];
        Set<Map.Entry<String, MatchEngineProperties.CoinScale>> entries = symbols.entrySet();
        int i=0;
        for(Map.Entry<String, MatchEngineProperties.CoinScale> entry : entries) {
            String symbol = entry.getKey();
            MatchEngineProperties.CoinScale value = entry.getValue();
            OrderBooks orderBooks = null;
            if(value!=null) {
                orderBooks=new OrderBooks(symbol,value.getCoinScale(), value.getBaseCoinScale());
            }else {
                orderBooks=new OrderBooks(symbol);
            }
            eventHandlers[i++]=new OrderEventHandler(orderBooks);

        }
        return eventHandlers;

    }

}


