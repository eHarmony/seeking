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

import com.google.code.morphia.Key;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.mapping.cache.EntityCacheStatistics;

/**
 * The Morphia Entity cache cannot handle a high throughput of unique objects
 * and ends up consuming too much memory. This is a non-functional mapped entity
 * cache.
 */
public class NonFunctionalEntityCache implements EntityCache {

    private final EntityCacheStatistics stats = new EntityCacheStatistics();

    @Override
    public Boolean exists(Key<?> arg0) {
        return false;
    }

    @Override
    public void flush() {
    }

    @Override
    public <T> T getEntity(Key<T> arg0) {
        return null;
    }

    @Override
    public <T> T getProxy(Key<T> arg0) {
        return null;
    }

    @Override
    public void notifyExists(Key<?> arg0, boolean arg1) {
    }

    @Override
    public <T> void putEntity(Key<T> arg0, T arg1) {
    }

    @Override
    public <T> void putProxy(Key<T> arg0, T arg1) {
    }

    @Override
    public EntityCacheStatistics stats() {
        return stats;
    }

}
