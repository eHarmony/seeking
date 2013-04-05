package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertEquals;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import com.eharmony.matching.seeking.translator.hibernate.SimpleHibernatePropertyResolverTest.LatLonCustom;

@SuppressWarnings("unused")
public class SimpleHibernateEntityResolverTest {
    
    @Entity(name = "without")
    public static class No {
        private Object property;
    }
    
    @Table(name = "with")
    public static class Yes {
        private String name;
    }
    
    public static class Maybe {
        private String name;
        private LatLonCustom latlon;    
    }
    
    private final SimpleHibernateEntityResolver resolver = new SimpleHibernateEntityResolver();

    @Test
    public void resolve_collectionName_entity() {
        assertEquals("without", resolver.resolve(No.class));
    }
    
    @Test
    public void resolve_collectionName_table() {
        assertEquals("with", resolver.resolve(Yes.class));
    }
    
    @Test
    public void resolve_collectionName_default() {
        assertEquals("Maybe", resolver.resolve(Maybe.class));
    }
}
