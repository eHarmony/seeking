package com.eharmony.matching.seeking.executor.hibernate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.eharmony.matching.seeking.executor.QueryExecutor;

public class IterativeHibernateQueryExecutorHsqldbIntegrationTest extends
        BaseHibernateQueryExecutorHsqldbIntegrationTest {

    @Autowired
    @Qualifier("iterativeHibernateQueryExecutor")
    private QueryExecutor executor;

    @Override
    protected QueryExecutor executor() {
        return executor;
    }

}
