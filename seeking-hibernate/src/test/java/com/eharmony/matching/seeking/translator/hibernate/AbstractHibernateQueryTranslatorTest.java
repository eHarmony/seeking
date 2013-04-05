package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionNonHandler;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.expression.Distance2dExpression;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker;
import com.eharmony.matching.seeking.test.MockHibernatePropertyResolver;
import com.google.common.base.Joiner;

public class AbstractHibernateQueryTranslatorTest {
    
    private final HibernatePropertyResolver resolver = new MockHibernatePropertyResolver();
    private final BoxMaker boxMaker = new SimpleBoxMaker();
    private final ContainsExpressionHandler<String> containsExpressionHandler = new ContainsExpressionNonHandler<String>();
    
    private final String property = "propertyName";
    private final double x = 1.0;
    private final double y = 2.0;
    private final double distance = 3.5;

    private final Class<Test> entityClass = Test.class;
    
    private final AbstractHibernateQueryTranslator<String, String, String> translator = new AbstractHibernateQueryTranslator<String, String, String>(
            String.class, String.class, resolver, boxMaker, containsExpressionHandler) {

        @Override
        public String eq(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String ne(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String lt(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String lte(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String gt(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String gte(String fieldName, Object value) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String between(String fieldName, Object from, Object to) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String in(String fieldName, Object[] values) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String notIn(String fieldName, Object[] values) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String contains(String fieldName, Object[] values) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String isNull(String fieldName) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String notNull(String fieldName) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String isEmpty(String fieldName) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String notEmpty(String fieldName) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String and(String... subqueries) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String or(String... subqueries) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String order(String fieldName, Order o) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public String order(String... orders) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        public <N extends Number & Comparable<N>> String within(
                String xFieldName, String yFieldName, Box<N> box) {
            return join(Operator.WITHIN, xFieldName, yFieldName, box.toString());
        }

        @Override
        public <T, R> String translateProjection(Query<T, R> query) {
            throw new RuntimeException("shouldn't be here");
        }

        @Override
        protected <T> String translate(NativeExpression e, Class<T> entityClass) {
            return e.getExpression().toString();
        }
        
    };
    
    private String join(Object... parts) {
        return Joiner.on(" ").join(parts);
    }
    
    private Query<Test, Test> query(Criterion criteria) {
        return QueryBuilder.builderFor(entityClass).add(criteria).build();
    }
    
    public String verify_translate_Distance2dExpression(Operator operator) {
        Distance2dExpression<Double> e = new Distance2dExpression<Double>(operator, property, x, y, distance);
        String expected = join(operator,
                resolver.resolveXField(property, entityClass), 
                resolver.resolveYField(property, entityClass), 
                boxMaker.make(new Point<Double>(x,y), distance));
        assertEquals(expected, translator.translate(e, entityClass));
        assertEquals(expected, translator.translate(query(e)));
        return expected;
    }
    
    @Test
    public void translate_Distance2dExpression() {
        verify_translate_Distance2dExpression(Operator.WITHIN);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void unexpected_Distance2dExpression() {
        translator.translate(new Distance2dExpression<Double>(Operator.EQUAL, property, x, y, distance), entityClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void within_singleFieldName_IllegalStateException() {
        translator.within("field", new Point<Integer>(1, 1), 1);
    }
    
    @Test
    public void translate_NativeExpression() {
        String expression = "property & 4 > 0";
        NativeExpression e = new NativeExpression(String.class, expression);
        assertEquals(expression, translator.translate(query(e)));
    }

}
