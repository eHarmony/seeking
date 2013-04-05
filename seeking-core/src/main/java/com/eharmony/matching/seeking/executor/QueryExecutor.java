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

package com.eharmony.matching.seeking.executor;

import java.util.Collection;

import com.eharmony.matching.seeking.query.Query;

/**
 * An object oriented representation of a generic query executor that runs data
 * retrieval or modification statements against heterogeneous data stores. This
 * interface hides the datastore specific details of a query's execution from
 * the calling code.
 * 
 * 
 * NOTE: Future versions of this library may instead consume and produce
 * Iterators rather than Iterables. This would ultimately be safer (some data
 * stores yield iterators) and more efficient.
 * 
 */
public interface QueryExecutor {
    
    /**
     * Find records that satisfy the provided query.
     *
     * @return an {@link Iterable} of entity type T
     *
     * @throws DataStoreException if an error occurs accessing the underlying
     *         data store
     */
    <T, R> Iterable<R> find(Query<T, R> query);
    
    /**
     * Find one record that satisfies the provided query.
     *
     * @return an object of entity type T
     *
     * @throws DataStoreException if an error occurs accessing the underlying
     *         data store
     */
    <T, R> R findOne(Query<T, R> query);
    
    /**
     * Persist the entity to the datastore.
     *
     * @throws DataStoreException if an error occurs accessing the underlying
     *         data store
     */
    <T> T save(T entity);
    
    
    /**
     * Bulk save multiple entities to the datastore.
     * 
     * @param entities an iterable of entities to save
     *
     * @throws DataStoreException           if an error occurs accessing the
     *         underlying data store
     * @throws RetryableDataStoreException  if the error that occurred can be
     *         at least partially retried. Any value in
     *         {@link RetryableDataStoreException#getRetryableStatus()} shall
     *         be of type {@link Collection} of T.
     */
    <T> Iterable<T> save(Iterable<T> entities);

}
