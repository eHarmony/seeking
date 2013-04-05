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

/**
 * A typed, native datastore query component.
 */
public class NativeExpression implements Criterion {

    private final Class<?> expressionClass;
    private final Object expression;

    public <T> NativeExpression(Class<T> expressionClass, T expression) {
        this.expressionClass = expressionClass;
        this.expression = expression;
    }

    public Class<?> getExpressionClass() {
        return expressionClass;
    }

    public Object getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "NativeExpression [" + expression + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result
                + ((expressionClass == null) ? 0 : expressionClass.hashCode());
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
        NativeExpression other = (NativeExpression) obj;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        if (expressionClass == null) {
            if (other.expressionClass != null)
                return false;
        } else if (!expressionClass.equals(other.expressionClass))
            return false;
        return true;
    }
}
