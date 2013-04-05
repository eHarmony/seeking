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

package com.eharmony.matching.seeking.query.criterion.junction;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;
import com.eharmony.matching.seeking.query.criterion.WithOperator;
import com.google.common.base.Joiner;

/**
 * and / or
 */
public abstract class Junction implements Criterion, WithOperator {

    private final List<Criterion> criteria = new ArrayList<Criterion>();
    private final Operator operator;
    
    protected Junction(Operator operator) {
        this.operator = operator;
    }
    
    public Junction add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }
    
    public Junction addAll(Criterion... criterions) {
        for (Criterion criterion : criterions) {
            if (criterion != null) {
                criteria.add(criterion);
            }
        }
        return this;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }
    
    public List<Criterion> getCriteria() {
        // TODO : immutable list?
        return new ArrayList<Criterion>(criteria);
    }

    @Override
    public String toString() {
        return "(" + Joiner.on(") " + operator.symbol() + " (").join(criteria) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((criteria == null) ? 0 : criteria.hashCode());
        result = prime * result
                + ((operator == null) ? 0 : operator.hashCode());
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
        Junction other = (Junction) obj;
        if (criteria == null) {
            if (other.criteria != null)
                return false;
        } else if (!criteria.equals(other.criteria))
            return false;
        if (operator != other.operator)
            return false;
        return true;
    }
    
}
