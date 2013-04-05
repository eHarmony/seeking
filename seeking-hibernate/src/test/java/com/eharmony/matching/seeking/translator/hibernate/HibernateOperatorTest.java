package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.matching.seeking.translator.hibernate.HibernateOperator;


public class HibernateOperatorTest {

    @Test
    public void testToString() {
        HibernateOperator[] operators = HibernateOperator.values();

        for (HibernateOperator operator : operators) {
            assertEquals(operator.symbol(), operator.toString());
        }
    }
    
}
