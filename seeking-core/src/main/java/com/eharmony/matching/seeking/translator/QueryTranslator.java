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

package com.eharmony.matching.seeking.translator;

import com.eharmony.matching.seeking.query.Query;

/**
 * Description of a class that translates a generic Query to a datastore
 * specific query
 * 
 * @param <Q>
 *            the query type
 * @param <O>
 *            the ordering type
 * @param <P>
 *            the projected type
 */
public interface QueryTranslator<Q, O, P> {

    /**
     * Translate the generic Query
     * 
     * @param query
     * @return
     */
    public <T, R> Q translate(Query<T, R> query);

    /**
     * Translate the generic Orderings
     * 
     * @param query
     * @return
     */
    public <T, R> O translateOrder(Query<T, R> query);

    /**
     * Translate the "Projections"
     * 
     * @param query
     * @return
     */
    public <T, R> P translateProjection(Query<T, R> query);

}
