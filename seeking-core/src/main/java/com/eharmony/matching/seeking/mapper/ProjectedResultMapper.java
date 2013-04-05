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

package com.eharmony.matching.seeking.mapper;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

/**
 * Map an array of properties and an array of associated property names to an
 * Object of a specified type.
 */
public class ProjectedResultMapper {
    
    private final ObjectMapper mapper;
    
    public ProjectedResultMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public ProjectedResultMapper() {
        this(new ObjectMapper());
    }
    
    protected Map<String,Object> propertyMap(Object[] properties,
            String[] propertyNames) {
        if (properties.length != propertyNames.length) {
            throw new IllegalArgumentException("The number of properties ("
                    + properties.length
                    + ") must match the number of property names ("
                    + propertyNames.length + ")");
        }
        int n = properties.length;
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(n);
        for (int i = 0; i < n; i++) {
            map.put(propertyNames[i], properties[i]);
        }
        return map;
    }
    
    /**
     * Map an Object or an Object array with an associated array of property
     * names to the provided type.
     * 
     * @param resultClass
     *            the desired mapped type
     * @param properties
     *            the property or properties to be mapped
     * @param propertyNames
     *            an array of associated property names
     * @return the mapped object of type R
     * @throws ClassCastException
     */
    public <R> R mapTo(Class<R> resultClass, Object properties,
            String[] propertyNames) {
        return propertyNames.length == 1
            ? resultClass.cast(properties)
            : mapper.convertValue(propertyMap((Object[]) properties, propertyNames), resultClass);
    }

}