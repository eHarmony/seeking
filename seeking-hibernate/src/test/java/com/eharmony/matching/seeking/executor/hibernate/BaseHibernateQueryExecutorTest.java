package com.eharmony.matching.seeking.executor.hibernate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.translator.hibernate.HibernateQueryTranslator;
import com.eharmony.matching.seeking.translator.hibernate.Orders;

public abstract class BaseHibernateQueryExecutorTest {
    
    protected final Class<TestClass> entityClass = TestClass.class;
    protected final SessionFactory sessionFactory = mock(SessionFactory.class);
    protected final HibernateQueryTranslator queryTranslator = mock(HibernateQueryTranslator.class);
    
    protected final Session session = mock(Session.class);
    
    protected final Query<TestClass, TestClass> query = QueryBuilder
            .builderFor(TestClass.class).add(Restrictions.eq("name", "test"))
            .build();
    protected final Criterion queryObject = org.hibernate.criterion.Restrictions
            .eq("name", "test");
    protected final Query<TestClass, Long> idQuery = QueryBuilder
            .builderFor(TestClass.class, Long.class, "id")
            .add(Restrictions.eq("name", "test")).build();
    protected final Criterion idQueryObject = org.hibernate.criterion.Restrictions
            .eq("name", "test");
    protected final Criteria criteria = mock(Criteria.class);
    
    protected final TestClass t1 = new TestClass(1L, "test1", 0, 0);
    protected final TestClass[] tests = {
            new TestClass(1L, "test1", 0, 0),
            new TestClass(2L, "test2", 0, 0),
            new TestClass(3L, "test3", 0, 0),
            new TestClass(4L, "test4", 0, 0),
            new TestClass(5L, "test5", 0, 0),
            new TestClass(6L, "test6", 0, 0),
    };
    
    protected abstract QueryExecutor executor();
    
    @Before
    public void before() {
        when(criteria.setMaxResults(anyInt())).thenReturn(criteria);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(queryTranslator.translate(query)).thenReturn(queryObject);
        when(queryTranslator.translateOrder(query)).thenReturn(new Orders());
        when(queryTranslator.translate(idQuery)).thenReturn(idQueryObject);
        when(queryTranslator.translateOrder(idQuery)).thenReturn(new Orders());
        when(session.createCriteria(entityClass)).thenReturn(criteria);
        when(session.save(any(TestClass.class))).thenAnswer(new Answer<TestClass>() {
            @Override
            public TestClass answer(InvocationOnMock invocation)
                    throws Throwable {
                return (TestClass) invocation.getArguments()[0];
            }
        });
    }
    
    public void find_one() {
        Iterable<TestClass> found = executor().find(query);
        assertEquals(t1, found.iterator().next());
        verify(queryTranslator).translate(query);
    }
    
    public void find_many() {
        Iterable<TestClass> found = executor().find(query);
        Iterator<TestClass> iterator = found.iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], iterator.next());
        }
        verify(queryTranslator).translate(query);
    }
    
    public void find_manyIds() {
        Iterable<TestClass> found = executor().find(query);
        Iterator<TestClass> iterator = found.iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i].getId(), iterator.next());
        }
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void findOne() {
        when(criteria.uniqueResult()).thenReturn(t1);
        
        TestClass found = executor().findOne(query);
        assertEquals(t1, found);
        
        verify(queryTranslator).translate(query);
        verify(criteria).uniqueResult();
    }
    
    @Test
    public void findOneId() {
        when(criteria.uniqueResult()).thenReturn(t1.getId());
        
        Long found = executor().findOne(idQuery);
        assertEquals(t1.getId(), found);
        
        verify(queryTranslator).translate(idQuery);
        verify(criteria).uniqueResult();
    }
    
    @Test
    public void save() {
        TestClass saved = executor().save(t1);
        assertEquals(t1, saved);
    }

    @Test
    public void save_many() {
        Iterable<TestClass> saved = executor().save(Arrays.asList(tests));
        Iterator<TestClass> iterator = saved.iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], iterator.next());
        }
    }

}
