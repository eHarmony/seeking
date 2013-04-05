package com.eharmony.matching.seeking.query.criterion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SymbolicLookupTest {
    
    public static enum TestSymbol implements Symbolic {
        A("a"),
        B("b"),
        C("c"),
        D("d"),
        E("e");
        
        private final String symbol;
        
        private TestSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }
    final ImmutableMap<String,TestSymbol> map = SymbolicLookup.map(TestSymbol.values());
    
    @Test
    public void apply() {
        final SymbolicLookup lookup = new SymbolicLookup();
        for (TestSymbol t : TestSymbol.values()) {
            assertEquals(t.symbol(), lookup.apply(t));
        }
    }
    
    @Test
    public void map() {
        for (TestSymbol t : TestSymbol.values()) {
            assertEquals(t, map.get(t.symbol()));
        }
    }
    
    @Test
    public void resolve() {
        for (TestSymbol t : TestSymbol.values()) {
            assertEquals(t, SymbolicLookup.resolve(t.symbol(), map, TestSymbol.class));
        }
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void resolve_IllegalArgumentException() {
        SymbolicLookup.resolve("f", map, TestSymbol.class);
    }

}
