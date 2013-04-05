package com.eharmony.matching.seeking.query.criterion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Ordering.Order;

public class OrderingTest {

    private final String propertyName = "propertyName";

    @Test
    public void constructor() {
        Order order = Order.ASCENDING;
        Ordering o = new Ordering(propertyName, order);
        assertEquals(propertyName, o.getPropertyName());
        assertEquals(order, o.getOrder());
    }

    @Test
    public void asc() {
        Ordering o = Ordering.asc(propertyName);
        assertEquals(propertyName, o.getPropertyName());
        assertEquals(Order.ASCENDING, o.getOrder());
    }

    @Test
    public void desc() {
        Ordering o = Ordering.desc(propertyName);
        assertEquals(propertyName, o.getPropertyName());
        assertEquals(Order.DESCENDING, o.getOrder());
    }

}
