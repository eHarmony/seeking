package com.eharmony.matching.seeking.translator.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.translator.PropertyResolver;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoQueryTranslatorTest {
    
    private final PropertyResolver resolver = new MorphiaPropertyResolver();
    private final MongoQueryTranslator translator = new MongoQueryTranslator(resolver);
    
    // Test Values
    private final String property = "propertyName";
    private final String value = "test property value";
    private final Integer from = 1;
    private final Integer to = 10;
    private final String[] values = { "test", "value", "array" };
    private final double x = 1.0;
    private final double y = 2.0;
    private final double distance = 3.5;
    
    // DBObject Helper Methods
    protected DBObject o() {
        return new BasicDBObject();
    }
    
    protected DBObject o(String key, Object value) {
        return new BasicDBObject(key, value);
    }
    
    protected DBObject o(MongoOperator operator, Object value) {
        return o(operator.symbol(), value);
    }
    
    protected DBObject merge(DBObject... objects) {
        // merge into one object
        DBObject merged = o();
        for (DBObject o : objects) {
            for (String key: o.keySet()) {
                merged.put(key, o.get(key));
            }
        }
        return merged;
    }
    
    protected <T> Set<T> set(int capacity) {
        return new LinkedHashSet<T>(capacity);
    }
    
    protected Set<Object> set(Object... objects) {
        return MongoQueryTranslator.set(objects);
    }
    
    protected List<Object> list(Object... objects) {
        return MongoQueryTranslator.list(objects);
    }
    
    @Test
    public void translate_NativeExpression_Criterion() {
        DBObject query = o("something", "or other");
        NativeExpression e = new NativeExpression(DBObject.class, query);
        assertEquals(query, translator.translate(e, Void.class));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void translate_NativeExpression_unsupported() {
        Integer query = 1;
        NativeExpression e = new NativeExpression(Integer.class, query);
        translator.translate(e, Void.class);
    }
    
    @Test
    public void eq() {
        assertEquals(o(property, value), translator.eq(property, value));
    }
    
    @Test
    public void ne() {
        assertEquals(o(property, o(MongoOperator.NOT_EQUAL, value)), 
                translator.ne(property, value));
    }
    
    @Test
    public void lt() {
        assertEquals(o(property, o(MongoOperator.LESS_THAN, value)), 
                translator.lt(property, value));
    }
    
    @Test
    public void lte() {
        assertEquals(o(property, o(MongoOperator.LESS_THAN_OR_EQUAL, value)), 
                translator.lte(property, value));
    }
    
    @Test
    public void gt() {
        assertEquals(o(property, o(MongoOperator.GREATER_THAN, value)), 
                translator.gt(property, value));
    }
    
    @Test
    public void gte() {
        assertEquals(o(property, o(MongoOperator.GREATER_THAN_OR_EQUAL, value)), 
                translator.gte(property, value));
    }
    
    @Test
    public void between() {
        DBObject between = o();
        between.put(MongoOperator.GREATER_THAN_OR_EQUAL.symbol(), from);
        between.put(MongoOperator.LESS_THAN_OR_EQUAL.symbol(), to);
        assertEquals(o(property, between), 
                translator.between(property, from, to));
    }
    
    @Test
    public void in() {
        assertEquals(o(property, o(MongoOperator.IN, values)), 
                translator.in(property, values));
    }
    
    @Test
    public void notIn() {
        assertEquals(o(property, o(MongoOperator.NOT_IN, values)), 
                translator.notIn(property, values));
    }

    @Test
    public void contains() {
        Set<DBObject> objects = set(values.length);
        for (Object value : values) {
            objects.add(o(property, value));
        }
        assertEquals(o(MongoOperator.AND, objects), 
                translator.contains(property, values));
    }
    
    @Test
    public void contains_one() {
        assertEquals(o(property, value), 
                translator.contains(property, new Object[]{ value }));
    }
    
    @Test
    public void exists_true() {
        assertEquals(o(property, o(MongoOperator.EXISTS, 1)), 
                translator.exists(property, true));
    }
    
    @Test
    public void exists_false() {
        assertEquals(o(property, o(MongoOperator.EXISTS, 0)), 
                translator.exists(property, false));
    }
    
    @Test
    public void isNull() {
        assertEquals(o(MongoOperator.OR, set(
                o(property, o(MongoOperator.EXISTS, 0)),
                o(property, null)
        )), translator.isNull(property));
    }
    
    @Test
    public void notNull() {
        assertEquals(o(MongoOperator.AND, set(
                o(property, o(MongoOperator.EXISTS, 1)),
                o(property, o(MongoOperator.NOT_EQUAL, null))
        )), translator.notNull(property));
    }
    
    @Test
    public void isEmpty() {
        assertEquals(o(MongoOperator.OR, set(
                o(property, o(MongoOperator.EXISTS, 0)),
                o(property, "")
        )), translator.isEmpty(property));
    }
    
    @Test
    public void notEmpty() {
        assertEquals(o(MongoOperator.AND, set(
                o(property, o(MongoOperator.EXISTS, 1)),
                o(property, o(MongoOperator.NOT_EQUAL, ""))
        )), translator.notEmpty(property));
    }
    
    @Test
    public void within() {
        assertEquals(o(property, o(MongoOperator.WITHIN, o(MongoOperator.CENTER, 
                list(list(x, y), distance)
        ))), translator.within(property, new Point<Double>(x, y), distance));
    }
    
    @Test
    public void or_one() {
        DBObject o = o(property, value);
        assertEquals(o, translator.or(o));
    }
    
    @Test
    public void or_many() {
        DBObject o1 = o(property, value);
        DBObject o2 = o(property, o(MongoOperator.IN, values));
        DBObject o3 = o(property, o(MongoOperator.LESS_THAN, value));
        DBObject expected = o(MongoOperator.OR, set(o1, o2, o3));
        DBObject actual = translator.or(o1, o2, o3);
        assertEquals(expected, actual);
    }
    
    @Test
    public void and_one() {
        DBObject o = o(property, value);
        assertEquals(o, translator.and(o));
    }
    
    @Test
    public void and_many_sameProperty() {
        DBObject o1 = o(property, value);
        DBObject o2 = o(property, o(MongoOperator.IN, values));
        DBObject o3 = o(property, o(MongoOperator.LESS_THAN, value));
        DBObject expected = o(MongoOperator.AND, set(o1, o2, o3));
        DBObject actual = translator.and(o1, o2, o3);
        assertEquals(expected, actual);
    }
    
    @Test
    public void and_many_differentProperties() {
        DBObject o1 = o("property1", value);
        DBObject o2 = o("property2", o(MongoOperator.IN, values));
        DBObject o3 = o("property3", o(MongoOperator.LESS_THAN, value));
        DBObject expected = merge(o1, o2, o3);
        DBObject actual = translator.and(o1, o2, o3);
        assertEquals(expected, actual);
    }
    
    @Test
    public void and_mixed_differentProperties() {
        DBObject o1 = o(property, value);
        DBObject o2 = o(property, o(MongoOperator.IN, values));
        DBObject o3 = o(property, o(MongoOperator.LESS_THAN, value));
        DBObject o4 = o("property1", value);
        DBObject o5 = o("property2", o(MongoOperator.IN, values));
        DBObject o6 = o("property3", o(MongoOperator.LESS_THAN, value));
        
        DBObject o7 = o("property4", value);
        DBObject o8 = o("property4", o(MongoOperator.IN, values));
        DBObject and1 = o(MongoOperator.AND, set(o7, o8));
        
        DBObject o9 = o("property5", value);
        DBObject o10 = o("property5", o(MongoOperator.IN, values));
        DBObject and2 = o(MongoOperator.AND, set(o9, o10));
        
        DBObject expected = merge(o4, o5, o6);
        expected.put(MongoOperator.AND.symbol(), set(o1, o2, o3, o7, o8, o9, o10));
        
        DBObject actual = translator.and(o1, o2, o3, o4, o5, o6, and1, and2);
        assertEquals(expected, actual);
    }
    
    @Test
    public void ordering_asc() {
        assertEquals(o(property, 1), translator.order(property, Order.ASCENDING));
    }

    @Test
    public void ordering_desc() {
        assertEquals(o(property, -1), translator.order(property, Order.DESCENDING));
    }

    @Test
    public void translateOrdering_many() {
        DBObject o1 = o("property1", 1);
        DBObject o2 = o("property2", -1);
        DBObject o3 = o("property3", 1);
        DBObject o4 = o("property4", -1);
        DBObject expected = o();
        expected.put("property1", 1);
        expected.put("property2", -1);
        expected.put("property3", 1);
        expected.put("property4", -1);

        assertEquals(expected, translator.order(o1, o2, o3, o4));
    }
    
    public void translateProjection_null() {
        assertNull(translator.translateProjection(QueryBuilder
                .builderFor(String.class).build()));
    }
    
    @Test
    public void translateProjection_someWithId() {
        String[] properties = new String[] { "id", "name", "date", "thisProperty" };
        String[] fields = new String[] { "_id", "name", "date", "thatProperty" };
        DBObject expected = o();
        for (String f : fields) {
            expected.put(f, 1);
        }
        assertEquals(expected, translator.translateProjection(QueryBuilder.builderFor(
                TestClass.class, TestClass.class, properties).build()));
    }
    
    @Test
    public void translateProjection_someWithoutId() {
        String[] properties = new String[] { "name", "date", "thisProperty" };
        String[] fields = new String[] { "name", "date", "thatProperty" };
        DBObject expected = o();
        for (String f : fields) {
            expected.put(f, 1);
        }
        expected.put("_id", 0);
        assertEquals(expected, translator.translateProjection(QueryBuilder.builderFor(
                TestClass.class, TestClass.class, properties).build()));
    }

}
