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

package com.eharmony.matching.seeking.translator.mongodb;

import java.util.List;

import com.eharmony.matching.seeking.translator.PropertyResolver;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.Mapper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Resolve the field names of entity class variables using Morphia's mapper.
 */
public class MorphiaPropertyResolver implements PropertyResolver {

    private final Mapper mapper;
    private final Splitter splitter = Splitter.on('.');
    private final Joiner joiner = Joiner.on('.');
    
    public MorphiaPropertyResolver() {
        this(new Mapper());
    }

    public MorphiaPropertyResolver(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String resolve(String fieldName, Class<?> entityClass) {
        Iterable<String> fieldNames = splitter.split(fieldName);
        List<String> resolved = Lists.newArrayList();
        Class<?> lastClass = entityClass;
        for (String name : fieldNames) {
            MappedField mappedField = mapper.getMappedClass(lastClass)
                    .getMappedFieldByJavaField(name);
            if (mappedField != null) {
                lastClass = mappedField.getConcreteType();
                resolved.add(mappedField.getNameToStore());
            } else {
                resolved.add(name);
            }
        }
        return joiner.join(resolved);
    }
    
}
