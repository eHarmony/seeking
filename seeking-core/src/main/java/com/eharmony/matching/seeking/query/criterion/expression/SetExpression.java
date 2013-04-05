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

import java.util.Arrays;

import com.eharmony.matching.seeking.query.criterion.Operator;
import com.google.common.base.Joiner;

/**
 * A set expression (in, contains, etc.).
 */
public class SetExpression extends Expression {

    private final Object[] values;

    public SetExpression(Operator operator, String propertyName,
            final Object[] values) {
        super(operator, propertyName);
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return getPropertyName() + " " + getOperator() + " ["
                + Joiner.on(',').join(values) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(values);
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
        SetExpression other = (SetExpression) obj;
        if (!Arrays.equals(values, other.values))
            return false;
        return true;
    }
}
