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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.query.MorphiaIterator;
import com.google.code.morphia.utils.ReflectionUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

/**
 * Transform the results returned from MongoDB via Morphia as they are iterated.
 * 
 * @param <T>
 *            the return type
 */
public class MongoResults<T> extends MorphiaIterator<T, T> implements
        Iterable<T>, Iterator<T> {

    private static final Log log = LogFactoryImpl.getLog(MongoResults.class);
    private final Class<T> returnType;
    private final boolean isPrimitiveLike;

    public MongoResults(DBCursor cursor, Mapper m, Class<T> clazz,
            EntityCache cache) {
        super(cursor, m, clazz, null, cache);
        this.returnType = clazz;
        this.isPrimitiveLike = ReflectionUtils.isPrimitiveLike(clazz);
    }

    @Override
    protected T processItem(BasicDBObject dbObj) {
        if (isPrimitiveLike) {
            Object item = dbObj.values().iterator().next();
            try {
                return returnType.cast(item);
            } catch (ClassCastException e) {
                log.error("Unable to cast "
                        + (item != null ? item.toString() : "null") + " to "
                        + returnType.toString());
                throw e;
            }
        } else {
            return super.processItem(dbObj);
        }
    }

}