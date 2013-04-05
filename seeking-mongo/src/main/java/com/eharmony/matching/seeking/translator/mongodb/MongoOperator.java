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

package com.eharmony.matching.seeking.translator.mongodb;

import com.eharmony.matching.seeking.query.criterion.Symbolic;

/**
 * Mappings for MongoDB query operators 
 */
public enum MongoOperator implements Symbolic {
    NOT_EQUAL("$ne"),
    GREATER_THAN("$gt"), 
    GREATER_THAN_OR_EQUAL("$gte"), 
    LESS_THAN("$lt"), 
    LESS_THAN_OR_EQUAL("$lte"),
    
    IN("$in"),
    NOT_IN("$nin"),
    
    EXISTS("$exists"),
    
    WITHIN("$within"),
    CENTER("$center"),

    OR("$or"),
    AND("$and");

    private final String symbol;

    private MongoOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String symbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return symbol();
    }

}
