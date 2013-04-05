package com.eharmony.matching.seeking.test;

import com.eharmony.matching.seeking.translator.hibernate.HibernatePropertyResolver;

public class MockHibernatePropertyResolver implements HibernatePropertyResolver {
    
    @Override
    public String resolve(String fieldName, Class<?> entityClass) {
        return fieldName;
    }

    @Override
    public String resolveXField(String propertyName, Class<?> entityClass) {
        return propertyName + ".x";
    }

    @Override
    public String resolveYField(String propertyName, Class<?> entityClass) {
        return propertyName + ".y";
    }
    
}