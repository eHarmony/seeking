package com.eharmony.matching.seeking.executor.hibernate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;

import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.mapper.ProjectedResultMapper;
import com.eharmony.matching.seeking.test.TestClass;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class HibernateQueryExecutorTest extends BaseHibernateQueryExecutorTest {
    
    private final HibernateQueryExecutor executor = new HibernateQueryExecutor(
            sessionFactory, queryTranslator, new ProjectedResultMapper());
    
    @Override
    protected QueryExecutor executor() {
        return executor;
    }
    
    @Test
    public void find_one() {
        when(criteria.list()).thenReturn(Arrays.asList(t1));
        super.find_one();
        verify(criteria).list();
    }
    
    @Test
    public void find_many() {
        when(criteria.list()).thenReturn(Arrays.asList(tests));
        super.find_many();
        verify(criteria).list();
    }
    
    @Test
    public void find_manyIds() {
        when(criteria.list()).thenReturn(Lists.transform(Arrays.asList(tests), new Function<TestClass, Long>() {
            @Override
            public Long apply(TestClass arg0) {
                return arg0.getId();
            }
        }));
        
        super.find_manyIds();
        
        verify(criteria).list();
    }

}
