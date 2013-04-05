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

package com.eharmony.matching.seeking.translator.mongodb;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.criterion.Ordering.Order;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.geometry.Point;
import com.eharmony.matching.seeking.translator.AbstractQueryTranslator;
import com.eharmony.matching.seeking.translator.PropertyResolver;
import com.eharmony.matching.seeking.translator.QueryTranslator;
import com.google.code.morphia.mapping.Mapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * DBObject based QueryTranslator for MongoDB queries
 */
public class MongoQueryTranslator extends
        AbstractQueryTranslator<DBObject, DBObject, DBObject> implements
        QueryTranslator<DBObject, DBObject, DBObject> {
        
    public MongoQueryTranslator(PropertyResolver propertyResolver) {
        super(DBObject.class, DBObject.class, propertyResolver);
    }

    protected DBObject object() {
        return new BasicDBObject();
    }
    
    protected DBObject object(String key, Object value) {
        return new BasicDBObject(key, value);
    }
    
    protected DBObject object(MongoOperator operator, Object value) {
        return object(operator.symbol(), value);
    }
    
    protected static <E> LinkedHashSet<E> set(E... elements) {
        LinkedHashSet<E> set = new LinkedHashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
    
    protected static List<Object> list(Object... elements) {
        return Lists.newArrayList(elements);
    }
    
    @SuppressWarnings("unchecked")
    protected DBObject merge(DBObject[] queries) {
        if (queries.length == 1) {
            return queries[0];
        }
        DBObject merged = object();
        Set<DBObject> ands = new LinkedHashSet<DBObject>();
        String andSymbol = MongoOperator.AND.symbol();
        // multimap => map<key,Collection<values>> 
        Map<String, Collection<DBObject>> map = multimap(queries).asMap();
        for (Entry<String, Collection<DBObject>> entry : map.entrySet()) {
            String key = entry.getKey();
            // merge multiple ANDs
            if (andSymbol.equals(key)) {
                for (DBObject o : entry.getValue()) {
                    ands.addAll((Set<DBObject>) o.get(andSymbol));
                }
            } else if (entry.getValue().size() == 1) {
                DBObject single = entry.getValue().iterator().next();
                merged.put(key, single.get(key));
            } else {
                for (DBObject o : entry.getValue()) {
                    ands.add(o);
                }
            }
        }
        // add ANDs
        if (ands.size() > 0) {
            merged.put(andSymbol, ands);
        }
        return merged;
    }
    
    private Multimap<String, DBObject> multimap(DBObject[] objects) {
        Multimap<String, DBObject> m = LinkedHashMultimap
                .<String, DBObject> create(objects.length, 8);
        for (DBObject o : objects) {
            Set<String> keys = o.keySet();
            for (String key : keys) {
                m.put(key, o);
            }
        }
        return m;
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.AbstractQueryTranslator#translate(com.eharmony.matching.seeking.query.criterion.expression.NativeExpression, java.lang.Class)
     */
    @Override
    protected <T> DBObject translate(NativeExpression e, Class<T> entityClass) {
        Class<?> expressionClass = e.getExpressionClass();
        if (DBObject.class.isAssignableFrom(expressionClass)) {
            return (DBObject) e.getExpression();
        } else {
            throw unsupported(e);
        }
    }

    @Override
    public DBObject eq(String fieldName, Object value) {
        return object(fieldName, value);
    }

    @Override
    public DBObject ne(String fieldName, Object value) {
        return object(fieldName, object(MongoOperator.NOT_EQUAL, value));
    }

    @Override
    public DBObject lt(String fieldName, Object value) {
        return object(fieldName, object(MongoOperator.LESS_THAN, value));
    }

    @Override
    public DBObject lte(String fieldName, Object value) {
        return object(fieldName, object(MongoOperator.LESS_THAN_OR_EQUAL, value));
    }

    @Override
    public DBObject gt(String fieldName, Object value) {
        return object(fieldName, object(MongoOperator.GREATER_THAN, value));
    }

    @Override
    public DBObject gte(String fieldName, Object value) {
        return object(fieldName, object(MongoOperator.GREATER_THAN_OR_EQUAL, value));
    }

    @Override
    public DBObject between(String fieldName, Object from, Object to) {
        DBObject range = object();
        range.put(MongoOperator.GREATER_THAN_OR_EQUAL.symbol(), from);
        range.put(MongoOperator.LESS_THAN_OR_EQUAL.symbol(), to);
        return object(fieldName, range);
    }

    @Override
    public DBObject in(String fieldName, Object[] values) {
        return object(fieldName, object(MongoOperator.IN, values));
    }

    @Override
    public DBObject notIn(String fieldName, Object[] values) {
        return object(fieldName, object(MongoOperator.NOT_IN, values));
    }

    @Override
    public DBObject contains(String fieldName, Object[] values) {
        int n = values.length;
        if (n == 1) {
            return eq(fieldName, values[0]);
        } else {
            DBObject[] conditions = new DBObject[n];
            for (int i = 0; i < n; i++) {
                conditions[i] = eq(fieldName, values[i]);
            }
            return and(conditions);
        }
    }
    
    protected DBObject exists(String fieldName, boolean exists) {
        return object(fieldName, object(MongoOperator.EXISTS, exists ? 1 : 0));
    }

    @Override
    public DBObject isNull(String fieldName) {
        return or(exists(fieldName, false), eq(fieldName, null));
    }

    @Override
    public DBObject notNull(String fieldName) {
        return and(exists(fieldName, true), ne(fieldName, null));
    }

    @Override
    public DBObject isEmpty(String fieldName) {
        return or(exists(fieldName, false), eq(fieldName, ""));
    }

    @Override
    public DBObject notEmpty(String fieldName) {
        return and(exists(fieldName, true), ne(fieldName, ""));
    }
    
    @Override
    public <N extends Number & Comparable<N>> DBObject within(String fieldName,
            Point<N> center, Number distance) {
        return object(fieldName, object(MongoOperator.WITHIN,
                object(MongoOperator.CENTER, 
                        list(list(center.getX(), center.getY()), distance))));
    }

    @Override
    public DBObject and(DBObject... subqueries) {
        return merge(subqueries);
    }

    @Override
    public DBObject or(DBObject... subqueries) {
        return subqueries.length == 1 ? subqueries[0] : object(
                MongoOperator.OR, set((Object[]) subqueries));
        
    }
    
    @Override
    public DBObject order(String fieldName, Order o) {
        return object(fieldName, Order.ASCENDING.equals(o) ? 1 : -1);
    }

    @Override
    public DBObject order(DBObject... orders) {
        DBObject merged = object();
        for (DBObject order : orders) {
            merged.putAll(order);
        }
        return merged;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.QueryTranslator#translateProjection(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> DBObject translateProjection(Query<T, R> query) {
        if (query.getReturnFields().size() > 0) {
            DBObject fields = object();
            for(String field : query.getReturnFields()) {
                fields.put(
                        getPropertyResolver().resolve(field,
                                query.getEntityClass()), 1);
            }
            // if _id is not present then exclude it
            if (!fields.containsField(Mapper.ID_KEY)) {
                fields.put(Mapper.ID_KEY, 0);
            }
            return fields;
        } else {
            return null;
        }
    }

}
