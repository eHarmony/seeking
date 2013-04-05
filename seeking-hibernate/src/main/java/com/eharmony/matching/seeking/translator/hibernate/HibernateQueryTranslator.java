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

package com.eharmony.matching.seeking.translator.hibernate;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionNonHandler;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker;
import com.eharmony.matching.seeking.translator.QueryTranslator;

/**
 * Hibernate Criteria based QueryTranslator
 */
public class HibernateQueryTranslator extends
        AbstractHibernateQueryTranslator<Criterion, Orders, Projection>
        implements QueryTranslator<Criterion, Orders, Projection> {

    public HibernateQueryTranslator(HibernatePropertyResolver propertyResolver,
            BoxMaker boxMaker,
            ContainsExpressionHandler<Criterion> containsExpressionHandler) {
        super(Criterion.class, Orders.class, propertyResolver, boxMaker,
                containsExpressionHandler);
    }
    
    public HibernateQueryTranslator(HibernatePropertyResolver propertyResolver,
            BoxMaker boxMaker) {
        this(propertyResolver, boxMaker,
                new ContainsExpressionNonHandler<Criterion>());
    }
    
    public HibernateQueryTranslator(HibernatePropertyResolver propertyResolver) {
        this(propertyResolver, new SimpleBoxMaker());
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.AbstractQueryTranslator#translate(com.eharmony.matching.seeking.query.criterion.expression.NativeExpression, java.lang.Class)
     */
    @Override
    protected <T> Criterion translate(NativeExpression e, Class<T> entityClass) {
        Class<?> expressionClass = e.getExpressionClass();
        if (expressionClass == String.class) {
            return Restrictions.sqlRestriction("(" + e.getExpression().toString() + ")");
        } else if (Criterion.class.isAssignableFrom(expressionClass)) {
            return (Criterion) e.getExpression();
        } else {
            throw unsupported(e);
        }
    }

    @Override
    public Criterion eq(String fieldName, Object value) {
        return Restrictions.eq(fieldName, value);
    }

    @Override
    public Criterion ne(String fieldName, Object value) {
        return Restrictions.ne(fieldName, value);
    }

    @Override
    public Criterion lt(String fieldName, Object value) {
        return Restrictions.lt(fieldName, value);
    }

    @Override
    public Criterion lte(String fieldName, Object value) {
        return Restrictions.le(fieldName, value);
    }

    @Override
    public Criterion gt(String fieldName, Object value) {
        return Restrictions.gt(fieldName, value);
    }

    @Override
    public Criterion gte(String fieldName, Object value) {
        return Restrictions.ge(fieldName, value);
    }

    @Override
    public Criterion between(String fieldName, Object from, Object to) {
        return Restrictions.between(fieldName, from, to);
    }

    @Override
    public Criterion in(String fieldName, Object[] values) {
        return Restrictions.in(fieldName, values);
    }

    @Override
    public Criterion notIn(String fieldName, Object[] values) {
        return Restrictions.not(Restrictions.in(fieldName, values));
    }

    @Override
    public Criterion isNull(String fieldName) {
        return Restrictions.isNull(fieldName);
    }

    @Override
    public Criterion notNull(String fieldName) {
        return Restrictions.isNotNull(fieldName);
    }

    @Override
    public Criterion isEmpty(String fieldName) {
        return Restrictions.isEmpty(fieldName);
    }

    @Override
    public Criterion notEmpty(String fieldName) {
        return Restrictions.isNotEmpty(fieldName);
    }
    
    @Override
    public <N extends Number & Comparable<N>> Criterion within(
            String xFieldName, String yFieldName, Box<N> box) {
        return and(
                between(xFieldName, box.getA().getX(), box.getB().getX()),
                between(yFieldName, box.getA().getY(), box.getB().getY())
        );
    }

    @Override
    public Criterion and(Criterion... subqueries) {
        Conjunction conjunction = Restrictions.conjunction();
        for (Criterion criterion : subqueries) {
            conjunction.add(criterion);
        }
        return conjunction;
    }

    @Override
    public Criterion or(Criterion... subqueries) {
        Disjunction disjunction = Restrictions.disjunction();
        for (Criterion criterion : subqueries) {
            disjunction.add(criterion);
        }
        return disjunction;
    }

    @Override
    public Orders order(String fieldName, Ordering.Order o) {
        return new Orders(Ordering.Order.ASCENDING.equals(o)
                ? Order.asc(fieldName) : Order.desc(fieldName));
    }

    @Override
    public Orders order(Orders... orders) {
        Orders merged = new Orders();
        for (Orders orderss: orders) {
            merged.addAll(orderss.get());
        }
        return merged;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.QueryTranslator#translateProjection(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> Projection translateProjection(Query<T, R> query) {
        if (query.getReturnFields().size() > 0) {
            ProjectionList projectionList = Projections.projectionList();
            for (String returnField : query.getReturnFields()) {
                projectionList.add(Projections.property(returnField));
            }
            return projectionList;
        } else {
            return null;
        }
    }
}
