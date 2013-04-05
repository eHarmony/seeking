package com.eharmony.matching.seeking.handler.hibernate;

import org.junit.Test;

public class ContainsExpressionNonHandlerTest {

    private final ContainsExpressionNonHandler<String> handler = new ContainsExpressionNonHandler<String>();

    @Test(expected = UnsupportedOperationException.class)
    public void contains() {
        handler.contains("name", new Object[] { "a", "b" });
    }

}
