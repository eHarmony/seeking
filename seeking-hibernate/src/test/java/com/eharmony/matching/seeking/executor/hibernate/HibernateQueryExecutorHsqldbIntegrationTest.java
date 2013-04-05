package com.eharmony.matching.seeking.executor.hibernate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.eharmony.matching.seeking.executor.QueryExecutor;

public class HibernateQueryExecutorHsqldbIntegrationTest extends
        BaseHibernateQueryExecutorHsqldbIntegrationTest {

    @Autowired
    @Qualifier("hibernateQueryExecutor")
    private QueryExecutor executor;

    @Override
    protected QueryExecutor executor() {
        return executor;
    }

}
