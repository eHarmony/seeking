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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionHandler;
import com.eharmony.matching.seeking.handler.hibernate.ContainsExpressionNonHandler;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Box;
import com.eharmony.matching.seeking.query.geometry.BoxMaker;
import com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker;
import com.eharmony.matching.seeking.translator.QueryTranslator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * HQL based QueryTranslator
 */
public class HqlQueryTranslator extends
        AbstractHibernateQueryTranslator<String, String, String> implements
        QueryTranslator<String, String, String> {

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final Function<Object, String> toString = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return string(o);
        }
    };
    private final DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    
    public HqlQueryTranslator(HibernatePropertyResolver propertyResolver,
            BoxMaker boxMaker,
            ContainsExpressionHandler<String> containsExpressionHandler) {
        super(String.class, String.class, propertyResolver, boxMaker,
                containsExpressionHandler);
    }
    
    public HqlQueryTranslator(HibernatePropertyResolver propertyResolver,
            BoxMaker boxMaker) {
        this(propertyResolver, boxMaker,
                new ContainsExpressionNonHandler<String>());
    }
    
    public HqlQueryTranslator(HibernatePropertyResolver propertyResolver) {
        this(propertyResolver, new SimpleBoxMaker());
    }

    protected String string(Object o) {
        if (o instanceof Object[]) {
            return "[" + Joiner.on(",").join(
                    Lists.transform(Arrays.asList((Object[])o), toString)
            ) + "]";
        } else if (o instanceof String) {
            return "\"" + ((String)o).replace("\"", "\\\"") + "\"";
        } else if (o instanceof Character) {
            return "'" + (o.toString().replace("'", "\\'")) + "'";
        } else if (o instanceof Date) {
            return string(dateFormat.format((Date)o));
        }
        return o.toString();
    }
    
    protected String join(String fieldName, Object... parts) {
        return fieldName + " " + Joiner.on(" ").join(
                Lists.transform(Arrays.asList(parts), toString));
    }
    
    @Override
    protected <T> String translate(NativeExpression e, Class<T> entityClass) {
        return e.getExpression().toString();
    }
    
    @Override
    public String eq(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.EQUAL, value);
    }

    @Override
    public String ne(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.NOT_EQUAL, value);
    }

    @Override
    public String lt(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.LESS_THAN, value);
    }

    @Override
    public String lte(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.LESS_THAN_OR_EQUAL, value);
    }

    @Override
    public String gt(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.GREATER_THAN, value);
    }

    @Override
    public String gte(String fieldName, Object value) {
        return join(fieldName, HibernateOperator.GREATER_THAN_OR_EQUAL, value);
    }

    @Override
    public String between(String fieldName, Object from, Object to) {
        return join(fieldName, HibernateOperator.BETWEEN, from,
                HibernateOperator.AND, to);
    }

    @Override
    public String in(String fieldName, Object[] values) {
        return join(fieldName, HibernateOperator.IN, values);
    }

    @Override
    public String notIn(String fieldName, Object[] values) {
        return join(fieldName, HibernateOperator.NOT_IN, values);
    }

    @Override
    public String isNull(String fieldName) {
        return join(fieldName, HibernateOperator.NULL);
    }

    @Override
    public String notNull(String fieldName) {
        return join(fieldName, HibernateOperator.NOT_NULL);
    }

    @Override
    public String isEmpty(String fieldName) {
        return join(fieldName, HibernateOperator.EMPTY);
    }

    @Override
    public String notEmpty(String fieldName) {
        return join(fieldName, HibernateOperator.NOT_EMPTY);
    }
    
    @Override
    public <N extends Number & Comparable<N>> String within(String xFieldName,
            String yFieldName, Box<N> box) {
        return and(
                between(xFieldName, box.getA().getX(), box.getB().getX()),
                between(yFieldName, box.getA().getY(), box.getB().getY())
        );
    }

    @Override
    public String and(String... subqueries) {
        return junction(HibernateOperator.AND, subqueries);
    }

    @Override
    public String or(String... subqueries) {
        return junction(HibernateOperator.OR, subqueries);
    }
    
    protected String junction(HibernateOperator operator, String... subqueries) {
        if (subqueries.length < 1) {
            return "";
        } else if (subqueries.length == 1) {
            return subqueries[0];
        } else {
            return "(" + Joiner.on(") " + operator.symbol() + " (").join(subqueries) + ")";
        }
    }

    @Override
    public String order(String fieldName, Order o) {
        return fieldName + " " + (Order.ASCENDING.equals(o) ? "asc" : "desc");
    }

    @Override
    public String order(String... orders) {
        return Joiner.on(", ").join(orders);
    }

    @Override
    public <T, R> String translateProjection(Query<T, R> query) {
        return query.getReturnFields().size() > 0 
                ? Joiner.on(",").join(query.getReturnFields()) : null;
    }
}
