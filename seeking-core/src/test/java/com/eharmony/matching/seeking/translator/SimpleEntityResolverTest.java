package com.eharmony.matching.seeking.translator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimpleEntityResolverTest {

    private final SimpleEntityResolver resolver = new SimpleEntityResolver();

    @Test
    public void resolve_collectionName() {
        assertEquals("Object", resolver.resolve(Object.class));
    }

}
