package com.eharmony.matching.seeking.executor.solr;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrException;
import org.junit.Before;
import org.junit.Test;

import com.eharmony.matching.seeking.executor.solr.mock.MockQueryResponse;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.test.TestClassTuple;
import com.eharmony.matching.seeking.test.TestClassTuple2;
import com.eharmony.matching.seeking.translator.solr.SolrOrderings;
import com.eharmony.matching.seeking.translator.solr.SolrQueryTranslator;

public class SolrQueryExecutorTest {
    
    private final Class<TestClass> entityClass = TestClass.class;
    
    private final SolrServer solrServer = mock(SolrServer.class);
    private final SolrQueryTranslator queryTranslator = mock(SolrQueryTranslator.class);
    private final SolrQueryExecutor executor = new SolrQueryExecutor(solrServer, queryTranslator);
    
    private final Query<TestClass, TestClass> query = QueryBuilder
            .builderFor(entityClass).add(Restrictions.eq("name", "test"))
            .build();
    private final String queryString = query.getCriteria().toString();
    
    private final Query<TestClass, Long> idQuery = QueryBuilder
            .builderFor(entityClass, Long.class, "id").build();
    
    private final Query<TestClass, TestClassTuple> tupleQuery = QueryBuilder
            .builderFor(entityClass, TestClassTuple.class, "id").build();
    
    private final Query<TestClass, TestClassTuple2> tuple2Query = QueryBuilder
            .builderFor(entityClass, TestClassTuple2.class, "id").build();
    
    private final TestClass t1 = new TestClass(1L, "test1", 1, 2);
    private final TestClass[] tests = {
            new TestClass(1L, "test1", 1, 2),
            new TestClass(2L, "test2", 3, 4),
            new TestClass(3L, "test3", 5, 6),
            new TestClass(4L, "test4", 7, 8),
            new TestClass(5L, "test5", 9, 0),
            new TestClass(6L, "test6", 1, 2),
    };
    private final Long[] ids = new Long[tests.length];
    
    public SolrQueryExecutorTest() {
        for (int i = 0; i < tests.length; i++) {
            ids[i] = tests[i].getId();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        when(queryTranslator.translate(any(Query.class))).thenReturn(queryString);
        when(queryTranslator.translateOrder(any(Query.class))).thenReturn(new SolrOrderings());
    }
    
    private void setResponse(Object...objects) {
        try {
            when(solrServer.query(any(SolrQuery.class))).thenReturn(new MockQueryResponse(objects));
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void find_one() {
        setResponse(t1);
        
        Iterable<TestClass> found = executor.find(query);
        assertEquals(t1, found.iterator().next());
        
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void find_many() {
        setResponse((Object[]) tests);
        
        Iterator<TestClass> found = executor.find(query).iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], found.next());
        }
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void find_many_asIds() {
        setResponse((Object[]) ids);
        
        Iterator<Long> found = executor.find(idQuery).iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i].getId(), found.next());
        }
        verify(queryTranslator).translate(idQuery);
    }
    
    @Test
    public void find_many_asTuple() {
        setResponse((Object[]) tests);
        
        Iterator<TestClassTuple> found = executor.find(tupleQuery).iterator();
        for (int i = 0; i < tests.length; i++) {
            TestClassTuple t = found.next();
            assertEquals(tests[i].getName(), t.getName());
            assertEquals(tests[i].getDate(), t.getTheDate());
        }
        verify(queryTranslator).translate(tupleQuery);
    }
    
    @Test
    public void find_many_asTuple2() {
        setResponse((Object[]) tests);
        
        Iterator<TestClassTuple2> found = executor.find(tuple2Query).iterator();
        for (int i = 0; i < tests.length; i++) {
            TestClassTuple2 t = found.next();
            assertEquals(tests[i].getId(), t.getIdentificationNumber());
            assertEquals(tests[i].getName(), t.getName());
        }
        verify(queryTranslator).translate(tuple2Query);
    }
    
    @Test
    public void findOne() {
        setResponse(t1);
        
        TestClass found = executor.findOne(query);
        assertEquals(t1, found);
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void save() {
        TestClass saved = executor.save(t1);
        assertEquals(t1, saved);
    }
    
    @Test
    public void save_many() {
        Iterable<TestClass> saved = executor.save(Arrays.asList(tests));
        Iterator<TestClass> iterator = saved.iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], iterator.next());
        }
    }
    
    @Test(expected = RuntimeException.class)
    public void save_fail() throws IOException, SolrServerException {
        when(solrServer.addBean(t1)).thenThrow(new SolrException(null, queryString));
        executor.save(t1);
    }
    
}
