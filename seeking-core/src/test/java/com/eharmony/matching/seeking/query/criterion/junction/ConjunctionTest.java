package com.eharmony.matching.seeking.query.criterion.junction;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eharmony.matching.seeking.query.criterion.Criterion;
import com.eharmony.matching.seeking.query.criterion.Operator;

public class ConjunctionTest {
    
    private final Criterion left = new Criterion() {
        @Override
        public String toString() {
            return "left";
        }
    };
    private final Criterion right = new Criterion() {
        @Override
        public String toString() {
            return "right";
        }
    };
    private final Conjunction and = new Conjunction(left, right);
    
    @Test
    public void getOperator() {
        assertEquals(Operator.AND, and.getOperator());
    }
    
    @Test
    public void getCriteria() {
        List<Criterion> criteria = and.getCriteria();
        assertEquals(left, criteria.get(0));
        assertEquals(right, criteria.get(1));
    }
    
    @Test
    public void add() {
        final Criterion newCriterion = new Criterion() {
            @Override
            public String toString() {
                return "new";
            }
        };
        and.add(newCriterion);
        List<Criterion> criteria = and.getCriteria();
        assertEquals(newCriterion, criteria.get(criteria.size() - 1));
    }

}
