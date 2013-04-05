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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;

import com.eharmony.matching.seeking.executor.DataStoreException;
import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.mapper.HibernateProjectedResultMapper;
import com.eharmony.matching.seeking.mapper.ProjectedResultMapper;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.translator.hibernate.HibernateQueryTranslator;

/**
 * A QueryExecutor implementation that uses Hibernate to interact with a SQL Store
 */
public class HibernateQueryExecutor implements QueryExecutor {
    
    private static final Log log = LogFactoryImpl.getLog(HibernateQueryExecutor.class);
    private final SessionFactory sessionFactory;
    private final HibernateQueryTranslator queryTranslator;
    private final HibernateProjectedResultMapper mapper;

    public HibernateQueryExecutor(SessionFactory sessionFactory,
            HibernateQueryTranslator queryTranslator,
            ProjectedResultMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.queryTranslator = queryTranslator;
        this.mapper = new HibernateProjectedResultMapper(mapper);
    }
    
    public HibernateQueryExecutor(SessionFactory sessionFactory,
            HibernateQueryTranslator queryTranslator) {
        this(sessionFactory, queryTranslator, new ProjectedResultMapper());
    }
    
    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#find(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> Iterable<R> find(Query<T, R> query) {
        try {
            return mapper.mapResults(getCriteria(query).list(), query);
        } catch (final HibernateException hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#findOne(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> R findOne(Query<T, R> query) {
        try {
            return mapper.mapResult(getCriteria(query).setMaxResults(1)
                    .uniqueResult(), query);
        } catch (final HibernateException hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#save(java.lang.Object)
     */
    @Override
    @Transactional
    public <T> T save(T entity) {
        try {
            getSession().save(entity);
            return entity;
        } catch (final HibernateException hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#save(java.lang.Iterable)
     */
    @Override
    @Transactional
    public <T> Iterable<T> save(Iterable<T> entities) {
        try {
            final List<T> saved = new ArrayList<T>();
            for (final T entity : entities) {
                saved.add(save(entity));
            }
            return saved;
        } catch (final HibernateException hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
    }

    protected <T, R> Criteria getCriteria(Query<T, R> query) {
        final Criterion translated = queryTranslator.translate(query);
        if (log.isDebugEnabled()) {
            log.debug(translated);
        }
        final Criteria criteria = getSession().createCriteria(query.getEntityClass());
        criteria.add(translated);
        final List<Order> orders = queryTranslator.translateOrder(query).get();
        for (final Order order : orders) {
            criteria.addOrder(order);
        }
        final Projection projection = queryTranslator.translateProjection(query);
        if (projection != null) {
            criteria.setProjection(projection);
        }
        if (query.getMaxResults() != null) {
            criteria.setMaxResults(query.getMaxResults());
        }
        return criteria;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    
    protected HibernateProjectedResultMapper getMapper() {
        return mapper;
    }
    
}
