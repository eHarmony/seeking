package com.eharmony.matching.seeking.translator.solr;

import static org.junit.Assert.assertEquals;

import org.apache.solr.client.solrj.beans.Field;
import org.junit.Test;

public class SolrjPropertyResolverTest {

    @SuppressWarnings("unused")
    public static class TestMappedClass {
        private int id;
        private String name;
        @Field("thatProperty")
        private String thisProperty;
    }

    private final SolrjPropertyResolver resolver = new SolrjPropertyResolver();

    @Test
    public void resolve_name() {
        assertEquals("name", resolver.resolve("name", TestMappedClass.class));
    }

    @Test
    public void resolve_thisProperty() {
        assertEquals("thatProperty",
                resolver.resolve("thisProperty", TestMappedClass.class));
    }

}
