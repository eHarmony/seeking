package com.eharmony.matching.seeking.executor.hibernate;

import static com.eharmony.matching.seeking.query.criterion.Restrictions.between;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.eq;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.in;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.test.BaseHSQLTest;
import com.eharmony.matching.seeking.test.TestClass;
import com.google.common.collect.Lists;

@ContextConfiguration(locations={"/hsqldb-test.cfg.xml"})
public abstract class BaseHibernateQueryExecutorHsqldbIntegrationTest extends BaseHSQLTest {

    private final Class<TestClass> entityClass = TestClass.class;
    
    private final Date now = new Date();
    
    private final TestClass t0 = new TestClass(0L, "test0", 0, 0);
    private final TestClass t1 = new TestClass(1L, "test1", 0, 1);
    private final TestClass t2 = new TestClass(2L, "test2", 1, 0);
    private final TestClass t3 = new TestClass(3L, "test3", 1, 1);
    private final TestClass t4 = new TestClass(4L, "test4", 0,-1);
    private final TestClass t5 = new TestClass(5L, "test5",-1, 0);
    private final TestClass t6 = new TestClass(6L, "test6",-1,-1);
    private final TestClass t7 = new TestClass(7L, "test7", 1, 2);
    private final TestClass t8 = new TestClass(8L, "test8", 2, 1);
    private final TestClass t9 = new TestClass(9L, "test9", 2, 2);
    
    private final TestClass[] tests = {t0, t1, t2, t3, t4, t5, t6, t7, t8, t9 };

    protected abstract QueryExecutor executor();
    
    @Before
    public void before() {
        executor().save(Arrays.asList(tests));
    }
    
    //@Test
    public void save_generateId() {
        TestClass newObject = new TestClass(null, "new test", 0, 0);
        executor().save(newObject);
        assertNotNull(newObject.getId());
    }
    
    @Test
    public void find_byName() {
        Iterable<TestClass> found = executor().find(QueryBuilder
                .builderFor(entityClass).add(eq("name", "test0")).build());
        assertEquals(t0, found.iterator().next());
    }
    
    @Test
    public void find_byId() {
        Iterable<TestClass> found = executor().find(QueryBuilder
                .builderFor(entityClass).add(eq("id", 7L)).build());
        assertEquals(t7, found.iterator().next());
    }
    
    @Test
    public void find_byIds() {
        Iterable<TestClass> found = executor().find(QueryBuilder
                .builderFor(entityClass)
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        ArrayList<TestClass> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t3));
        assertTrue(list.contains(t5));
        assertTrue(list.contains(t7));
    }
    
    @Test
    public void find_byDateRange() {
        Date later = new Date();
        Iterable<TestClass> found = executor().find(QueryBuilder
                .builderFor(entityClass).add(between("date", now, later))
                .build());
        ArrayList<TestClass> list = Lists.newArrayList(found);
        for (TestClass t : tests) {
            assertTrue(list.contains(t));
        }
    }
    
    @Test
    public void find_byLatLon() {
        Iterable<TestClass> found = executor().find(QueryBuilder
                .builderFor(entityClass).add(within("latLon", 0D, 0D, 1D))
                .build());
        ArrayList<TestClass> list = Lists.newArrayList(found);
        assertEquals(7, list.size());
        assertTrue(list.contains(t0));
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t2));
        assertTrue(list.contains(t3));
        assertTrue(list.contains(t4));
        assertTrue(list.contains(t5));
        assertTrue(list.contains(t6));
    }

}
