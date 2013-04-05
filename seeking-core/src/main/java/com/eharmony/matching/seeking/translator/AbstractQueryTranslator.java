/*
 *  Copyright 2012 eHarmony, Inc
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.eharmony.matching.seeking.translator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
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

/**
 * Abstract Query Translation. Convert a generic Query with nested criteria to a
 * datastore specific query. Extend to provide datastore specific query
 * component implementations
 * 
 * @param <Q>
 *            the query type
 * @param <O>
 *            the ordering type
 * @param <P>
 *            the projected type
 */
public abstract class AbstractQueryTranslator<Q,O,P> implements QueryTranslator<Q,O,P> {

    private final Class<Q> queryClass;
    private final Class<O> orderClass;
    private final PropertyResolver propertyResolver;

    public AbstractQueryTranslator(Class<Q> queryClass,
            Class<O> orderClass,
            PropertyResolver propertyResolver) {
        this.queryClass = queryClass;
        this.orderClass = orderClass;
        this.propertyResolver = propertyResolver;
    }
    
    protected Class<Q> getQueryClass() {
        return queryClass;
    }

    protected Class<O> getOrderClass() {
        return orderClass;
    }

    protected PropertyResolver getPropertyResolver() {
        return propertyResolver;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.QueryTranslator#translate(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T,R> Q translate(Query<T,R> query) {
        Criterion rootCriterion = query.getCriteria();
        Class<T> entityClass = query.getEntityClass();
        return translate(rootCriterion, entityClass);
    }
    
    /*
     * Notes regarding the abundance of instanceof checks:
     * 
     * A case for considering it the lesser of evils:
     * https://sites.google.com/site/steveyegge2/when-polymorphism-fails
     * 
     * If we were using Scala, we could create datastore specific Traits that
     * would allow us to extend the Generic Query and allow it produce a
     * datastore specific query.
     * 
     * There's also a strong case for the visitor pattern. The downsides are
     * needlessly exposing the underlying Criterion types to the various
     * implementations and a significant increase in the number of classes
     * needed for implementations.
     * 
     * If this becomes a maintainability or performance issue then we should
     * look at replacing the current approach with something like the visitor
     * pattern.
     */
    protected <T> Q translate(Criterion c, Class<T> entityClass) {
        // a Criterion can be an Expression or a Junction
        if (c instanceof Expression) {
            return translate((Expression) c, entityClass);
        } else if (c instanceof Junction) {
            return translate((Junction) c, entityClass);
        } else if (c instanceof NativeExpression) {
            return translate((NativeExpression) c, entityClass);
        } else {
            throw unsupported(c.getClass());
        }
    }
    
    protected abstract <T> Q translate(NativeExpression e, Class<T> entityClass);

    protected <T> Q translate(Expression e,
            Class<T> entityClass) {
        String fieldName = propertyResolver.resolve(e.getPropertyName(),
                entityClass);
        if (e instanceof EqualityExpression) {
            return translate((EqualityExpression) e, fieldName);
        } else if (e instanceof RangeExpression) {
            return translate((RangeExpression) e, fieldName);
        } else if (e instanceof SetExpression) {
            return translate((SetExpression) e, fieldName);
        } else if (e instanceof UnaryExpression) {
            return translate((UnaryExpression) e, fieldName);
        } else if (e instanceof Distance2dExpression) {
            return translate((Distance2dExpression<?>) e, entityClass);
        } else {
            throw unsupported(e.getClass());
        }
    }

    protected <T> Q translate(Junction j, Class<T> entityClass) {
        // a Junction can be a Conjunction (and) or a Disjunction (or)
        if (j instanceof Conjunction) {
            return translate((Conjunction) j, entityClass);
        } else if (j instanceof Disjunction) {
            return translate((Disjunction) j, entityClass);
        } else {
            throw unsupported(j.getClass());
        }
    }

    protected <T> Q translate(Conjunction j, Class<T> entityClass) {
        return and(subqueries(j, entityClass));
    }

    protected <T> Q translate(Disjunction j, Class<T> entityClass) {
        return or(subqueries(j, entityClass));
    }

    @SuppressWarnings("unchecked")
    protected <T> Q[] subqueries(Junction j, Class<T> entityClass) {
        List<Criterion> criteria = j.getCriteria();
        List<Q> translated = new ArrayList<Q>(criteria.size());
        for (Criterion c : criteria) {
            Q q = translate(c, entityClass);
            if (q != null) {
                translated.add(q);
            }
        }
        return translated.toArray((Q[]) Array.newInstance(queryClass,
                translated.size()));
    }

    protected Q translate(EqualityExpression e, String fieldName) {
        Operator operator = e.getOperator();
        Object value = e.getValue();
        
        switch (operator) {
        case EQUAL:
            return eq(fieldName, value);
        case NOT_EQUAL:
            return ne(fieldName, value);
        case GREATER_THAN:
            return gt(fieldName, value);
        case GREATER_THAN_OR_EQUAL:
            return gte(fieldName, value);
        case LESS_THAN:
            return lt(fieldName, value);
        case LESS_THAN_OR_EQUAL:
            return lte(fieldName, value);
        default:
            throw unsupported(operator, EqualityExpression.class);
        }
    }

    protected Q translate(RangeExpression e, String fieldName) {
        Operator operator = e.getOperator();
        Object from = e.getFrom();
        Object to = e.getTo();
        
        switch (operator) {
        case BETWEEN:
            return between(fieldName, from, to);
        default:
            throw unsupported(operator, RangeExpression.class);
        }
    }

    protected Q translate(SetExpression e, String fieldName) {
        Operator operator = e.getOperator();
        Object[] values = e.getValues();

        switch (operator) {
        case IN:
            return in(fieldName, values);
        case NOT_IN:
            return notIn(fieldName, values);
        case CONTAINS:
            return contains(fieldName, values);
        default:
            throw unsupported(operator, SetExpression.class);
        }
    }

    protected Q translate(UnaryExpression e, String fieldName) {
        Operator operator = e.getOperator();

        switch (operator) {
        case NULL:
            return isNull(fieldName);
        case NOT_NULL:
            return notNull(fieldName);
        case EMPTY:
            return isEmpty(fieldName);
        case NOT_EMPTY:
            return notEmpty(fieldName);
        default:
            throw unsupported(operator, UnaryExpression.class);
        }
    }
    
    protected <T, N extends Number & Comparable<N>> Q translate(
            Distance2dExpression<N> e, Class<T> entityClass) {
        Operator operator = e.getOperator();
        String fieldName = propertyResolver.resolve(e.getPropertyName(), entityClass);

        switch (operator) {
        case WITHIN:
            /*
             * Default behavior, this will not work for implementations that
             * require two resolved properties. Override this method to provide
             * a dual property solution.
             */
            return within(fieldName, new Point<N>(e.getX(), e.getY()),
                    e.getDistance());
        default:
            throw unsupported(operator, Distance2dExpression.class);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.QueryTranslator#translateOrder(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> O translateOrder(Query<T, R> query) {
        List<Ordering> orderingList = query.getOrder().get();
        @SuppressWarnings("unchecked")
        O[] orders = (O[]) Array.newInstance(orderClass, orderingList.size());
        for (int i = 0; i < orders.length; i++) {
            Ordering ordering = orderingList.get(i);
            orders[i] = order(
                    propertyResolver.resolve(ordering.getPropertyName(),
                            query.getEntityClass()), ordering.getOrder());
        }
        return order(orders);
    }

    protected UnsupportedOperationException unsupported(
            Class<? extends Criterion> type) {
        throw new UnsupportedOperationException(type.getSimpleName()
                + " type not supported.");
    }

    protected UnsupportedOperationException unsupported(Operator operator,
            Class<? extends Expression> expressionType) {
        throw new UnsupportedOperationException(operator
                + " not supported for " + expressionType.getSimpleName());
    }
    
    protected UnsupportedOperationException unsupported(
            NativeExpression e) {
        throw new UnsupportedOperationException("Native Expression ("
                + e.getExpression() + ") of type " + e.getExpressionClass()
                + " not supported.");
    }

    /**
     * Translate an "equal" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q eq(String fieldName, Object value);

    /**
     * Translate a "not equal" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q ne(String fieldName, Object value);

    /**
     * Translate a "less than" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q lt(String fieldName, Object value);

    /**
     * Translate a "less than or equal" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q lte(String fieldName, Object value);

    /**
     * Translate a "greater than" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q gt(String fieldName, Object value);

    /**
     * Translate a "greater than or equal" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param value
     *            the reference value
     */
    public abstract Q gte(String fieldName, Object value);

    /**
     * Translate a "between" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param from
     *            the lower bound value
     * @param to
     *            the upper bound value
     */
    public abstract Q between(String fieldName, Object from, Object to);

    /**
     * Translate an "in" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param values
     *            the reference values
     */
    public abstract Q in(String fieldName, Object[] values);

    /**
     * Translate a "not in" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param values
     *            the reference values
     */
    public abstract Q notIn(String fieldName, Object[] values);

    /**
     * Translate a "contains" expression
     * 
     * @param fieldName
     *            the resolved field name
     * @param values
     *            the reference values
     */
    public abstract Q contains(String fieldName, Object[] values);

    /**
     * Translate a "is null" expression
     * 
     * @param fieldName
     *            the resolved field name
     */
    public abstract Q isNull(String fieldName);

    /**
     * Translate a "not null" expression
     * 
     * @param fieldName
     *            the resolved field name
     */
    public abstract Q notNull(String fieldName);

    /**
     * Translate a "is empty" expression
     * 
     * @param fieldName
     *            the resolved field name
     */
    public abstract Q isEmpty(String fieldName);

    /**
     * Translate a "is empty" expression
     * 
     * @param fieldName
     *            the resolved field name
     */
    public abstract Q notEmpty(String fieldName);
    
    /**
     * Translate a within statement
     * 
     * @param propertyName
     * @param x
     * @param y
     * @param distance
     * @return
     */
    public abstract <N extends Number & Comparable<N>> Q within(
            String fieldName, Point<N> center, Number distance);

    /**
     * Translate an "and" expression
     */
    public abstract Q and(Q... subqueries);

    /**
     * Translate an "or" expression
     */
    public abstract Q or(Q... subqueries);
    
    /**
     * Translate an order statement
     * 
     * @param fieldName
     *            the resolved field name
     * @param o
     *            the order (ascending or descending)
     * @return
     */
    public abstract O order(String fieldName, Order o);

    /**
     * Join multiple orderings
     * 
     * @param a
     *            Conjunction
     */
    public abstract O order(O... orders);

}
