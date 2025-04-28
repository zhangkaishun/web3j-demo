package org.zks.match.disruptor;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderEvent implements Serializable {
    private final long timestamp;
    protected transient Object source;

    public OrderEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public OrderEvent(Object source) {
        this.timestamp = System.currentTimeMillis();
        this.source = source ;
    }
}
