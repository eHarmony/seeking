package com.eharmony.matching.seeking.translator.solr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.translator.solr.SolrOperator;

public class SolrOperatorTest {

    @Test
    public void testToString() {
        SolrOperator[] operators = SolrOperator.values();

        for (SolrOperator operator : operators) {
            assertEquals(operator.symbol(), operator.toString());
        }
    }

}
