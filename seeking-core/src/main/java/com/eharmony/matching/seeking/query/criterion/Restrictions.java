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

import java.util.Collection;

import com.eharmony.matching.seeking.query.criterion.expression.Distance2dExpression;
import com.eharmony.matching.seeking.query.criterion.expression.EqualityExpression;
import com.eharmony.matching.seeking.query.criterion.expression.NativeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.RangeExpression;
import com.eharmony.matching.seeking.query.criterion.expression.SetExpression;
import com.eharmony.matching.seeking.query.criterion.expression.UnaryExpression;
import com.eharmony.matching.seeking.query.criterion.junction.Conjunction;
import com.eharmony.matching.seeking.query.criterion.junction.Disjunction;

/**
 * Hibernate style Restriction expression builder
 */
public class Restrictions {

    private Restrictions() {
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression eq(String propertyName, Object value) {
        return new EqualityExpression(Operator.EQUAL, propertyName, value);
    }
    
    /**
     * Apply a "not equal" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression ne(String propertyName, Object value) {
        return new EqualityExpression(Operator.NOT_EQUAL, propertyName, value);
    }
    
    /**
     * Apply a "less than" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression lt(String propertyName, Object value) {
        return new EqualityExpression(Operator.LESS_THAN, propertyName, value);
    }
    
    /**
     * Apply a "less than or equal" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression lte(String propertyName, Object value) {
        return new EqualityExpression(Operator.LESS_THAN_OR_EQUAL, propertyName, value);
    }
    
    /**
     * Apply a "greater than" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression gt(String propertyName, Object value) {
        return new EqualityExpression(Operator.GREATER_THAN, propertyName, value);
    }
    
    /**
     * Apply a "greater than or equal" constraint to the named property
     * @param propertyName
     * @param value
     * @return Criterion
     */
    public static EqualityExpression gte(String propertyName, Object value) {
        return new EqualityExpression(Operator.GREATER_THAN_OR_EQUAL, propertyName, value);
    }
    
    /**
     * Apply a "between" constraint to the named property
     * @param propertyName
     * @param from value
     * @param to value
     * @return Criterion
     */
    public static RangeExpression between(String propertyName, Object from, Object to) {
        return new RangeExpression(Operator.BETWEEN, propertyName, from, to);
    }
    
    /**
     * Apply a "between" constraint to the named integer property with a finite,
     * discrete number of values. This is translated into an inclusive "in" 
     * expression.
     * 
     * @param propertyName
     * @param from value
     * @param to value
     * @return Criterion
     */
    public static SetExpression discreteRange(String propertyName, int from, int to) {
        return new SetExpression(Operator.IN, propertyName, 
                from <= to ? range(from, to) : range(to, from));
    }
    
    /*
     * NOTE: this yields an Integer[], not an int[] to conform with the Object[]
     * signature of the Expressions. Give that, nulls are still unacceptable.
     */
    protected static Integer[] range(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from must be <= to (" + from
                    + "," + to + ")");
        }
        int n = to - from + 1;
        Integer[] range = new Integer[n];
        for (int i = 0; i < n; i++) {
            range[i] = from + i;
        }
        return range;
    }
    
    /**
     * Apply an "in" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression in(String propertyName, Object[] values) {
        return new SetExpression(Operator.IN, propertyName, values);
    }
    
    /**
     * Apply an "in" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression in(String propertyName, Collection<? extends Object> values) {
        return in(propertyName, values.toArray());
    }
    
    /**
     * Apply a "not in" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression notIn(String propertyName, Object[] values) {
        return new SetExpression(Operator.NOT_IN, propertyName, values);
    }
    
    /**
     * Apply a "not in" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression notIn(String propertyName, Collection<? extends Object> values) {
        return notIn(propertyName, values.toArray());
    }
    
    /**
     * Apply a "contains" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression contains(String propertyName, Object value) {
        return contains(propertyName, new Object[] { value });
    }
    
    /**
     * Apply a "contains" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
    public static SetExpression contains(String propertyName, Object[] values) {
        return new SetExpression(Operator.CONTAINS, propertyName, values);
    }
    
    /**
     * Apply an "is null" constraint to the named property
     * @return Criterion
     */
    public static UnaryExpression isNull(String propertyName) {
        return new UnaryExpression(Operator.NULL, propertyName);
    }
    
    
    /**
     * Apply an "is not null" constraint to the named property
     * @return Criterion
     */
    public static UnaryExpression isNotNull(String propertyName) {
        return new UnaryExpression(Operator.NOT_NULL, propertyName);
    }
    
    /**
     * Constrain a collection valued property to be empty
     */
    public static UnaryExpression isEmpty(String propertyName) {
        return new UnaryExpression(Operator.EMPTY, propertyName);
    }
    
    /**
     * Constrain a collection valued property to be non-empty
     */
    public static UnaryExpression isNotEmpty(String propertyName) {
        return new UnaryExpression(Operator.NOT_EMPTY, propertyName);
    }
    
    /**
     * Constrain a collection valued property to be non-empty
     */
    public static <T> NativeExpression nativeQuery(Class<T> type, T expression) {
        return new NativeExpression(type, expression);
    }
    
    /**
     * Return the conjuction of two expressions
     *
     * @param lhs
     * @param rhs
     * @return Criterion
     */
    public static Conjunction and(Criterion... criteria) {
        return new Conjunction(criteria);
    }
    
    /**
     * Return the disjuction of two expressions
     *
     * @param lhs
     * @param rhs
     * @return Criterion
     */
    public static Disjunction or(Criterion... criteria) {
        return new Disjunction(criteria);
    }
    
    /**
     * Apply a "within" constraint to the property with a center of x,y and
     * distance of d
     * 
     * @param propertyName
     * @param x
     * @param y
     * @param distance
     * @return
     */
    public static <N extends Number & Comparable<N>> Distance2dExpression<N> within(
            String propertyName, N x, N y, N distance) {
        return new Distance2dExpression<N>(Operator.WITHIN, propertyName, x, y,
                distance);
    }
}
