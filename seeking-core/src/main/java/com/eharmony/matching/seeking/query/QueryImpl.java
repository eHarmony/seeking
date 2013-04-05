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

package com.eharmony.matching.seeking.query;

import java.util.List;

import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Orderings;

/**
 * The default implementation of the generic Query interface
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public class QueryImpl<T, R> implements Query<T, R> {

    private final Class<T> entityClass;
    private final Class<R> returnType;
    private final Criterion criteria;
    private final Orderings orderings;
    private final Integer maxResults;
    private final List<String> returnFields;

    public QueryImpl(Class<T> entityClass, Class<R> returnType,
            Criterion criteria, Orderings orderings, Integer maxResults,
            List<String> returnFields) {
        this.entityClass = entityClass;
        this.returnType = returnType;
        this.criteria = criteria;
        this.returnFields = returnFields;
        this.orderings = orderings;
        this.maxResults = maxResults;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getEntityClass()
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getReturnType()
     */
    @Override
    public Class<R> getReturnType() {
        return returnType;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getReturnFields()
     */
    @Override
    public List<String> getReturnFields() {
        return returnFields;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getCriteria()
     */
    @Override
    public Criterion getCriteria() {
        return criteria;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getOrder()
     */
    @Override
    public Orderings getOrder() {
        return orderings;
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.query.Query#getMaxResults()
     */
    @Override
    public Integer getMaxResults() {
        return maxResults;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QueryImpl [entityClass=" + entityClass + ", criteria="
                + criteria + ", orderings=" + orderings + ", maxResults="
                + maxResults + "]";
    }
}
