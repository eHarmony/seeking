package com.eharmony.matching.seeking.query.criterion;

import static org.junit.Assert.*;
import org.junit.Test;

public class OperatorTest {

    @Test
    public void fromString() {
        Operator[] operators = Operator.values();
        
        for (Operator operator : operators) {
            assertEquals(operator, Operator.fromString(operator.symbol()));
        }
    }
    
    @Test
    public void testToString() {
        Operator[] operators = Operator.values();
        
        for (Operator operator : operators) {
            assertEquals(operator.symbol(), operator.toString());
        }
    }
    
}
