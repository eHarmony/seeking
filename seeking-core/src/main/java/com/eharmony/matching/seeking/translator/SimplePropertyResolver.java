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

/**
 * The Simple Property Resolver uses the provided class field name as the name
 * for the field/property/column in the datastore.
 */
public class SimplePropertyResolver implements PropertyResolver {

    /*
     * (non-Javadoc)
     * @see com.eharmony.matching.seeking.translator.PropertyResolver#resolve(java.lang.String, java.lang.Class)
     */
    @Override
    public String resolve(String fieldName, Class<?> entityClass) {
        return fieldName;
    }
    
}
