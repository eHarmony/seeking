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

import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.WithOperator;
import com.eharmony.matching.seeking.query.criterion.WithProperty;

/**
 * An abstract expression with an operator that acts on a property name.
 */
public abstract class Expression implements Criterion, WithOperator, WithProperty {

    private final Operator operator;
    private final String propertyName;

    protected Expression(Operator operator, String propertyName) {
        this.operator = operator;
        this.propertyName = propertyName;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
                + ((propertyName == null) ? 0 : propertyName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Expression other = (Expression) obj;
        if (operator != other.operator)
            return false;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        return true;
    }

}
