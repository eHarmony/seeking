package com.eharmony.matching.seeking.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.query.criterion.expression.Distance2dExpression;
import com.eharmony.matching.seeking.query.criterion.expression.EqualityExpression;
import com.eharmony.matching.seeking.query.criterion.expression.Expression;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.RangeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.SetExpression;
import com.eharmony.matching.seeking.query.criterion.expression.UnaryExpression;
import com.eharmony.matching.seeking.query.criterion.junction.Conjunction;
import com.eharmony.matching.seeking.query.criterion.junction.Disjunction;
import com.eharmony.matching.seeking.query.criterion.junction.Junction;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.google.common.base.Joiner;

public class AbstractQueryTranslatorTest {

    private final String property = "propertyName";
    private final String field = "resolvedFieldName";
    private final String testValue = "test property value";
    private final Integer testFrom = 1;
    private final Integer testTo = 10;
    private final String[] testValues = { "test", "value", "array" };
    private final double x = 1.0;
    private final double y = 2.0;
    private final double distance = 3.5;

    private final Class<Test> entityClass = Test.class;
    private final PropertyResolver resolver = new PropertyResolver() {
        @Override
        public String resolve(String fieldName, Class<?> entityClass) {
            return property.equals(fieldName) ? field : fieldName;
        }
    };
    private final AbstractQueryTranslator<String, String, String> translator = new AbstractQueryTranslator<String, String, String>(
            String.class, String.class, resolver) {

        @Override
        public String eq(String fieldName, Object value) {
            return join(Operator.EQUAL, fieldName, value);
        }

        @Override
        public String ne(String fieldName, Object value) {
            return join(Operator.NOT_EQUAL, fieldName, value);
        }

        @Override
        public String lt(String fieldName, Object value) {
            return join(Operator.LESS_THAN, fieldName, value);
        }

        @Override
        public String lte(String fieldName, Object value) {
            return join(Operator.LESS_THAN_OR_EQUAL, fieldName, value);
        }

        @Override
        public String gt(String fieldName, Object value) {
            return join(Operator.GREATER_THAN, fieldName, value);
        }

        @Override
        public String gte(String fieldName, Object value) {
            return join(Operator.GREATER_THAN_OR_EQUAL, fieldName, value);
        }

        @Override
        public String between(String fieldName, Object from, Object to) {
            return join(Operator.BETWEEN, fieldName, from, to);
        }

        @Override
        public String in(String fieldName, Object[] values) {
            return join(Operator.IN, fieldName, values);
        }

        @Override
        public String notIn(String fieldName, Object[] values) {
            return join(Operator.NOT_IN, fieldName, values);
        }

        @Override
        public String contains(String fieldName, Object[] values) {
            return join(Operator.CONTAINS, fieldName, values);
        }

        @Override
        public String isNull(String fieldName) {
            return join(Operator.NULL, fieldName);
        }

        @Override
        public String notNull(String fieldName) {
            return join(Operator.NOT_NULL, fieldName);
        }

        @Override
        public String isEmpty(String fieldName) {
            return join(Operator.EMPTY, fieldName);
        }

        @Override
        public String notEmpty(String fieldName) {
            return join(Operator.NOT_EMPTY, fieldName);
        }

        @Override
        public String and(String... subqueries) {
            return Joiner.on(" and ").join(subqueries);
        }

        @Override
        public String or(String... subqueries) {
            return Joiner.on(" or ").join(subqueries);
        }

        @Override
        public String order(String fieldName, Order o) {
            return fieldName + " "
                    + (Order.ASCENDING.equals(o) ? "asc" : "desc");
        }

        @Override
        public String order(String... orders) {
            return Joiner.on(", ").join(orders);
        }

        @Override
        public <N extends Number & Comparable<N>> String within(
                String fieldName, Point<N> center, Number distance) {
            return join(Operator.WITHIN, fieldName, center.getX(), center.getY(), distance);
        }

        @Override
        public <T, R> String translateProjection(Query<T, R> query) {
            return query.getReturnFields().size() > 0 ? join(query.getReturnFields()) : null;
        }

        @Override
        protected <T> String translate(NativeExpression e, Class<T> entityClass) {
            return e.getExpression().toString();
        }
    };
    
    private String join(Object... parts) {
        return Joiner.on(" ").join(parts);
    }
    
    private String join(Iterable<?> parts) {
        return Joiner.on(" ").join(parts);
    }
    
    private Query<Test, Test> query(Criterion criteria) {
        return QueryBuilder.builderFor(entityClass).add(criteria).build();
    }
    
    private Query<Test, Test> orderQuery(Ordering... orderings) {
        return QueryBuilder.builderFor(entityClass).addOrder(orderings).build();
    }
    
    
    /*-------------------------------------------------------------------------- 
     * Verification helper methods
     */
    public void verify_translateQuery(Query<Test, Test> q, String expected) {
        assertEquals(expected, translator.translate(q));
    }
    
    public void verify_translate_Criterion(Criterion c, String expected) {
        assertEquals(expected, translator.translate(c, entityClass));
        verify_translateQuery(query(c), expected);
    }
    
    public String verify_translate_Junction(Junction j, String separator) {
        j.addAll(
                Restrictions.eq(property, testValue),
                Restrictions.between(property, testFrom, testTo),
                Restrictions.in(property, testValues),
                Restrictions.isNotNull(property)
        );
        String expected = Joiner.on(separator).join(translator.subqueries(j, entityClass));
        
        assertEquals(expected, translator.translate(j, entityClass));
        verify_translate_Criterion(j, expected);
        
        return expected;
    }
    
    public void verify_translate_Expression(Expression e, String expected) {
        assertEquals(expected, translator.translate(e, entityClass));
        verify_translate_Criterion(e, expected);
    }
    
    public String verify_translate_EqualityExpression(Operator operator) {
        EqualityExpression e = new EqualityExpression(operator, property, testValue);
        String expected = join(operator, field, testValue);
        
        assertEquals(expected, translator.translate(e, field));
        verify_translate_Expression(e, expected);
        return expected;
    }
    
    public String verify_translate_RangeExpression(Operator operator) {
        RangeExpression e = new RangeExpression(operator, property, testFrom, testTo);
        String expected = join(operator, field, testFrom, testTo);
        
        assertEquals(expected, translator.translate(e, field));
        verify_translate_Expression(e, expected);
        return expected;
    }
    
    public String verify_translate_SetExpression(Operator operator) {
        SetExpression e = new SetExpression(operator, property, testValues);
        String expected = join(operator, field, testValues);
        
        assertEquals(expected, translator.translate(e, field));
        verify_translate_Expression(e, expected);
        return expected;
    }
    
    public String verify_translate_UnaryExpression(Operator operator) {
        UnaryExpression e = new UnaryExpression(operator, property);
        String expected = join(operator, field);
        
        assertEquals(expected, translator.translate(e, field));
        verify_translate_Expression(e, expected);
        return expected;
    }
    
    public String verify_translate_Distance2dExpression(Operator operator) {
        Distance2dExpression<Double> e = new Distance2dExpression<Double>(operator, property, x, y, distance);
        String expected = join(operator, field, x, y, distance);
        
        assertEquals(expected, translator.translate(e, entityClass));
        verify_translate_Expression(e, expected);
        return expected;
    }
    
    /*--------------------------------------------------------------------------
     * Junction Tests
     */
    @Test
    public void translate_Conjunction() {
        Conjunction j = new Conjunction();
        String expected = verify_translate_Junction(j, " and ");
        assertEquals(expected, translator.translate(j, entityClass));
    }
    
    @Test
    public void translate_Disjunction() {
        Disjunction j = new Disjunction();
        String expected = verify_translate_Junction(j, " or ");
        assertEquals(expected, translator.translate(j, entityClass));
    }
    
    @Test
    public void subqueries() {
        Junction j = new Conjunction(
                Restrictions.eq(property, testValue),
                Restrictions.between(property, testFrom, testTo),
                Restrictions.in(property, testValues),
                Restrictions.isNotNull(property)
        );
        String[] subqueries = translator.subqueries(j, entityClass);
        
        List<Criterion> criteria = j.getCriteria();
        assertEquals(criteria.size(), subqueries.length);
        
        for (int i = 0; i < subqueries.length; i++) {
            assertEquals(translator.translate(criteria.get(i), entityClass), subqueries[i]);
        }
    }
    
    /*--------------------------------------------------------------------------
     * EqualityExpression Tests
     */
    @Test
    public void translate_EqualityExpression_EQUAL() {
        String expected = verify_translate_EqualityExpression(Operator.EQUAL);
        assertEquals(expected, translator.eq(field, testValue));
    }
    
    @Test
    public void translate_EqualityExpression_NOT_EQUAL() {
        String expected = verify_translate_EqualityExpression(Operator.NOT_EQUAL);
        assertEquals(expected, translator.ne(field, testValue));
    }
    
    @Test
    public void translate_EqualityExpression_GREATER_THAN() {
        String expected = verify_translate_EqualityExpression(Operator.GREATER_THAN);
        assertEquals(expected, translator.gt(field, testValue));
    }
    
    @Test
    public void translate_EqualityExpression_GREATER_THAN_OR_EQUAL() {
        String expected = verify_translate_EqualityExpression(Operator.GREATER_THAN_OR_EQUAL);
        assertEquals(expected, translator.gte(field, testValue));
    }
    
    @Test
    public void translate_EqualityExpression_LESS_THAN() {
        String expected = verify_translate_EqualityExpression(Operator.LESS_THAN);
        assertEquals(expected, translator.lt(field, testValue));
    }
    
    @Test
    public void translate_EqualityExpression_LESS_THAN_OR_EQUAL() {
        String expected = verify_translate_EqualityExpression(Operator.LESS_THAN_OR_EQUAL);
        assertEquals(expected, translator.lte(field, testValue));
    }
    
    /*--------------------------------------------------------------------------
     * RangeExpression Tests
     */
    @Test
    public void translate_RangeExpression_BETWEEN() {
        String expected = verify_translate_RangeExpression(Operator.BETWEEN);
        assertEquals(expected, translator.between(field, testFrom, testTo));
    }
    
    /*--------------------------------------------------------------------------
     * SetExpression Tests
     */
    @Test
    public void translate_SetExpression_IN() {
        String expected = verify_translate_SetExpression(Operator.IN);
        assertEquals(expected, translator.in(field, testValues));
    }
    
    @Test
    public void translate_SetExpression_NOT_IN() {
        String expected = verify_translate_SetExpression(Operator.NOT_IN);
        assertEquals(expected, translator.notIn(field, testValues));
        
    }
    
    @Test
    public void translate_SetExpression_CONTAINS() {
        String expected = verify_translate_SetExpression(Operator.CONTAINS);
        assertEquals(expected, translator.contains(field, testValues));
    }
    
    /*-------------------------------------------------------------------------- 
     * UnaryExpression Tests
     */
    
    @Test
    public void translate_UnaryExpression_NULL() {
        String expected = verify_translate_UnaryExpression(Operator.NULL);
        assertEquals(expected, translator.isNull(field));        
    }
    
    @Test
    public void translate_UnaryExpression_NOT_NULL() {
        String expected = verify_translate_UnaryExpression(Operator.NOT_NULL);
        assertEquals(expected, translator.notNull(field));
    }
    
    @Test
    public void translate_UnaryExpression_EMPTY() {
        String expected = verify_translate_UnaryExpression(Operator.EMPTY);
        assertEquals(expected, translator.isEmpty(field));
    }
    
    @Test
    public void translate_UnaryExpression_NOT_EMPTY() {
        String expected = verify_translate_UnaryExpression(Operator.NOT_EMPTY);
        assertEquals(expected, translator.notEmpty(field));
    }
    
    /*--------------------------------------------------------------------------
     * Distance2dExpression Tests
     */
    @Test
    public void translate_Distance2dExpression_WITHIN() {
        String expected = verify_translate_Distance2dExpression(Operator.WITHIN);
        assertEquals(expected, translator.within(field, new Point<Double>(x, y), distance));
    }
    
    /*--------------------------------------------------------------------------
     * NativeExpression Tests
     */
    @Test
    public void translate_NativeExpression() {
        String expression = "property & 4 > 0";
        NativeExpression e = new NativeExpression(String.class, expression);
        assertEquals(expression, translator.translate(query(e)));
        assertEquals(expression, translator.translate(e, entityClass));
    }
    
    /*--------------------------------------------------------------------------
     * UnsupportedOperationException Tests
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_Junction() {
        translator.translate(new Junction(Operator.EQUAL){}, entityClass);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_Criterion() {
        translator.translate(new Criterion(){}, entityClass);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_Expression() {
        translator.translate(new Expression(Operator.EQUAL, field){}, entityClass);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_EqualityExpression() {
        translator.translate(new EqualityExpression(Operator.BETWEEN, property, testValue), field);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_RangeExpression() {
        translator.translate(new RangeExpression(Operator.EQUAL, property, testFrom, testTo), field);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_SetExpression() {
        translator.translate(new SetExpression(Operator.EQUAL, property, testValues), field);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_UnaryExpression() {
        translator.translate(new UnaryExpression(Operator.EQUAL, property), field);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_Distance2dExpression() {
        translator.translate(new Distance2dExpression<Double>(Operator.EQUAL, property, x, y, distance), entityClass);
    }
    
    /*--------------------------------------------------------------------------
     * Ordering Tests
     */
    @Test
    public void translateOrdering_one_asc() {
        Ordering o = Ordering.asc(property);
        String expected = field + " asc";
        assertEquals(expected, translator.translateOrder(orderQuery(o)));
    }
    
    @Test
    public void translateOrdering_one_desc() {
        Ordering o = Ordering.desc(property);
        String expected = field + " desc";
        assertEquals(expected, translator.translateOrder(orderQuery(o)));
    }
    
    @Test
    public void translateOrdering_many() {
        Query<Test,Test> orderQuery = orderQuery(
                Ordering.asc("property1"),
                Ordering.desc("property2"),
                Ordering.asc("property3"),
                Ordering.desc("property4")
            );
        String expected = "property1 asc, property2 desc, property3 asc, property4 desc";
        assertEquals(expected, translator.translateOrder(orderQuery));
    }
    
    /*--------------------------------------------------------------------------
     * Projection Tests
     */
    @Test
    public void translateProjection_null() {
        assertNull(translator.translateProjection(QueryBuilder.builderFor(String.class).build()));
    }
    
    @Test
    public void translateProjection_some() {
        assertEquals(
                "a b c",
                translator.translateProjection(QueryBuilder.builderFor(
                        String.class, String.class, "a", "b", "c").build()));
    }
}
