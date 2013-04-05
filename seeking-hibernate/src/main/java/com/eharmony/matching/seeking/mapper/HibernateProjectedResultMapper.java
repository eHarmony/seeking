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

package com.eharmony.matching.seeking.mapper;

import java.util.List;

import org.hibernate.ScrollableResults;

import com.eharmony.matching.seeking.query.Query;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Maps results from Hibernate to the requested return type.
 */
public class HibernateProjectedResultMapper {
    
    private final ProjectedResultMapper mapper;
    
    public HibernateProjectedResultMapper(ProjectedResultMapper mapper) {
        this.mapper = mapper;
    }
    
    protected <T, R> String[] returnFields(Query<T, R> query) {
        List<String> list = query.getReturnFields();
        String[] array = new String[list.size()];
        return list.toArray(array);
    }
    
    public <T, R> R mapResult(Object o, Query<T, R> query, String[] returnFields) {
        return query.getEntityClass().equals(query.getReturnType())
                ? query.getReturnType().cast(o)
                : mapper.mapTo(query.getReturnType(), o, returnFields);
    }
    
    public <T, R> R mapResult(Object o, Query<T, R> query) {
        return mapResult(o, query, returnFields(query));
    }
    
    @SuppressWarnings("unchecked")
    public <T, R> List<R> mapResults(List<?> objects, final Query<T, R> query) {
        final String[] returnFields = returnFields(query);
        return query.getEntityClass().equals(query.getReturnType())
                ? (List<R>) objects
                : Lists.transform(objects, new Function<Object,R>() {
                    @Override
                    public R apply(Object arg0) {
                        return mapResult(arg0, query, returnFields);
                    }
                });
    }
    
    @SuppressWarnings("unchecked")
    public <T, R> Iterable<R> mapResults(ScrollableResults objects, final Query<T, R> query) {
        ScrollableResultsIterable iterable = new ScrollableResultsIterable(objects);
        final String[] returnFields = returnFields(query);
        return query.getEntityClass().equals(query.getReturnType())
                ? Iterables.transform(iterable,  new Function<Object,R>() {
                    @Override
                    public R apply(Object arg0) {
                        return (R) arg0;
                    }
                })
                : Iterables.transform(iterable, new Function<Object,R>() {
                    @Override
                    public R apply(Object arg0) {
                        return mapResult(arg0, query, returnFields);
                    }
                });
    }
    

}
