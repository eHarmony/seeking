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

package com.eharmony.matching.seeking.executor.mongodb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.eharmony.matching.seeking.executor.DataStoreException;
import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.executor.RetryableDataStoreException;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.translator.EntityResolver;
import com.eharmony.matching.seeking.translator.mongodb.MongoQueryTranslator;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * A QueryExecutor implementation that interacts with MongoDB
 */
public class MongoQueryExecutor implements QueryExecutor {

    private static final Log log = LogFactoryImpl.getLog(MongoQueryExecutor.class);

    // a value of 0 will use the server side default (currently 100)
    private static final int DEFAULT_MONGODB_BATCH_SIZE = 0;

    private final MongoQueryTranslator queryTranslator;
    private final Mapper mapper;
    private final EntityCache cache;
    private final DB db;
    private final EntityResolver entityResolver;
    private final Map<Class<?>,DBCollection> collections;
    private final WriteConcern writeConcern;
    private final Set<Class<?>> mappedClasses = Sets.newHashSet();
    private final int batchSize;


    public MongoQueryExecutor(
            DB db,
            WriteConcern writeConcern,
            MongoQueryTranslator queryTranslator,
            EntityResolver entityResolver,
            final int batchSize) {
        if (null == db) {
            throw new IllegalArgumentException("null database");
        }
        this.db = db;

        /* Keep reference rather than making defensive copy: write concern
         * can be (and is) subclassed.
         */
        if (null == writeConcern) {
            throw new IllegalArgumentException("null write concern");
        }
        this.writeConcern = writeConcern;

        if (null == queryTranslator) {
            throw new IllegalArgumentException("null query translator");
        }
        this.queryTranslator = queryTranslator;

        if (null == entityResolver) {
            throw new IllegalArgumentException("null entity resolver");
        }
        this.entityResolver = entityResolver;

        this.mapper = new Mapper();
        this.collections = Maps.newConcurrentMap();
        // TODO: fix the entity cache?
        this.cache = new NonFunctionalEntityCache();
        this.batchSize = batchSize;
    }
    
    public MongoQueryExecutor(
            DB db,
            WriteConcern writeConcern,
            MongoQueryTranslator queryTranslator,
            EntityResolver entityResolver) {
        this(db, writeConcern, queryTranslator, entityResolver,
                DEFAULT_MONGODB_BATCH_SIZE);
    }


    public MongoQueryExecutor(
            Mongo mongo,
            String database,
            WriteConcern writeConcern,
            MongoQueryTranslator queryTranslator,
            EntityResolver entityResolver) {
        this(mongo.getDB(database), writeConcern, queryTranslator, entityResolver);
    }
    
    public MongoQueryExecutor(
            Mongo mongo,
            String database,
            WriteConcern writeConcern,
            MongoQueryTranslator queryTranslator,
            EntityResolver entityResolver,
            final int batchSize) {
        this(mongo.getDB(database), writeConcern, queryTranslator, entityResolver, batchSize);
    }
    
    public DB getDB() {
        return db;
    }
    
    public DBCollection getCollection(Class<?> entityClass) {
        // we don't need to synchronize because we don't care if this happens
        // more than once
        if (!collections.containsKey(entityClass)) {
            final String collection = this.entityResolver.resolve(entityClass);
            collections.put(entityClass, db.getCollection(collection));
        }
        return collections.get(entityClass);
    }


    public <T, R> void remove(Query<T, R> query) {
        try {
            getCollection(query.getEntityClass()).remove(
                    this.queryTranslator.translate(query),
                    this.writeConcern);
        } catch (final MongoException mx) {
            throw new DataStoreException(mx.getMessage(), mx);
        }
    }


    public void removeAll(Class<?> entityClass) {
        try {
            getCollection(entityClass).remove(
                    new BasicDBObject(),
                    this.writeConcern);
        } catch (final MongoException mx) {
            throw new DataStoreException(mx.getMessage(), mx);
        }
    }


    private <T, R> DBCursor translate(Query<T, R> query) {
        mapClasses(query.getEntityClass());
        final DBObject translated = queryTranslator.translate(query);
        if (log.isDebugEnabled()) {
            log.debug(translated);
        }
        final DBObject fields = queryTranslator.translateProjection(query);
        final DBCursor cursor = find(getCollection(query.getEntityClass()),
                translated, fields);
        cursor.sort(queryTranslator.translateOrder(query));
        if (query.getMaxResults() != null) {
            cursor.limit(query.getMaxResults());
        }
        return cursor;
    }

    private void mapClasses(Class<?>... classes) {
        for (final Class<?> clazz : classes) {
            /*
             * Should we synchronize this? The overhead of mapping a class twice
             * (in the unlikely event that it happens) is small compared to the
             * overhead of hitting a synchronize block every time we perform a
             * query
             */
            if (!mappedClasses.contains(clazz)) {
                mapper.addMappedClass(clazz);
                mappedClasses.add(clazz);
            }
        }
    }

    private <T, R> MongoResults<R> fetch(DBCursor cursor, Query<T, R> query) {
        if (batchSize != DEFAULT_MONGODB_BATCH_SIZE) {
            cursor = cursor.batchSize(batchSize);
        }
        return new MongoResults<R>(cursor, mapper, query.getReturnType(), cache);
    }


    @VisibleForTesting
    protected DBCursor find(DBCollection collection, DBObject query, DBObject fields) {
        // Collection.find is final, this is wrapped to allow unit testing.
        return collection.find(query, fields);
    }


    @VisibleForTesting
    protected WriteResult save(DBCollection collection, DBObject entity) {
        return collection.save(entity, this.writeConcern);
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#find(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> Iterable<R> find(Query<T, R> query) {
        try {
            return fetch(translate(query), query);
        } catch (final MongoException mx) {
            throw new DataStoreException(mx.getMessage(), mx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#findOne(com.eharmony.matching.seeking.query.Query)
     */
    @Override
    public <T, R> R findOne(Query<T, R> query) {
        try {
            final MongoResults<R> results = fetch(translate(query).limit(1), query);
            return results.hasNext() ? results.next() : null;
        } catch (final MongoException mx) {
            throw new DataStoreException(mx.getMessage(), mx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#save(java.lang.Object)
     */
    @Override
    public <T> T save(T entity) {
        try {
            final DBObject mapped = mapper.toDBObject(entity);
            save(getCollection(entity.getClass()), mapped);
            return entity;
        } catch (final MongoException mx) {
            throw new DataStoreException(mx.getMessage(), mx);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.executor.QueryExecutor#save(java.lang.Iterable)
     */
    @Override
    public <T> Iterable<T> save(Iterable<T> entities) {
        final Collection<T> failed = Lists.newArrayList();
        MongoException mostRecentMongoException = null;

        final Collection<T> saved = Lists.newArrayList();
        for (final T entity : entities) {
            try {
                saved.add(save(entity));
            } catch (final MongoException mx) {
                mostRecentMongoException = mx;
                failed.add(entity);
            }
        }

        if (null != mostRecentMongoException) {
            assert !failed.isEmpty();
            throw new RetryableDataStoreException(mostRecentMongoException, failed);
        }
        assert failed.isEmpty();
        return saved;
    }

}
