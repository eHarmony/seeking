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

package com.eharmony.matching.seeking.translator.hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eharmony.matching.seeking.translator.EntityResolver;
import com.eharmony.matching.seeking.translator.SimpleEntityResolver;
import com.google.common.base.Strings;

/**
 * A Hibernate Entity resolver that uses the @Table or @Entity annotation to
 * resolve the table name. Falls back on the simple class name.
 */
public class SimpleHibernateEntityResolver extends SimpleEntityResolver implements EntityResolver {

    @Override
    public String resolve(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table annotation = entityClass.getAnnotation(Table.class);
            if (!Strings.isNullOrEmpty(annotation.name())) {
                return annotation.name();
            }
        }
        if (entityClass.isAnnotationPresent(Entity.class)) {
            Entity annotation = entityClass.getAnnotation(Entity.class);
            if (!Strings.isNullOrEmpty(annotation.name())) {
                return annotation.name();
            }
        }
        return entityClass.getSimpleName();
    }

}
