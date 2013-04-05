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
 * An equality expression comparing the property against the value with the
 * operator.
 */
public class EqualityExpression extends Expression {

    private final Object value;

    public EqualityExpression(Operator operator, String propertyName, Object value) {
        super(operator, propertyName);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getPropertyName() + " " + getOperator() + " " + value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        EqualityExpression other = (EqualityExpression) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    
}
