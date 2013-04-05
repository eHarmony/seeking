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

import com.google.common.base.Optional;

/**
 * A data store exception that describes a situation in which an operation could
 * be completely or partially retried.
 */
public final class RetryableDataStoreException extends DataStoreException {

    private static final long serialVersionUID = 5095341832078759251L;

    private final Optional<?> retryableStatus;

    /**
     * @param retryableStatus
     *            an object that provides clients with any additional
     *            information they may need in order to retry the operation, or
     *            null if there is no such information.
     */
    public RetryableDataStoreException(Object retryableStatus) {
        super();
        this.retryableStatus = fromNullable(retryableStatus);
    }

    /**
     * @param retryableStatus
     *            an object that provides clients with any additional
     *            information they may need in order to retry the operation, or
     *            null if there is no such information.
     */
    public RetryableDataStoreException(String message, Object retryableStatus) {
        super(message);
        this.retryableStatus = fromNullable(retryableStatus);
    }

    /**
     * @param retryableStatus
     *            an object that provides clients with any additional
     *            information they may need in order to retry the operation, or
     *            null if there is no such information.
     */
    public RetryableDataStoreException(Throwable cause, Object retryableStatus) {
        super(cause);
        this.retryableStatus = fromNullable(retryableStatus);
    }

    /**
     * @param retryableStatus
     *            an object that provides clients with any additional
     *            information they may need in order to retry the operation, or
     *            null if there is no such information.
     */
    public RetryableDataStoreException(String message, Throwable cause,
            Object retryableStatus) {
        super(message, cause);
        this.retryableStatus = fromNullable(retryableStatus);
    }

    /**
     * Provide any additional information that may be necessary for clients to
     * retry the failed operation at the right granularity. Operations that
     * throw this exception type should document the type and contents of this
     * property.
     */
    public Optional<?> getRetryableStatus() {
        return this.retryableStatus;
    }

    private static Optional<?> fromNullable(Object nullable) {
        return (nullable instanceof Optional) ? (Optional<?>) nullable
                : Optional.fromNullable(nullable);
    }

}
