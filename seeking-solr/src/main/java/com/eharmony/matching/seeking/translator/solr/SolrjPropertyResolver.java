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

package com.eharmony.matching.seeking.translator.solr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.solr.client.solrj.beans.Field;

import com.eharmony.matching.seeking.translator.PropertyResolver;

/**
 * Resolve the field names of entity class variables using SolrJ's @Field
 * annotation.
 * 
 * TODO: implement a solution to "flatten" hierarchical object models because
 * Solr doesn't support nested documents and, as a consequence, the Solr
 * implementation of Seeking only supports flat objects.
 */
public class SolrjPropertyResolver implements PropertyResolver {
    
    private static final Log log = LogFactoryImpl.getLog(SolrjPropertyResolver.class);
    
    @Override
    public String resolve(String fieldName, Class<?> entityClass) {
        try {
            java.lang.reflect.Field field = entityClass.getDeclaredField(fieldName);
            if (field.isAnnotationPresent(Field.class)) {
                return field.getAnnotation(Field.class).value();
            }
        } catch (Exception e) {
            log.warn("Unabled to resolve name for field: " + fieldName, e);
        }
        return fieldName;
    }
    
}
