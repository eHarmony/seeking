package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.mockito.cglib.beans.BeanMap;

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionNonHandler;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker;
import com.eharmony.matching.seeking.test.MockHibernatePropertyResolver;

public class HibernateQueryTranslatorTest {

    private final HibernatePropertyResolver resolver = new MockHibernatePropertyResolver();
    private final BoxMaker boxMaker = new SimpleBoxMaker();
    private final ContainsExpressionHandler<Criterion> containsExpressionHandler = new ContainsExpressionNonHandler<Criterion>();
    private final HibernateQueryTranslator translator = new HibernateQueryTranslator(resolver, boxMaker, containsExpressionHandler);
    
    // Test Values
    private final String property = "propertyName";
    private final String value = "test property value";
    private final Integer from = 1;
    private final Integer to = 10;
    private final String[] values = { "test", "value", "array" };
    private final Box<Integer> box = new Box<Integer>(new Point<Integer>(0, 0),
            new Point<Integer>(1, 1));
    
    private void assertEquivalent(Object a, Object b) {
        assertEquals(BeanMap.create(a), BeanMap.create(b));
    }
    
    @Test
    public void translate_NativeExpression_String() {
        String query = "some SQL query";
        NativeExpression e = new NativeExpression(String.class, query);
        Criterion expected = Restrictions.sqlRestriction("(" + query + ")");
        Criterion actual = translator.translate(e, Void.class);
        assertEquals(expected.toString(), actual.toString());
        assertEquivalent(expected, actual);
    }
    
    @Test
    public void translate_NativeExpression_Criterion() {
        Criterion query = Restrictions.eq("something", "or other");
        NativeExpression e = new NativeExpression(Criterion.class, query);
        assertEquivalent(query, translator.translate(e, Void.class));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void translate_NativeExpression_unsupported() {
        Integer query = 1;
        NativeExpression e = new NativeExpression(Integer.class, query);
        translator.translate(e, Void.class);
    }
    
    @Test
    public void eq() {
        assertEquivalent(Restrictions.eq(property, value), translator.eq(property, value));
    }
    
    @Test
    public void ne() {
        assertEquivalent(Restrictions.ne(property, value), 
                translator.ne(property, value));
    }
    
    @Test
    public void lt() {
        assertEquivalent(Restrictions.lt(property, value), 
                translator.lt(property, value));
    }
    
    @Test
    public void lte() {
        assertEquivalent(Restrictions.le(property, value), 
                translator.lte(property, value));
    }
    
    @Test
    public void gt() {
        assertEquivalent(Restrictions.gt(property, value), 
                translator.gt(property, value));
    }
    
    @Test
    public void gte() {
        assertEquivalent(Restrictions.ge(property, value), 
                translator.gte(property, value));
    }
    
    @Test
    public void between() {
        assertEquivalent(Restrictions.between(property, to, from), 
                translator.between(property, from, to));
    }
    
    @Test
    public void in() {
        assertEquivalent(Restrictions.in(property, values), 
                translator.in(property, values));
    }
    
    @Test
    public void notIn() {
        assertEquivalent(Restrictions.not(Restrictions.in(property, values)), 
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
        assertEquivalent(Restrictions.isNull(property), translator.isNull(property));
    }
    
    @Test
    public void notNull() {
        assertEquivalent(Restrictions.isNotNull(property), translator.notNull(property));
    }
    
    @Test
    public void isEmpty() {
        assertEquivalent(Restrictions.isEmpty(property), translator.isEmpty(property));
    }
    
    @Test
    public void notEmpty() {
        assertEquivalent(Restrictions.isNotEmpty(property), translator.notEmpty(property));
    }
    
    @Test
    public void within() {
        String xprop = "property.x";
        String yprop = "property.y";
        assertEquivalent(Restrictions.conjunction()
                .add(Restrictions.between(xprop, box.getA().getX(), box.getB().getX())) 
                .add(Restrictions.between(yprop, box.getA().getY(), box.getB().getY())
            ), translator.within(xprop, yprop, box));
    }
    
    @Test
    public void or() {
        Criterion o1 = Restrictions.eq(property, value);
        Criterion o2 = Restrictions.in(property, values);
        Criterion o3 = Restrictions.lt(property, value);
        Junction expected = Restrictions.disjunction().add(o1).add(o2).add(o3);
        assertEquivalent(expected, translator.or(o1, o2, o3));
    }
    
    @Test
    public void and() {
        Criterion o1 = Restrictions.eq(property, value);
        Criterion o2 = Restrictions.in(property, values);
        Criterion o3 = Restrictions.lt(property, value);
        Junction expected = Restrictions.conjunction().add(o1).add(o2).add(o3);
        assertEquivalent(expected, translator.and(o1, o2, o3));
    }
    
    @Test
    public void ordering_asc() {
        assertEquivalent(Order.asc(property),
                translator.order(property, Ordering.Order.ASCENDING).get().get(0));
    }
    
    @Test
    public void ordering_desc() {
        assertEquivalent(Order.desc(property),
                translator.order(property, Ordering.Order.DESCENDING).get().get(0));
    }
    
    @Test
    public void translateOrdering_many() {
        Order o1 = Order.asc("property1");
        Order o2 = Order.desc("property2");
        Order o3 = Order.asc("property3");
        Order o4 = Order.desc("property4");
        Order[] orders = new Order[] { o1, o2, o3, o4 };
        
        Orders l1 = new Orders(o1);
        Orders l2 = new Orders(o2);
        Orders l3 = new Orders(o3);
        Orders l4 = new Orders(o4);
        
        
        Orders merged = translator.order(l1, l2, l3, l4);
        for (int i = 1; i < orders.length; i++) {
            assertEquivalent(orders[i], merged.get().get(i));
        }
    }
    
    @Test
    public void translateProjection_null() {
        assertNull(translator.translateProjection(QueryBuilder.builderFor(String.class).build()));
    }
    
    @Test
    public void translateProjection_some() {
        ProjectionList expected = Projections.projectionList()
                .add(Projections.property("a")).add(Projections.property("b"))
                .add(Projections.property("c"));
        Projection actual = translator.translateProjection(QueryBuilder.builderFor(
                String.class, String.class, "a", "b", "c").build());
        assertArrayEquals(expected.getAliases(), actual.getAliases());
    }
}
