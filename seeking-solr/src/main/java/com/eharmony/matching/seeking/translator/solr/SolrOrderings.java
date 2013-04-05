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

package com.eharmony.matching.seeking.translator.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A list of wrapped Solr ORDERs. Encapsulated for type safety and as a guard
 * against type erasure.
 */
public class SolrOrderings  {
    
    private final List<SolrOrdering> orderings = new ArrayList<SolrOrdering>();

    public SolrOrderings() {
    }
    
    public SolrOrderings(SolrOrdering... orderings) {
        this.orderings.addAll(Arrays.asList(orderings));
    }
    
    public static SolrOrderings merge(SolrOrderings... orderings) {
        SolrOrderings merged = new SolrOrderings();
        for (SolrOrderings o : orderings) {
            merged.addAll(o.get());
        }
        return merged;
    }

    public void add(SolrOrdering ordering) {
        orderings.add(ordering);
    }
    
    public void addAll(Collection<SolrOrdering> orderings) {
        this.orderings.addAll(orderings);
    }

    public List<SolrOrdering> get() {
        return orderings;
    }

    @Override
    public String toString() {
        return "SolrOrderings [" + orderings + "]";
    }

}
