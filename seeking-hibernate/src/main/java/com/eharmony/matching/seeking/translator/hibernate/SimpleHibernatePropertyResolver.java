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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.eharmony.matching.seeking.query.geometry.XField;
import com.eharmony.matching.seeking.query.geometry.YField;
import com.eharmony.matching.seeking.translator.SimplePropertyResolver;
import com.google.common.base.Function;

/**
 * A Hibernate Property resolver. Hibernate will do further translation down the
 * line. This class just takes care of the @XField and @YField annotations
 * explicitly.
 */
public class SimpleHibernatePropertyResolver extends SimplePropertyResolver
        implements HibernatePropertyResolver {

    private final Function<Annotation, String> xFieldExtractor = new Function<Annotation, String>() {
        @Override
        public String apply(Annotation a) {
            return ((XField) a).value(); 
        }
    };
    
    private final Function<Annotation, String> yFieldExtractor = new Function<Annotation, String>() {
        @Override
        public String apply(Annotation a) {
            return ((YField) a).value(); 
        }
    };
    
    @Override
    public String resolveXField(String propertyName, Class<?> entityClass) {
        return resolveAnnotatedSubFieldName(propertyName, entityClass,
                XField.class, xFieldExtractor);
    }

    @Override
    public String resolveYField(String propertyName, Class<?> entityClass) {
        return resolveAnnotatedSubFieldName(propertyName, entityClass,
                YField.class, yFieldExtractor);
    }
    
    protected <A extends Annotation> String resolveAnnotatedSubFieldName(
            String propertyName, Class<?> entityClass, Class<A> annotationType,
            Function<Annotation, String> extractor) {
        try {
            Field field = entityClass.getDeclaredField(propertyName);
            Class<?> type = field.getType();
            Field[] fields = type.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(annotationType)) {
                    String fieldName = extractor.apply(f.getAnnotation(annotationType));
                    return propertyName + "."
                            + ("".equals(fieldName) ? f.getName() : fieldName);
                }
            }
            throw new IllegalArgumentException(propertyName
                    + " is not a valid geometric field");
        } catch (Exception e) {
            throw new IllegalArgumentException(propertyName
                    + " is not a valid geometric field", e);
        }
    }
    
}
