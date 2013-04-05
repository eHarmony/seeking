package com.eharmony.matching.seeking.translator.mongodb;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.eharmony.matching.seeking.test.TestClass;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.mapping.Mapper;

public class MorphiaPropertyResolverTest {
    
    @SuppressWarnings("unused")
    public static class TestMappedEmbeddedClass {
        private int a;
        @Property("bee")
        private int b;
    }
    
    @SuppressWarnings("unused")
    public static class TestMappedClass {
        @Id
        private int id;
        private String name;
        @Property("thatProperty")
        private String thisProperty;
        @Embedded
        private TestMappedEmbeddedClass t;
        @Embedded("teatwo")
        private TestMappedEmbeddedClass t2;
    }
    
    @Before
    public void before() {
        mapper.addMappedClass(TestClass.class);
    }
    
    private final Mapper mapper = new Mapper();
    private final MorphiaPropertyResolver resolver = new MorphiaPropertyResolver(
            mapper);
    
    @Test
    public void resolve_id() {
        assertEquals("_id", resolver.resolve("id", TestMappedClass.class));
    }
    
    @Test
    public void resolve_name() {
        assertEquals("name", resolver.resolve("name", TestMappedClass.class));
    }
    
    @Test
    public void resolve_thisProperty() {
        assertEquals("thatProperty",
                resolver.resolve("thisProperty", TestMappedClass.class));
    }
    
    @Test
    public void resolve_t() {
        assertEquals("t", resolver.resolve("t", TestMappedClass.class));
        assertEquals("t.a", resolver.resolve("t.a", TestMappedClass.class));
        assertEquals("t.bee", resolver.resolve("t.b", TestMappedClass.class));
    }
    
    @Test
    public void resolve_t2() {
        assertEquals("teatwo", resolver.resolve("t2", TestMappedClass.class));
        assertEquals("teatwo.a", resolver.resolve("t2.a", TestMappedClass.class));
        assertEquals("teatwo.bee", resolver.resolve("t2.b", TestMappedClass.class));
    }
    
}
