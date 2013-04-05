package com.eharmony.matching.seeking.query.criterion.expression;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Operator;
import com.google.common.base.Joiner;

public class SetExpressionTest {

    private final String propertyName = "propertyName";
    private final String[] testValues = { "test", "value", "array" };
    private final Operator operator = Operator.IN;

    private final SetExpression e = new SetExpression(operator,
            propertyName, testValues);

    @Test
    public void getOperator() {
        assertEquals(operator, e.getOperator());
    }

    @Test
    public void getPropertyName() {
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void getValues() {
        assertArrayEquals(testValues, e.getValues());
    }

    @Test
    public void testToString() {
        String expected = propertyName + " " + operator + " ["
                + Joiner.on(',').join(testValues) + "]";
        assertEquals(expected, e.toString());
    }
    
}
