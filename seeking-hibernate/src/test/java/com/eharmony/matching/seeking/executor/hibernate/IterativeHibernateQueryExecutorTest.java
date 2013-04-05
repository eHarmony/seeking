package com.eharmony.matching.seeking.executor.hibernate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.ScrollMode;
import org.junit.Test;

import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.mapper.ProjectedResultMapper;
import com.eharmony.matching.seeking.test.MockScrollableResults;

public class IterativeHibernateQueryExecutorTest extends
        BaseHibernateQueryExecutorTest {

    private final IterativeHibernateQueryExecutor executor = new IterativeHibernateQueryExecutor(
            sessionFactory, queryTranslator, new ProjectedResultMapper());

    @Override
    protected QueryExecutor executor() {
        return executor;
    }

    @Test
    public void find_one() {
        when(criteria.scroll(ScrollMode.FORWARD_ONLY)).thenReturn(new MockScrollableResults(t1));
        super.find_one();
        verify(criteria).scroll(ScrollMode.FORWARD_ONLY);
    }

    @Test
    public void find_many() {
        when(criteria.scroll(ScrollMode.FORWARD_ONLY)).thenReturn(new MockScrollableResults((Object[])tests));
        super.find_many();
        verify(criteria).scroll(ScrollMode.FORWARD_ONLY);
    }
    
    @Test
    public void find_manyIds() {
        when(criteria.scroll(ScrollMode.FORWARD_ONLY)).thenReturn(new MockScrollableResults(1L,2L,3L,4L,5L,6L));
        super.find_manyIds();
        verify(criteria).scroll(ScrollMode.FORWARD_ONLY);
    }

}
