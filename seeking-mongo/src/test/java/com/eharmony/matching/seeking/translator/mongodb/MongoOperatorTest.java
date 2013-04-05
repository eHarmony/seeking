package com.eharmony.matching.seeking.translator.mongodb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MongoOperatorTest {

    @Test
    public void testToString() {
        MongoOperator[] operators = MongoOperator.values();

        for (MongoOperator operator : operators) {
            assertEquals(operator.symbol(), operator.toString());
        }
    }

}
