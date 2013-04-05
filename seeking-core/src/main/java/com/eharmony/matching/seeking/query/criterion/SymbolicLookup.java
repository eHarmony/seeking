/*
 *  Copyright 2012 eHarmony, Inc
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.eharmony.matching.seeking.query.criterion;

/**
 * A tool for resolving a Symbolic object from its String symbol 
 */
import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SymbolicLookup implements Function<Symbolic, String> {

    public String apply(Symbolic symbolic) {
        return symbolic.symbol();
    }
    
    public static <T extends Symbolic> ImmutableMap<String,T> map(T[] values) {
        return Maps.uniqueIndex(Arrays.asList(values), new SymbolicLookup());
    }
    
    public static <T extends Symbolic> T resolve(String symbol,
            ImmutableMap<String, T> map, Class<T> clss) {
        T symbolic = map.get(symbol);
        if (symbolic == null) {
            throw new IllegalArgumentException(symbol + " is not a valid "
                    + clss.getSimpleName() + " symbol");
        }
        return symbolic;
    }

}
