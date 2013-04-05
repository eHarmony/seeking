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

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.expression.Distance2dExpression;
import com.eharmony.matching.seeking.query.criterion.expression.UnaryExpression;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.translator.AbstractQueryTranslator;
import com.eharmony.matching.seeking.translator.QueryTranslator;

/**
 * Base class for the different Hibernate QueryTranslator implementations. 
 *
 * @param <Q>
 * @param <O>
 * @param <P>
 */
public abstract class AbstractHibernateQueryTranslator<Q, O, P> extends
        AbstractQueryTranslator<Q, O, P> implements QueryTranslator<Q, O, P> {
    
    private final BoxMaker boxMaker;
    private final ContainsExpressionHandler<Q> containsExpressionHandler;

    public AbstractHibernateQueryTranslator(Class<Q> queryClass,
            Class<O> orderClass, HibernatePropertyResolver propertyResolver,
            BoxMaker boxMaker,
            ContainsExpressionHandler<Q> containsExpressionHandler) {
        super(queryClass, orderClass, propertyResolver);
        this.boxMaker = boxMaker;
        this.containsExpressionHandler = containsExpressionHandler;
    }
    
    protected HibernatePropertyResolver getPropertyResolver() {
        return (HibernatePropertyResolver) super.getPropertyResolver();
    }
    
    public BoxMaker getBoxMaker() {
        return boxMaker;
    }
    
    public ContainsExpressionHandler<Q> getContainsExpressionHandler() {
        return containsExpressionHandler;
    }
    
    @Override
    protected <T, N extends Number & Comparable<N>> Q translate(Distance2dExpression<N> e, Class<T> entityClass) {
        Operator operator = e.getOperator();
        String xFieldName = getPropertyResolver().resolveXField(e.getPropertyName(), entityClass);
        String yFieldName = getPropertyResolver().resolveYField(e.getPropertyName(), entityClass);
        if (Operator.WITHIN.equals(operator)) {
            Box<N> box = boxMaker.make(new Point<N>(e.getX(), e.getY()), e.getDistance());
            return within(xFieldName, yFieldName, box);
        } else {
            throw unsupported(operator, UnaryExpression.class);
        }
    }
    
    @Override
    public <N extends Number & Comparable<N>> Q within(String fieldName,
            Point<N> center, Number distance) {
        throw new IllegalStateException(
                "Cannot perform geometric queries using one field");
    }
    
    public abstract <N extends Number & Comparable<N>> Q within(
            String xFieldName, String yFieldName, Box<N> box);
    
    @Override
    public Q contains(String fieldName, Object[] values) {
        return containsExpressionHandler.contains(fieldName, values);
    }    
}
