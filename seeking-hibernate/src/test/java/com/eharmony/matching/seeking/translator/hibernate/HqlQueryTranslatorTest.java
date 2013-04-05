package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import org.junit.Test;

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionNonHandler;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker;
import com.eharmony.matching.seeking.test.MockHibernatePropertyResolver;
import com.google.common.base.Joiner;

public class HqlQueryTranslatorTest {

    private final HibernatePropertyResolver resolver = new MockHibernatePropertyResolver();
    private final BoxMaker boxMaker = new SimpleBoxMaker();
    private final ContainsExpressionHandler<String> containsExpressionHandler = new ContainsExpressionNonHandler<String>();
    private final HqlQueryTranslator translator = new HqlQueryTranslator(resolver, boxMaker, containsExpressionHandler);
    
    // Test Values
    private final String property = "propertyName";
    private final String value = "test property value";
    private final Integer from = 1;
    private final Integer to = 10;
    private final String[] values = { "test", "value", "array" };
    private final Box<Integer> box = new Box<Integer>(new Point<Integer>(0, 0),
            new Point<Integer>(1, 1));
    
    protected String join(String prop, HibernateOperator op) {
        return prop + " " + op.symbol();
    }
    
    protected String join(String prop, HibernateOperator op, Object val) {
        return join(prop, op) + " " + translator.string(val);
    }
    
    protected String join(String prop, HibernateOperator op, Object... vals) {
        StringBuilder b = new StringBuilder(join(prop, op));
        for (Object val : vals) {
            b.append(" ").append(translator.string(val));
        }
        return b.toString();
    }
    
    @Test
    public void eq() {
        assertEquals(join(property, HibernateOperator.EQUAL, value),
                translator.eq(property, value));
    }
    
    @Test
    public void ne() {
        assertEquals(join(property, HibernateOperator.NOT_EQUAL, value),
                translator.ne(property, value));
    }
    
    @Test
    public void lt() {
        assertEquals(join(property, HibernateOperator.LESS_THAN, value),
                translator.lt(property, value));
    }
    
    @Test
    public void lte() {
        assertEquals(join(property, HibernateOperator.LESS_THAN_OR_EQUAL, value),
                translator.lte(property, value));
    }
    
    @Test
    public void gt() {
        assertEquals(join(property, HibernateOperator.GREATER_THAN, value),
                translator.gt(property, value));
    }
    
    @Test
    public void gte() {
        assertEquals(join(property, HibernateOperator.GREATER_THAN_OR_EQUAL, value),
                translator.gte(property, value));
    }
    
    @Test
    public void between() {
        assertEquals(
                join(property, HibernateOperator.BETWEEN, from,
                        HibernateOperator.AND, to), translator.between(
                        property, from, to));
    }
    
    @Test
    public void in() {
        assertEquals(join(property, HibernateOperator.IN, (Object) values), 
                translator.in(property, values));
    }
    
    @Test
    public void notIn() {
        assertEquals(join(property, HibernateOperator.NOT_IN, (Object) values), 
                translator.notIn(property, values));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void contains() {
        translator.contains(property, values);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void contains_one() {
        translator.contains(property, new Object[] { value });
    }
    
    @Test
    public void isNull() {
        assertEquals(join(property, HibernateOperator.NULL), translator.isNull(property));
    }

    @Test
    public void notNull() {
        assertEquals(join(property, HibernateOperator.NOT_NULL), translator.notNull(property));
    }

    @Test
    public void isEmpty() {
        assertEquals(join(property, HibernateOperator.EMPTY), translator.isEmpty(property));
    }

    @Test
    public void notEmpty() {
        assertEquals(join(property, HibernateOperator.NOT_EMPTY), translator.notEmpty(property));
    }
    
    @Test
    public void within() {
        String xprop = "property.x";
        String yprop = "property.y";
        String expected = "(" + 
                join(xprop, HibernateOperator.BETWEEN, box.getA().getX(), HibernateOperator.AND, box.getB().getX()) +
                ") " + HibernateOperator.AND + " (" + 
                join(yprop, HibernateOperator.BETWEEN, box.getA().getY(), HibernateOperator.AND, box.getB().getY()) +
        ")";
        assertEquals(expected, translator.within(xprop, yprop, box));
    }
    
    @Test
    public void or_one() {
        assertEquals("", translator.or());
    }
    
    @Test
    public void or_many() {
        String q1 = "test this";
        String q2 = "test that";
        String q3 = "testing";
        String expected = "(" + q1 + ") or (" + q2 + ") or (" + q3 + ")";
        String actual = translator.or(q1, q2, q3);
        assertEquals(expected, actual);
    }
    
    @Test
    public void and_none() {
        assertEquals("", translator.and());
    }
    
    @Test
    public void and_one() {
        String q1 = "test this";
        assertEquals(q1, translator.and(q1));
    }
    
    @Test
    public void and_many() {
        String q1 = "test this";
        String q2 = "test that";
        String q3 = "testing";
        String expected = "(" + q1 + ") and (" + q2 + ") and (" + q3 + ")";
        String actual = translator.and(q1, q2, q3);
        assertEquals(expected, actual);
    }
    
    @Test
    public void string_String() {
        assertEquals("\"test string\"", translator.string("test string"));
    }
    
    @Test
    public void string_String_quotes() {
        assertEquals("\"\\\"test\\\" string\"", translator.string("\"test\" string"));
    }
    
    @Test
    public void string_Character() {
        assertEquals("'a'", translator.string('a'));
    }
    
    @Test
    public void string_CharacterQuote() {
        assertEquals("'\\''", translator.string('\''));
    }
    
    @Test
    public void string_Date() {
        Calendar c = Calendar.getInstance();
        // everything about java.util.Calendar is awful
        c.set(Calendar.YEAR, 2012);
        c.set(Calendar.MONTH, 2 - 1);
        c.set(Calendar.DATE, 21);
        c.set(Calendar.HOUR_OF_DAY, 14);
        c.set(Calendar.MINUTE, 34);
        c.set(Calendar.SECOND, 25);
        assertEquals("\"2012-02-21 14:34:25\"", translator.string(c.getTime()));
    }
    
    @Test
    public void ordering_asc() {
        assertEquals(property + " asc", translator.order(property, Order.ASCENDING));
    }
    
    @Test
    public void ordering_desc() {
        assertEquals(property + " desc", translator.order(property, Order.DESCENDING));
    }
    
    @Test
    public void translateOrdering_many() {
        String[] orders = new String[] { "property1 asc", "property2 desc",
                "property3 asc", "property4 desc" };
        String expected = Joiner.on(", ").join(orders);
        assertEquals(expected, translator.order(orders));
    }
    
    @Test
    public void translateProjection_null() {
        assertNull(translator.translateProjection(QueryBuilder.builderFor(String.class).build()));
    }
    
    @Test
    public void translateProjection_some() {
        assertEquals(
                "a,b,c",
                translator.translateProjection(QueryBuilder.builderFor(
                        String.class, String.class, "a", "b", "c").build()));
    }
}
