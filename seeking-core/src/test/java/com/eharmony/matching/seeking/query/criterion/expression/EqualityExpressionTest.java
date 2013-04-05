package com.eharmony.matching.seeking.query.criterion.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Operator;

public class EqualityExpressionTest {

    private final String propertyName = "propertyName";
    private final String testValue = "test value";
    private final Operator operator = Operator.EQUAL;

    private final EqualityExpression e = new EqualityExpression(operator,
            propertyName, testValue);

    @Test
    public void getOperator() {
        assertEquals(operator, e.getOperator());
    }

    @Test
    public void getPropertyName() {
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void getValue() {
        assertEquals(testValue, e.getValue());
    }

    @Test
    public void testToString() {
        String expected = propertyName + " " + operator + " " + testValue;
        assertEquals(expected, e.toString());
    }

}
