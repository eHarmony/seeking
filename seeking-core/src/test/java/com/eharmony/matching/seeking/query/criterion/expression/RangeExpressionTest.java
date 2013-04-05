package com.eharmony.matching.seeking.query.criterion.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Operator;

public class RangeExpressionTest {

    private final String propertyName = "propertyName";
    private final Integer from = 1;
    private final Integer to = 10;
    private final Operator operator = Operator.BETWEEN;

    private final RangeExpression e = new RangeExpression(operator,
            propertyName, from, to);

    @Test
    public void getOperator() {
        assertEquals(operator, e.getOperator());
    }

    @Test
    public void getPropertyName() {
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void getFrom() {
        assertEquals(from, e.getFrom());
    }

    @Test
    public void getTo() {
        assertEquals(to, e.getTo());
    }

    @Test
    public void testToString() {
        String expected = propertyName + " " + operator + " " + from + "," + to;
        assertEquals(expected, e.toString());
    }

}
