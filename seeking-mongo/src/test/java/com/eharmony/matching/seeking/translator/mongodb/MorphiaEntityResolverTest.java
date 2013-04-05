package com.eharmony.matching.seeking.translator.mongodb;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.eharmony.matching.seeking.test.TestClass;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.mapping.Mapper;

public class MorphiaEntityResolverTest {
    
    @SuppressWarnings("unused")
    @Entity("testclass")
    public static class TestMappedClass {
        @Id
        private int id;
    }
    
    @SuppressWarnings("unused")
    public static class UnnamedMappedClass {
        @Id
        private int id;
    }
    
    @Before
    public void before() {
        mapper.addMappedClass(TestClass.class);
        mapper.addMappedClass(UnnamedMappedClass.class);
    }
    
    private final Mapper mapper = new Mapper();
    private final MorphiaEntityResolver resolver = new MorphiaEntityResolver(mapper);
    
    @Test
    public void resolve_collectionName() {
        assertEquals("testclass", resolver.resolve(TestMappedClass.class));
    }
    
    @Test
    public void resolve_collectionName_unnamed() {
        assertEquals("UnnamedMappedClass", resolver.resolve(UnnamedMappedClass.class));
    }
}
