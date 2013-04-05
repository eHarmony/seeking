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

package com.eharmony.matching.seeking.executor.hibernate;

import org.hibernate.ScrollMode;
import org.hibernate.SessionFactory;

import com.eharmony.matching.seeking.mapper.ProjectedResultMapper;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.translator.hibernate.HibernateQueryTranslator;

/**
 * An extension of the Hibernate Query Executor that performs iterative finds
 * using Hibernate's scroll() method.
 * 
 */
public class IterativeHibernateQueryExecutor extends HibernateQueryExecutor {
    
    public IterativeHibernateQueryExecutor(SessionFactory sessionFactory,
            HibernateQueryTranslator queryTranslator,
            ProjectedResultMapper mapper) {
        super(sessionFactory, queryTranslator, mapper);
    }
    
    public IterativeHibernateQueryExecutor(SessionFactory sessionFactory,
            HibernateQueryTranslator queryTranslator) {
        super(sessionFactory, queryTranslator);
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.hibernate.HibernateQueryExecutor#find(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> Iterable<R> find(Query<T, R> query) {
        return getMapper().mapResults(
                getCriteria(query).scroll(ScrollMode.FORWARD_ONLY), query);
    }
}
