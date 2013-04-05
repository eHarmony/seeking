package com.eharmony.matching.seeking.translator.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.translator.PropertyResolver;
import com.eharmony.matching.seeking.translator.SimplePropertyResolver;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class SolrQueryTranslatorTest {
    
    private final PropertyResolver resolver = new SimplePropertyResolver();
    private final SolrQueryTranslator translator = new SolrQueryTranslator(resolver);
    
    // Test Values
    private final String property = "propertyName";
    private final String value = "test property value";
    private final Integer from = 1;
    private final Integer to = 10;
    private final String[] values = { "test", "value", "array" };
    private final double x = 1.0;
    private final double y = 2.0;
    private final double distance = 3.5;
    
    private final String opTO =  " " + SolrOperator.TO + " ";
    private final String opOR =  " " + SolrOperator.OR + " ";
    private final String opAND =  " " + SolrOperator.AND + " ";
    private final String opALL =  "[* TO *]";
    
    
    protected String quoted(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }
    
    protected List<String> quoted(String... strings) {
        return Lists.transform(Arrays.asList(strings), new Function<String, String>() {
            @Override
            public String apply(String arg0) {
                return quoted(arg0);
            }
            
        });
    }
    
    protected String join(Object... values) {
        return Joiner.on("").join(values);
    }
    
    protected String fq(String field, String q) {
        return join(field, SolrOperator.EQUAL, q);
    }
    
    @Test
    public void eq() {
        assertEquals(fq(property, quoted(value)),
                translator.eq(property, value));
    }
    
    @Test
    public void ne() {
        assertEquals(fq(SolrOperator.NEGATION + property, quoted(value)),
                translator.ne(property, value));
    }
    
    @Test
    public void lt() {
        assertEquals(fq(property, join(SolrOperator.OPEN_INCLUSIVE, SolrOperator.WILDCARD, opTO, quoted(value), SolrOperator.CLOSE_EXCLUSIVE)),
                translator.lt(property, value));
    }
    
    @Test
    public void lte() {
        assertEquals(fq(property, join(SolrOperator.OPEN_INCLUSIVE, SolrOperator.WILDCARD, opTO, quoted(value), SolrOperator.CLOSE_INCLUSIVE)),
                translator.lte(property, value));
    }
    
    @Test
    public void gt() {
        assertEquals(fq(property, join(SolrOperator.OPEN_EXCLUSIVE, quoted(value), opTO, SolrOperator.WILDCARD, SolrOperator.CLOSE_INCLUSIVE)),
                translator.gt(property, value));
    }
    
    @Test
    public void gte() {
        assertEquals(fq(property, join(SolrOperator.OPEN_INCLUSIVE, quoted(value), opTO, SolrOperator.WILDCARD, SolrOperator.CLOSE_INCLUSIVE)),
                translator.gte(property, value));
    }
    
    @Test
    public void between() {
        assertEquals(fq(property, join(SolrOperator.OPEN_INCLUSIVE, from, opTO, to, SolrOperator.CLOSE_INCLUSIVE)),
                translator.between(property, from, to));
    }
    
    @Test
    public void in() {
        assertEquals(fq(property, join("(", Joiner.on(opOR).join(quoted(values)), ")")), 
                translator.in(property, values));
    }
    
    @Test
    public void notIn() {
        assertEquals(fq(SolrOperator.NEGATION + property, join("(", Joiner.on(opOR).join(quoted(values)), ")")), 
                translator.notIn(property, values));
    }
    
    @Test
    public void contains() {
        assertEquals(fq(property, join("(", Joiner.on(opAND).join(quoted(values)), ")")), 
                translator.contains(property, values));
    }
    
    @Test
    public void contains_one() {
        assertEquals(fq(property, quoted(value)), 
                translator.contains(property, new Object[] { value }));
    }
    
    @Test
    public void isNull() {
        assertEquals(fq(SolrOperator.NEGATION + property, opALL), translator.isNull(property));
    }

    @Test
    public void notNull() {
        assertEquals(fq(property, opALL), translator.notNull(property));
    }

    @Test
    public void isEmpty() {
        assertEquals(fq(SolrOperator.NEGATION + property, opALL), translator.isEmpty(property));
    }

    @Test
    public void notEmpty() {
        assertEquals(fq(property, opALL), translator.notEmpty(property));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void within() {
        translator.within(property, new Point<Double>(x, y), distance);
    }
    
    @Test
    public void spatialQuery() {
        SolrSpatialQuery spatialQuery = translator.getSpatialQuery(QueryBuilder
                .builderFor(TestClass.class)
                .add(Restrictions.within(property, x, y, distance)).build());
        assertEquals(property, spatialQuery.getField());
        assertEquals(x, spatialQuery.getX());
        assertEquals(y, spatialQuery.getY());
        assertEquals(distance, spatialQuery.getDistance());
    }
    
    @Test
    public void spatialQuery_nested() {
        Query<TestClass, TestClass> query = QueryBuilder
                .builderFor(TestClass.class)
                .add(Restrictions.in(property, values))
                .add(Restrictions.or(
                        Restrictions.eq(property, value),
                        Restrictions.eq(property, from),
                        Restrictions.eq(property, to),
                        // NOTE: the OR will not be respected here; this is a
                        // limitation and it's nested here solely for testing
                        // purposes
                        Restrictions.within(property, x, y, distance)))
                .add(Restrictions.between(property, from, to))
                .build();
        SolrSpatialQuery spatialQuery = translator.getSpatialQuery(query);
        assertEquals(property, spatialQuery.getField());
        assertEquals(x, spatialQuery.getX());
        assertEquals(y, spatialQuery.getY());
        assertEquals(distance, spatialQuery.getDistance());
    }
    
    @Test
    public void spatialQuery_none() {
        Query<TestClass, TestClass> query = QueryBuilder
                .builderFor(TestClass.class)
                .add(Restrictions.in(property, values))
                .add(Restrictions.or(
                        Restrictions.eq(property, value),
                        Restrictions.eq(property, from),
                        Restrictions.eq(property, to)))
                .add(Restrictions.between(property, from, to))
                .build();
        assertNull(translator.getSpatialQuery(query));
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
        String expected = "(" + q1 + ")" + opOR + "(" + q2 + ")" + opOR + "(" + q3 + ")";
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
        String expected = "(" + q1 + ")" + opAND + "(" + q2 + ")" + opAND + "(" + q3 + ")";
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
        assertEquals("\"2012-02-21T14:34:25Z\"", translator.string(c.getTime()));
    }
    
    @Test
    public void ordering_asc() {
        SolrOrderings orderings = translator.order(property, Order.ASCENDING);
        List<SolrOrdering> list = orderings.get();
        assertEquals(1, list.size());
        SolrOrdering ordering = list.get(0);
        assertEquals(property, ordering.getField());
        assertEquals(SolrQuery.ORDER.asc, ordering.getOrder());
    }
    
    @Test
    public void ordering_desc() {
        SolrOrderings orderings = translator.order(property, Order.DESCENDING);
        List<SolrOrdering> list = orderings.get();
        assertEquals(1, list.size());
        SolrOrdering ordering = list.get(0);
        assertEquals(property, ordering.getField());
        assertEquals(SolrQuery.ORDER.desc, ordering.getOrder());
    }
    
    @Test
    public void translateOrdering_many() {
        SolrOrderings[] orders = new SolrOrderings[] { 
                new SolrOrderings(new SolrOrdering("property1", SolrQuery.ORDER.asc)),
                new SolrOrderings(new SolrOrdering("property2", SolrQuery.ORDER.desc)),
                new SolrOrderings(new SolrOrdering("property3", SolrQuery.ORDER.asc)),
                new SolrOrderings(new SolrOrdering("property4", SolrQuery.ORDER.desc))};
        SolrOrderings expected = SolrOrderings.merge(orders);
        SolrOrderings actual = translator.order(orders);
        assertEquals(4, actual.get().size());
        assertEquals(expected.get(), actual.get());
    }
    
    @Test
    public void translateProjection_null() {
        assertEquals(
                Collections.EMPTY_LIST,
                translator.translateProjection(QueryBuilder.builderFor(String.class).build()));
    }
    
    @Test
    public void translateProjection_some() {
        assertEquals(
                Arrays.asList("a", "b", "c"),
                translator.translateProjection(QueryBuilder.builderFor(
                        String.class, String.class, "a", "b", "c").build()));
    }

}
