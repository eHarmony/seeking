package com.eharmony.matching.seeking.query.criterion.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Operator;

public class UnaryExpressionTest {
    
    private final String propertyName = "propertyName";
    private final Operator operator = Operator.EMPTY;

    private final UnaryExpression e = new UnaryExpression(operator,
            propertyName);

    @Test
    public void getOperator() {
        assertEquals(operator, e.getOperator());
    }

    @Test
    public void getPropertyName() {
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void testToString() {
        String expected = propertyName + " " + operator;
        assertEquals(expected, e.toString());
    }

}
