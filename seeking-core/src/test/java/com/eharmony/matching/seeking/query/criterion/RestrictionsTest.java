package com.eharmony.matching.seeking.query.criterion;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.expression.EqualityExpression;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.RangeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.SetExpression;
import com.eharmony.matching.seeking.query.criterion.expression.UnaryExpression;
import com.eharmony.matching.seeking.query.criterion.junction.Conjunction;
import com.eharmony.matching.seeking.query.criterion.junction.Disjunction;

public class RestrictionsTest {

    private final String propertyName = "propertyName";
    private final String value = "test property value";
    private final Integer from = 1;
    private final Integer to = 10;
    private final String[] values = { "test", "value", "array" };

    private final Criterion left = new Criterion() {
        @Override
        public String toString() {
            return "left";
        }
    };
    private final Criterion right = new Criterion() {
        @Override
        public String toString() {
            return "right";
        }
    };

    @Test
    public void eq() {
        EqualityExpression e = Restrictions.eq(propertyName, value);
        assertEquals(Operator.EQUAL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void ne() {
        EqualityExpression e = Restrictions.ne(propertyName, value);
        assertEquals(Operator.NOT_EQUAL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void lt() {
        EqualityExpression e = Restrictions.lt(propertyName, value);
        assertEquals(Operator.LESS_THAN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void lte() {
        EqualityExpression e = Restrictions.lte(propertyName, value);
        assertEquals(Operator.LESS_THAN_OR_EQUAL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void gt() {
        EqualityExpression e = Restrictions.gt(propertyName, value);
        assertEquals(Operator.GREATER_THAN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void gte() {
        EqualityExpression e = Restrictions.gte(propertyName, value);
        assertEquals(Operator.GREATER_THAN_OR_EQUAL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValue());
    }

    @Test
    public void between() {
        RangeExpression e = Restrictions.between(propertyName, from, to);
        assertEquals(Operator.BETWEEN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(from, e.getFrom());
        assertEquals(to, e.getTo());
    }
    
    @Test
    public void range() {
        assertArrayEquals(new Integer[] { 10, 11, 12, 13, 14, 15, 16, 17 },
                Restrictions.range(10, 17));
    }
    
    @Test
    public void range_one() {
        assertArrayEquals(new Integer[] { 10 }, Restrictions.range(10, 10));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void range_IllegalArgumentException() {
        Restrictions.range(17, 10);
    }
    
    @Test
    public void discreetRange() {
        SetExpression e = Restrictions.discreteRange(propertyName, from, to);
        assertEquals(Operator.IN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertArrayEquals(Restrictions.range(from, to), e.getValues());
    }
    
    @Test
    public void discreetRange_backwards() {
        SetExpression e = Restrictions.discreteRange(propertyName, to, from);
        assertEquals(Operator.IN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertArrayEquals(Restrictions.range(from, to), e.getValues());
    }

    @Test
    public void in_array() {
        SetExpression e = Restrictions.in(propertyName, values);
        assertEquals(Operator.IN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertArrayEquals(values, e.getValues());
    }

    @Test
    public void in_collection() {
        SetExpression e = Restrictions.in(propertyName, Arrays.asList(values));
        assertEquals(Operator.IN, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertArrayEquals(values, e.getValues());
    }

    @Test
    public void contains_one() {
        SetExpression e = Restrictions.contains(propertyName, value);
        assertEquals(Operator.CONTAINS, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertEquals(value, e.getValues()[0]);
        assertEquals(1, e.getValues().length);
    }

    @Test
    public void contains_many() {
        SetExpression e = Restrictions.contains(propertyName, values);
        assertEquals(Operator.CONTAINS, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
        assertArrayEquals(values, e.getValues());
    }

    @Test
    public void isNull() {
        UnaryExpression e = Restrictions.isNull(propertyName);
        assertEquals(Operator.NULL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void isNotNull() {
        UnaryExpression e = Restrictions.isNotNull(propertyName);
        assertEquals(Operator.NOT_NULL, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void isEmpty() {
        UnaryExpression e = Restrictions.isEmpty(propertyName);
        assertEquals(Operator.EMPTY, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
    }

    @Test
    public void isNotEmpty() {
        UnaryExpression e = Restrictions.isNotEmpty(propertyName);
        assertEquals(Operator.NOT_EMPTY, e.getOperator());
        assertEquals(propertyName, e.getPropertyName());
    }
    
    public void nativeQuery() {
        String expression = "my native expression";
        NativeExpression e = Restrictions.nativeQuery(String.class, expression);
        assertEquals(expression, e.getExpression());
    }

    @Test
    public void and() {
        Conjunction and = Restrictions.and(left, right);
        List<Criterion> criteria = and.getCriteria();
        assertEquals(Operator.AND, and.getOperator());
        assertEquals(2, criteria.size());
        assertEquals(left, criteria.get(0));
        assertEquals(right, criteria.get(1));

    }

    @Test
    public void or() {
        Disjunction or = Restrictions.or(left, right);
        List<Criterion> criteria = or.getCriteria();
        assertEquals(Operator.OR, or.getOperator());
        assertEquals(2, criteria.size());
        assertEquals(left, criteria.get(0));
        assertEquals(right, criteria.get(1));
    }

}
