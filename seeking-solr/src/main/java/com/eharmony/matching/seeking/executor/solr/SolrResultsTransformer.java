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

package com.eharmony.matching.seeking.executor.solr;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Transform the results returned from Solr into the desire return type.
 */
public class SolrResultsTransformer {
    
    @SuppressWarnings("unchecked")
    private static final Set<Class<?>> valueTypes = new HashSet<Class<?>>(
            Arrays.asList(boolean.class, byte.class, char.class, double.class,
                    float.class, int.class, long.class, short.class,
                    Boolean.class, Byte.class, Character.class, Date.class,
                    Double.class, Float.class, Integer.class, Long.class,
                    Short.class, String.class));
    private final DocumentObjectBinder binder = new DocumentObjectBinder();
    
    public static <T> boolean isValueType(final Class<T> type) {
        return type == null ? false : valueTypes.contains(type);        
    }

    public <T> List<T> transform(SolrDocumentList results, final Class<T> returnType) {
        if (isValueType(returnType)) {
            return Lists.transform(results, new Function<SolrDocument,T>() {
                @Override
                public T apply(SolrDocument document) {
                    Iterator<Object> iterator = document.values().iterator();
                    return iterator.hasNext() ? returnType.cast(iterator.next()) : null;
                }
            });
        } else {
            return binder.getBeans(returnType, results);
        }
    }
    
}