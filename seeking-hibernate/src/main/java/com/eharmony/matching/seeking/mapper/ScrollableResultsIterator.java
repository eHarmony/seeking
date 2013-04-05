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

package com.eharmony.matching.seeking.mapper;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import org.hibernate.ScrollableResults;

/**
 * An iterator for Hibernate's scrollable results
 */
@NotThreadSafe
public class ScrollableResultsIterator implements Iterator<Object> {
    
    private final ScrollableResults scrollable;
    private Object current = null;
    
    public ScrollableResultsIterator(ScrollableResults scrollableResults) {
        this.scrollable = scrollableResults;
    }

    private boolean updateCurrent() {
        if (!scrollable.next()) {
            current = null;
            scrollable.close();
            return false;
        } else {
            Object[] row = scrollable.get();
            current = row.length > 1 ? row : row[0];
            return true;
        }
    }
    
    @Override
    public boolean hasNext() {
        return current != null || updateCurrent();
    }

    @Override
    public Object next() {
        if (current == null) {
            updateCurrent();
        }
        final Object toReturn = current;
        current = null;
        return toReturn;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
