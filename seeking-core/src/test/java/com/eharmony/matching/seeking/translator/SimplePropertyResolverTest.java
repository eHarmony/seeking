package com.eharmony.matching.seeking.translator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimplePropertyResolverTest {

    private final SimplePropertyResolver resolver = new SimplePropertyResolver();
    private final String propertyName = "propertyName";

    @Test
    public void resolve() {
        assertEquals(propertyName, resolver.resolve(propertyName, Object.class));
    }
    
}
