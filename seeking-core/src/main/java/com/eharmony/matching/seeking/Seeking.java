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

package com.eharmony.matching.seeking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.QueryImpl;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Orderings;
import com.eharmony.matching.seeking.query.criterion.Restrictions;

/**
 * A better builder for Query objects. In progress.
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public class Seeking<T, R> {

    private final Class<T> entityClass;
    private final Class<R> returnType;
    private List<Criterion> criteria = new ArrayList<Criterion>();
    private Orderings orderings = new Orderings();
    private Integer maxResults;
    private List<String> returnFields = Collections.emptyList();

    public static <T> Seeking<T, T> a(Class<T> entityClass) {
        return new Seeking<T, T>(entityClass, entityClass);
    }

    public Seeking(Class<T> entityClass, Class<R> returnType) {
        this.entityClass = entityClass;
        this.returnType = returnType;
    }

    public Seeking(Class<T> entityClass, Class<R> returnType,
            Seeking<?, ?> other) {
        this(entityClass, returnType);
        this.criteria = other.criteria;
        this.orderings = other.orderings;
        this.maxResults = other.maxResults;
        this.returnFields = other.returnFields;
    }

    public Seeking<T, R> as(Class<R> returnType) {
        return new Seeking<T, R>(this.entityClass, returnType, this);
    }

    public Seeking<T, R> as(Class<R> returnType, String... returnFields) {
        return as(returnType).using(returnFields);
    }

    public Seeking<T, R> using(String... returnFields) {
        this.returnFields = Arrays.asList(returnFields);
        return this;
    }
    
    
    
    // ************************************************************************
    // TODO: complete a DSL for expressive building of queries.
    // ************************************************************************

    
    
    
    /**
     * Compound all the restrictions and create the query
     * 
     * @return the query
     */
    public Query<T, R> build() {
        // if criteria.size == 0, rootCriterion = null
        Criterion rootCriterion = null;
        if (criteria.size() == 1) {
            rootCriterion = criteria.get(0);
        } else if (criteria.size() > 1) {
            rootCriterion = Restrictions.and(criteria
                    .toArray(new Criterion[criteria.size()]));
        }
        return new QueryImpl<T, R>(entityClass, returnType, rootCriterion,
                orderings, maxResults, returnFields);
    }

    @Override
    public String toString() {
        return "Seeking [entityClass=" + entityClass + ", criteria=" + criteria
                + ", orderings=" + orderings + ", maxResults=" + maxResults
                + "]";
    }

}
