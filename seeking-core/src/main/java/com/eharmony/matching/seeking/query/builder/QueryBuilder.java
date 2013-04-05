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

package com.eharmony.matching.seeking.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.QueryImpl;
import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.Orderings;
import com.eharmony.matching.seeking.query.criterion.Restrictions;

/**
 * Builder for Query objects
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public class QueryBuilder<T, R> {

    private final Class<T> entityClass;
    private final Class<R> returnType;
    private List<Criterion> criteria = new ArrayList<Criterion>();
    private Orderings orderings = new Orderings();
    private Integer maxResults;
    private List<String> returnFields = Collections.emptyList();

    public QueryBuilder(Class<T> entityClass, Class<R> returnType) {
        this.entityClass = entityClass;
        this.returnType = returnType;
    }

    public static <T, R> QueryBuilder<T, R> builderFor(Class<T> entityClass,
            Class<R> returnType, String... returnFields) {
        return new QueryBuilder<T, R>(entityClass, returnType)
                .setReturnFields(returnFields);
    }

    public static <T> QueryBuilder<T, T> builderFor(Class<T> entityClass) {
        return new QueryBuilder<T, T>(entityClass, entityClass);
    }

    /**
     * Add a restriction to constrain the results to be retrieved
     * 
     * @param criterion
     * @return the builder
     */
    public QueryBuilder<T, R> add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    /**
     * Add an ordering to the result set
     * 
     * @param order
     * @return the builder
     */
    public QueryBuilder<T, R> addOrder(Ordering... orders) {
        for (Ordering order : orders) {
            orderings.add(order);
        }
        return this;
    }

    /**
     * Specify the fields to return
     * 
     * @param returnFields
     * @return the builder
     */
    public QueryBuilder<T, R> setReturnFields(String... returnFields) {
        this.returnFields = Arrays.asList(returnFields);
        return this;
    }

    /**
     * Set a limit upon the number of objects to be retrieved by this query
     * 
     * @param maxResults
     * @return the builder
     */
    public QueryBuilder<T, R> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

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
        return "QueryBuilder [entityClass=" + entityClass + ", criteria="
                + criteria + ", orderings=" + orderings + ", maxResults="
                + maxResults + "]";
    }

}
