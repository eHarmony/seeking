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

package com.eharmony.matching.seeking.translator.solr;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.expression.Distance2dExpression;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.UnaryExpression;
import com.eharmony.matching.seeking.query.criterion.junction.Junction;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.translator.AbstractQueryTranslator;
import com.eharmony.matching.seeking.translator.PropertyResolver;
import com.eharmony.matching.seeking.translator.QueryTranslator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Translate generic Query objects into Solr string queries
 */
public class SolrQueryTranslator extends
        AbstractQueryTranslator<String, SolrOrderings, List<String>> implements
        QueryTranslator<String, SolrOrderings, List<String>> {
        
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
    private final Function<Object, String> toString = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return string(o);
        }
    };
    
    public SolrQueryTranslator(PropertyResolver propertyResolver) {
        super(String.class, SolrOrderings.class, propertyResolver);
    }
    
    protected String string(Object o) {
        //+ - && || ! ( ) { } [ ] ^ " ~ * ? : \
        if (o instanceof Object[]) {
            return "[" + Joiner.on(",").join(
                    Lists.transform(Arrays.asList((Object[])o), toString)
            ) + "]";
        } else if (o instanceof String) {
            return "\"" + ((String)o).replace("\"", "\\\"") + "\"";
        } else if (o instanceof Character) {
            return "'" + (o.toString().replace("'", "\\'")) + "'";
        } else if (o instanceof Date) {
            return string(dateFormatter.print(new DateTime((Date) o)));
        } else if (o instanceof DateTime) {
            return string(dateFormatter.print((DateTime) o));
        }
        return o.toString();
    }
    
    protected String fq(String fieldName, String value) {
        return fieldName + SolrOperator.EQUAL + value;
    }
    
    protected String negate(String query) {
        return SolrOperator.NEGATION + query;
    }
    
    protected String range(String from, String to, boolean fromInclusive, boolean toInclusive) {
        return (fromInclusive ? SolrOperator.OPEN_INCLUSIVE : SolrOperator.OPEN_EXCLUSIVE).symbol() +
                 from + " " + SolrOperator.TO.symbol() + " " + to + 
                 (toInclusive ? SolrOperator.CLOSE_INCLUSIVE : SolrOperator.CLOSE_EXCLUSIVE).symbol();
                
    }
    
    @Override
    protected <T, N extends Number & Comparable<N>> String translate(
            Distance2dExpression<N> e, Class<T> entityClass) {
        Operator operator = e.getOperator();
        if (Operator.WITHIN.equals(operator)) {
            // skip and capture elsewhere
            return null;
        } else {
            throw unsupported(operator, UnaryExpression.class);
        }
    }
    
    @Override
    protected <T> String translate(NativeExpression e, Class<T> entityClass) {
        return e.getExpression().toString();
    }
    
    @Override
    public String eq(String fieldName, Object value) {
        return fq(fieldName, string(value));
    }

    @Override
    public String ne(String fieldName, Object value) {
        return negate(eq(fieldName, value));
    }

    @Override
    public String lt(String fieldName, Object value) {
        return fq(fieldName, range("*", string(value), true, false));
    }

    @Override
    public String lte(String fieldName, Object value) {
        return fq(fieldName, range("*", string(value), true, true));
    }

    @Override
    public String gt(String fieldName, Object value) {
        return fq(fieldName, range(string(value), "*", false, true));
    }

    @Override
    public String gte(String fieldName, Object value) {
        return fq(fieldName, range(string(value), "*", true, true));
    }

    @Override
    public String between(String fieldName, Object from, Object to) {
        return fq(fieldName, range(string(from), string(to), true, true));
    }

    @Override
    public String in(String fieldName, Object[] values) {
        return fq(fieldName, fieldJunction(SolrOperator.OR, values));
    }

    @Override
    public String notIn(String fieldName, Object[] values) {
        return negate(in(fieldName, values));
    }
    
    @Override
    public String contains(String fieldName, Object[] values) {
        int n = values.length;
        if (n == 1) {
            return eq(fieldName, values[0]);
        } else {
            return fq(fieldName, fieldJunction(SolrOperator.AND, values));
        }
    }

    @Override
    public String isNull(String fieldName) {
        return negate(fq(fieldName, range(SolrOperator.WILDCARD.symbol(), SolrOperator.WILDCARD.symbol(), true, true)));
    }

    @Override
    public String notNull(String fieldName) {
        return fq(fieldName, range(SolrOperator.WILDCARD.symbol(), SolrOperator.WILDCARD.symbol(), true, true));
    }

    @Override
    public String isEmpty(String fieldName) {
        return isNull(fieldName);
    }

    @Override
    public String notEmpty(String fieldName) {
        return notNull(fieldName);
    }
    
    @Override
    public <N extends Number & Comparable<N>> String within(String fieldName,
            Point<N> center, Number distance) {
        throw new UnsupportedOperationException("Cannot return geospatial query as a String");
    }
    
    public <T, R> SolrSpatialQuery getSpatialQuery(Query<T, R> query) {
        Distance2dExpression<? extends Number> c = findGeospatialCriterion(query
                .getCriteria());
        return (c != null)
                ? new SolrSpatialQuery(getPropertyResolver().resolve(
                    c.getPropertyName(), query.getEntityClass()), c.getX(),
                    c.getY(), c.getDistance())
                : null;
    }
    
    protected <T> Distance2dExpression<?> findGeospatialCriterion(
            Criterion c) {
        // find the first geospatial query component
        if (c instanceof Distance2dExpression) {
            return (Distance2dExpression<?>) c;
        } else if (c instanceof Junction) {
            for (Criterion c2 : ((Junction) c).getCriteria()) {
                Distance2dExpression<?> e = findGeospatialCriterion(c2);
                if (e != null) {
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public String and(String... subqueries) {
        return junction(SolrOperator.AND, subqueries);
    }

    @Override
    public String or(String... subqueries) {
        return junction(SolrOperator.OR, subqueries);
    }
    
    protected String junction(SolrOperator operator, String... subqueries) {
        if (subqueries.length < 1) {
            return "";
        } else if (subqueries.length == 1) {
            return subqueries[0];
        } else {
            return "(" + Joiner.on(") " + operator.symbol() + " (").join(subqueries) + ")";
        }
    }
    
    protected String fieldJunction(SolrOperator operator, Object... values) {
        List<String> strings = Lists.transform(Arrays.asList(values), toString);
        return "(" + Joiner.on(" " + operator + " ").join(strings) + ")";
    }

    @Override
    public SolrOrderings order(String fieldName, Order o) {
        return new SolrOrderings(new SolrOrdering(fieldName, 
                (Order.ASCENDING.equals(o) 
                        ? SolrQuery.ORDER.asc
                        : SolrQuery.ORDER.desc)));
    }

    @Override
    public SolrOrderings order(SolrOrderings... orderings) {
        return SolrOrderings.merge(orderings);
    }

    @Override
    public <T, R> List<String> translateProjection(Query<T, R> query) {
        return query.getReturnFields();
    }

}
