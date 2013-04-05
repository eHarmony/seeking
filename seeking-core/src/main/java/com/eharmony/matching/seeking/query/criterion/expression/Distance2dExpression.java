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

package com.eharmony.matching.seeking.query.criterion.expression;

import com.eharmony.matching.seeking.query.criterion.Operator;

/**
 * Representation of a two dimensional distance expression where (x,y) is point
 * from which the distance is being measured.
 * 
 * This can be transformed into a datastore specific distance search or a
 * general box query if no native distance query is supported
 * 
 * @param <N>
 *            the numeric type
 */
public class Distance2dExpression<N extends Number & Comparable<N>> extends
        Expression {

    private final N x;
    private final N y;
    private final N distance;

    public Distance2dExpression(Operator operator, String propertyName, N x,
            N y, N distance) {
        super(operator, propertyName);
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    public N getX() {
        return x;
    }

    public N getY() {
        return y;
    }

    public N getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Distance2dExpression [x=" + x + ", y=" + y + ", distance="
                + distance + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((distance == null) ? 0 : distance.hashCode());
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        Distance2dExpression other = getClass().cast(obj);
        if (distance == null) {
            if (other.distance != null)
                return false;
        } else if (!distance.equals(other.distance))
            return false;
        if (x == null) {
            if (other.x != null)
                return false;
        } else if (!x.equals(other.x))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        return true;
    }

}
