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

package com.eharmony.matching.seeking.executor;

/**
 * An exception that indicates a problem loading from or storing to a data
 * store. It presents a uniform abstraction across things like
 * {@link java.sql.SQLException}, {@link org.hibernate.HibernateException}, and
 * {@link com.mongodb.MongoException}.
 */
public class DataStoreException extends RuntimeException {

    private static final long serialVersionUID = -1363433871972493184L;

    public DataStoreException() {
        super();
    }

    public DataStoreException(String message) {
        super(message);
    }

    public DataStoreException(Throwable cause) {
        super(cause);
    }

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
