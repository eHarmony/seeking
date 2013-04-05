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

package com.eharmony.matching.seeking.query.geometry;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Generic math for Numbers.  Used in the 2D expressions and boxes.
 */
public class GenericMath {

    /*
     * TODO: need a better, more elegant, and better performing solution for this
     */
    
    @SuppressWarnings("unchecked")
    public static <N extends Number, M extends Number> N add(N a, M b) {
        if (a instanceof Double) {
            return (N) Double.valueOf(a.doubleValue() + b.doubleValue());
        } else if (a instanceof Float) {
            return (N) Float.valueOf(a.floatValue() + b.floatValue());
        } else if (a instanceof Long) {
            return (N) Long.valueOf(a.longValue() + b.longValue());
        } else if (a instanceof BigInteger) {
            return (N) bigInt(a).add(bigInt(b));
        } else if (a instanceof BigDecimal) {
            return (N) bigDec(a).add(bigDec(b));
        } else {
            // assume it's an Integer
            return (N) Integer.valueOf(a.intValue() + b.intValue());
        }
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number, M extends Number> N subtract(N a, M b) {
        if (a instanceof Double) {
            return (N) Double.valueOf(a.doubleValue() - b.doubleValue());
        } else if (a instanceof Float) {
            return (N) Float.valueOf(a.floatValue() - b.floatValue());
        } else if (a instanceof Long) {
            return (N) Long.valueOf(a.longValue() - b.longValue());
        } else if (a instanceof BigInteger) {
            return (N) bigInt(a).subtract(bigInt(b));
        } else if (a instanceof BigDecimal) {
            return (N) bigDec(a).subtract(bigDec(b));
        } else {
            // assume it's an Integer
            return (N) Integer.valueOf(a.intValue() - b.intValue());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <N extends Number, M extends Number> N multiply(N a, M b) {
        if (a instanceof Double) {
            return (N) Double.valueOf(a.doubleValue() * b.doubleValue());
        } else if (a instanceof Float) {
            return (N) Float.valueOf(a.floatValue() * b.floatValue());
        } else if (a instanceof Long) {
            return (N) Long.valueOf(a.longValue() * b.longValue());
        } else if (a instanceof BigInteger) {
            return (N) bigInt(a).multiply(bigInt(b));
        } else if (a instanceof BigDecimal) {
            return (N) bigDec(a).multiply(bigDec(b));
        } else {
            // assume it's an Integer
            return (N) Integer.valueOf(a.intValue() * b.intValue());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <N extends Number, M extends Number> N divide(N a, M b) {
        if (a instanceof Double) {
            return (N) Double.valueOf(a.doubleValue() / b.doubleValue());
        } else if (a instanceof Float) {
            return (N) Float.valueOf(a.floatValue() / b.floatValue());
        } else if (a instanceof Long) {
            return (N) Long.valueOf(a.longValue() / b.longValue());
        } else if (a instanceof BigInteger) {
            return (N) bigInt(a).divide(bigInt(b));
        } else if (a instanceof BigDecimal) {
            return (N) bigDec(a).divide(bigDec(b));
        } else {
            // assume it's an Integer
            return (N) Integer.valueOf(a.intValue() / b.intValue());
        }
    }

    public static <N extends Comparable<N>> N min(N a, N b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static <N extends Comparable<N>> N max(N a, N b) {
        return a.compareTo(b) > 0 ? a : b;
    }
    
    public static BigInteger bigInt(Number n) {
        if (n instanceof BigInteger) {
            return (BigInteger) n;
        } else {
            return BigInteger.valueOf(n.longValue());
        }
    }
    
    public static BigDecimal bigDec(Number n) {
        if (n instanceof BigDecimal) {
            return (BigDecimal) n;
        } else {
            return new BigDecimal(n.doubleValue());
        }
    }
    
}
