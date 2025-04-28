package org.zks.match.model;

import java.util.Iterator;
import java.util.LinkedList;

public class MergeOrder {
    private LinkedList<Order> orders=new LinkedList<>();

    public void add(Order order) {
        orders.addLast(order);
    }

    public Order getFirst() {
        return orders.getFirst();
    }

    public int size() {
        return orders.size();
    }

    public Iterator<Order> iterator() {
        return orders.iterator();
    }


}
