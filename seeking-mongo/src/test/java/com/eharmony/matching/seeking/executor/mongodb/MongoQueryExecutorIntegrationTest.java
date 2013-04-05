package com.eharmony.matching.seeking.executor.mongodb;

import static com.eharmony.matching.seeking.query.criterion.Restrictions.between;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.eq;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.in;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.common.collect.Lists;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.test.TestClassTuple;
import com.eharmony.matching.seeking.test.TestClassTuple2;
import com.eharmony.matching.seeking.translator.EntityResolver;
import com.eharmony.matching.seeking.translator.mongodb.MongoQueryTranslator;
import com.eharmony.matching.seeking.translator.mongodb.MorphiaPropertyResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MongoQueryExecutorIntegrationTest {
    
    private final Class<TestClass> entityClass = TestClass.class;
    
    private final String testName = "test" + System.currentTimeMillis();
    private final Mongo mongo;
    private final DB db;
    private final MongoQueryTranslator queryTranslator;
    private final MongoQueryExecutor executor;
    
    private final Date now = new Date();
    
    private final TestClass t0 = new TestClass(0L, "test0", 0, 1);
    private final TestClass t1 = new TestClass(1L, "test1", 2, 3);
    private final TestClass t2 = new TestClass(2L, "test2", 4, 5);
    private final TestClass t3 = new TestClass(3L, "test3", 6, 7);
    private final TestClass t4 = new TestClass(4L, "test4", 8, 9);
    private final TestClass t5 = new TestClass(5L, "test5", 0, 1);
    private final TestClass t6 = new TestClass(6L, "test6", 2, 3);
    private final TestClass t7 = new TestClass(7L, "test7", 4, 5);
    private final TestClass t8 = new TestClass(8L, "test8", 6, 7);
    private final TestClass t9 = new TestClass(9L, "test9", 8, 9);
    
    private final TestClass[] tests = {t0, t1, t2, t3, t4, t5, t6, t7, t8, t9 };
    
    public MongoQueryExecutorIntegrationTest() throws Exception {
        final EntityResolver resolver = new EntityResolver() {
            @Override
            public String resolve(Class<?> clazz) {
                return testName;
            }
        };
        mongo = new Mongo("localhost");
        db = mongo.getDB(testName);
        queryTranslator = new MongoQueryTranslator(new MorphiaPropertyResolver());
        executor = new MongoQueryExecutor(db, WriteConcern.SAFE, queryTranslator, resolver);
    }

    @After
    public void after() {
        db.dropDatabase();
    }
    
    @Before
    public void before() {
        executor.save(Arrays.asList(tests));
    }
    
    @Test
    public void find_byName() {
        final Iterable<TestClass> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(eq("name", "test0")).build());
        assertEquals(t0, found.iterator().next());
    }
    
    @Test
    public void find_byId() {
        final Iterable<TestClass> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(eq("id", 7L)).build());
        assertEquals(t7, found.iterator().next());
    }
    
    @Test
    public void find_byIds() {
        final Iterable<TestClass> found = executor.find(QueryBuilder
                .builderFor(entityClass)
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        final ArrayList<TestClass> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t3));
        assertTrue(list.contains(t5));
        assertTrue(list.contains(t7));
    }
    
    @Test
    public void find_byIdsAsLongs() {
        final Iterable<Long> found = executor.find(QueryBuilder
                .builderFor(entityClass, Long.class, "id")
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        final ArrayList<Long> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertEquals(t1.getId(), list.get(0));
        assertEquals(t3.getId(), list.get(1));
        assertEquals(t5.getId(), list.get(2));
        assertEquals(t7.getId(), list.get(3));
    }
    
    @Test
    public void find_byIdsAsTestClassTuples() {
        final Iterable<TestClassTuple> found = executor.find(QueryBuilder
                .builderFor(entityClass, TestClassTuple.class, "name", "date")
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        final ArrayList<TestClassTuple> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertEquals(t1.getName(), list.get(0).getName());
        assertEquals(t1.getDate(), list.get(0).getTheDate());
        assertEquals(t3.getName(), list.get(1).getName());
        assertEquals(t3.getDate(), list.get(1).getTheDate());
        assertEquals(t5.getName(), list.get(2).getName());
        assertEquals(t5.getDate(), list.get(2).getTheDate());
        assertEquals(t7.getName(), list.get(3).getName());
        assertEquals(t7.getDate(), list.get(3).getTheDate());
    }
    
    @Test
    public void find_byIdsAsTestClassTuple2s() {
        final Iterable<TestClassTuple2> found = executor.find(QueryBuilder
                .builderFor(entityClass, TestClassTuple2.class, "id", "name", "date")
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        final ArrayList<TestClassTuple2> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertEquals(t1.getId(), list.get(0).getIdentificationNumber());
        assertEquals(t1.getName(), list.get(0).getName());
        assertEquals(t3.getId(), list.get(1).getIdentificationNumber());
        assertEquals(t3.getName(), list.get(1).getName());
        assertEquals(t5.getId(), list.get(2).getIdentificationNumber());
        assertEquals(t5.getName(), list.get(2).getName());
        assertEquals(t7.getId(), list.get(3).getIdentificationNumber());
        assertEquals(t7.getName(), list.get(3).getName());
    }
    
    @Test
    public void find_byIdsAsName() {
        final Iterable<String> found = executor.find(QueryBuilder
                .builderFor(entityClass, String.class, "name")
                .add(in("id", new Long[] { 1L, 3L, 5L, 7L })).build());
        final ArrayList<String> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        assertEquals(t1.getName(), list.get(0));
        assertEquals(t3.getName(), list.get(1));
        assertEquals(t5.getName(), list.get(2));
        assertEquals(t7.getName(), list.get(3));
    }
    
    @Test
    public void find_byDateRange() {
        final Date later = new Date();
        final Iterable<TestClass> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(between("date", now, later))
                .build());
        final ArrayList<TestClass> list = Lists.newArrayList(found);
        for (final TestClass t : tests) {
            assertTrue(list.contains(t));
        }
    }
    
    @Test
    public void find_byEmbeddedObjectProperty() {
        final Iterable<TestClass> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(eq("embeddedObject.first", 0L)).build());
        final ArrayList<TestClass> list = Lists.newArrayList(found);
        assertEquals(2, list.size());
        assertTrue(list.contains(t0));
        assertTrue(list.contains(t5));
    }
    
    @Test
    public void find_byDistance() {
        db.getCollection(testName).ensureIndex(new BasicDBObject("embeddedObject", "2d"));
        final Iterable<TestClass> found = executor.find(QueryBuilder.builderFor(entityClass).add(
                within("embeddedObject", 5, 5, 2)).build());
        final ArrayList<TestClass> list = Lists.newArrayList(found);
        assertEquals(2, list.size());
        assertTrue(list.contains(t2));
        assertTrue(list.contains(t7));
    }
    
}
