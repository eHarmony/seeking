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
 * A generic, object oriented representation of a query
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public interface Query<T, R> {

    /**
     * Get the queried entity type.
     * 
     * @return the entity class being queried
     */
    public Class<T> getEntityClass();

    /**
     * Get the return entity type.
     * 
     * @return the class of the desired return type
     */
    public Class<R> getReturnType();

    /**
     * The list of properties to return. An empty collection means all
     * properties.
     * 
     * @return the fields to be returned from the query
     */
    public List<String> getReturnFields();

    /**
     * Get the query criteria. The top level object can either be a single
     * Criterion or a Junction of multiple nested Criterion objects.
     * 
     * @return the root criterion node
     */
    public Criterion getCriteria();

    /**
     * Get the Order clauses.
     * 
     * @return the ordering clauses
     */
    public Orderings getOrder();

    /**
     * Get the max desired results. Null signifies no maximum.
     * 
     * @return the maximum number of results or null if no maximum
     * 
     *         TODO: use Optional<Integer> rather than rely on null?
     */
    public Integer getMaxResults();
}
