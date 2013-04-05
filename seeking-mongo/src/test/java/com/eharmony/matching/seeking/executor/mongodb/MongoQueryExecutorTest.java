package com.eharmony.matching.seeking.executor.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.mapping.Mapper;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBDecoderFactory;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import com.eharmony.matching.seeking.executor.mongodb.mock.MockDBCursor;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.test.TestClass;
import com.eharmony.matching.seeking.test.TestClass.TestEmbeddedClass;
import com.eharmony.matching.seeking.test.TestClassTuple;
import com.eharmony.matching.seeking.test.TestClassTuple2;
import com.eharmony.matching.seeking.translator.EntityResolver;
import com.eharmony.matching.seeking.translator.mongodb.MongoQueryTranslator;

import org.junit.Before;
import org.junit.Test;

public class MongoQueryExecutorTest {

    private static final String ID_PROPERTY_NAME = "id";
    private static final String NAME_PROPERTY_NAME = "name";

    public static class TestMongoQueryExecutor extends MongoQueryExecutor {
        public TestMongoQueryExecutor(DB db,
                MongoQueryTranslator queryTranslator, EntityResolver resolver) {
            super(db, WriteConcern.SAFE, queryTranslator, resolver);
        }
        public TestMongoQueryExecutor(DB db,
                MongoQueryTranslator queryTranslator, EntityResolver resolver,
                int batchSize) {
            super(db, WriteConcern.SAFE, queryTranslator, resolver, batchSize);
        }
        public DBCursor cursor;
        @Override
        protected DBCursor find(DBCollection collection, DBObject query, DBObject fields) {
            // collection.find is final and therefore cannot be mocked
            return cursor;
        }
        @Override
        protected WriteResult save(DBCollection collection, DBObject entity) {
            // collection.save is final and therefore cannot be mocked
            return writeResult;
        }
    }
    
    private final Class<TestClass> entityClass = TestClass.class;
    
    private final DB db = mock(DB.class);
    private final DBCollection collection = mock(DBCollection.class);
    private final DBDecoderFactory dbDecoderFactory = mock(DBDecoderFactory.class);
    private final DatastoreImpl datastore = mock(DatastoreImpl.class);
    private final MongoQueryTranslator queryTranslator = mock(MongoQueryTranslator.class);
    private final EntityResolver entityResolver = mock(EntityResolver.class);
    private final TestMongoQueryExecutor executor = new TestMongoQueryExecutor(
            db, queryTranslator, entityResolver);
    
    private final static WriteResult writeResult = mock(WriteResult.class);
    private final static CommandResult lastError = mock(CommandResult.class);
    
    private final Query<TestClass, TestClass> query = QueryBuilder
            .builderFor(this.entityClass)
            .add(Restrictions.eq(NAME_PROPERTY_NAME, "test"))
            .build();
    private final DBObject queryObject = new BasicDBObject();

    private final Query<TestClass, Long> idQuery = QueryBuilder
            .builderFor(entityClass, Long.class, ID_PROPERTY_NAME)
            .build();
    private final DBObject idQueryObject = new BasicDBObject();

    private final Query<TestClass, TestClassTuple> tupleQuery = QueryBuilder
            .builderFor(entityClass, TestClassTuple.class, ID_PROPERTY_NAME)
            .build();
    private final DBObject tupleQueryObject = new BasicDBObject();

    private final Query<TestClass, TestClassTuple2> tuple2Query = QueryBuilder
            .builderFor(entityClass, TestClassTuple2.class, ID_PROPERTY_NAME)
            .build();
    private final DBObject tuple2QueryObject = new BasicDBObject();
    
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
    
    public MongoQueryExecutorTest() {
        for (int i = 0; i < tests.length; i++) {
            ids[i] = tests[i].getId();
        }
    }
    
    protected DBObject toDBObject(TestClass test) {
        final DBObject o = new BasicDBObject();
        o.put("_id", test.getId());
        o.put(NAME_PROPERTY_NAME, test.getName());
        o.put("date", test.getDate());
        o.put("embeddedObject", toDBObject(test.getEmbeddedObject()));
        return o;
    }

    protected DBObject toDBObject(TestEmbeddedClass testEmbedded) {
        final DBObject o = new BasicDBObject();
        o.put("first", testEmbedded.getFirst());
        o.put("second", testEmbedded.getSecond());
        return o;
    }

    protected DBObject[] toDBObjectArray(TestClass... testClasses) {
        final DBObject[] objects = new DBObject[testClasses.length];
        for (int i = 0; i < testClasses.length; i++) {
            objects[i] = toDBObject(testClasses[i]);
        }
        return objects;
    }

    protected DBObject[] toDBObjectArray(Long... testIds) {
        final DBObject[] objects = new DBObject[testIds.length];
        for (int i = 0; i < testIds.length; i++) {
            objects[i] = new BasicDBObject("_id", testIds[i]);
        }
        return objects;
    }

    @Before
    public void before() {
        final Mapper mapper = new Mapper();
        when(datastore.getMapper()).thenReturn(mapper);
        when(collection.getDBDecoderFactory()).thenReturn(dbDecoderFactory);
        when(queryTranslator.translate(query)).thenReturn(queryObject);
        when(queryTranslator.translate(idQuery)).thenReturn(idQueryObject);
        when(queryTranslator.translate(tupleQuery)).thenReturn(tupleQueryObject);
        when(queryTranslator.translate(tuple2Query)).thenReturn(tuple2QueryObject);
        when(writeResult.getLastError()).thenReturn(lastError);
        when(db.getCollection(anyString())).thenReturn(collection);
    }
    
    @Test
    public void find_one() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObject(t1));
        executor.cursor = cursor;

        final Iterable<TestClass> found = executor.find(query);
        final Iterator<TestClass> foundIterator = found.iterator();
        assertTrue(foundIterator.hasNext());
        assertEquals(this.t1, foundIterator.next());
        assertFalse(foundIterator.hasNext());

        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void find_many() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObjectArray(tests));
        executor.cursor = cursor;

        final Iterable<TestClass> found = executor.find(query);
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], found.iterator().next());
        }
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void find_many_withBatchSize() {
        final TestMongoQueryExecutor executor = new TestMongoQueryExecutor(db,
                queryTranslator, entityResolver, 1000);
        final DBCursor cursor = new MockDBCursor(collection, toDBObjectArray(tests));
        executor.cursor = cursor;

        final Iterable<TestClass> found = executor.find(query);
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], found.iterator().next());
        }
        verify(queryTranslator).translate(query);
    }
    
    @Test
    public void find_many_asIds() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObjectArray(ids));
        executor.cursor = cursor;

        final Iterable<Long> found = executor.find(idQuery);
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i].getId(), found.iterator().next());
        }
        verify(queryTranslator).translate(idQuery);
    }
    
    @Test
    public void find_many_asTuple() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObjectArray(tests));
        executor.cursor = cursor;

        final Iterable<TestClassTuple> found = executor.find(tupleQuery);
        for (int i = 0; i < tests.length; i++) {
            final TestClassTuple t = found.iterator().next();
            assertEquals(tests[i].getName(), t.getName());
            assertEquals(tests[i].getDate(), t.getTheDate());
        }
        verify(queryTranslator).translate(tupleQuery);
    }
    
    @Test
    public void find_many_asTuple2() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObjectArray(tests));
        executor.cursor = cursor;

        final Iterable<TestClassTuple2> found = executor.find(tuple2Query);
        for (int i = 0; i < tests.length; i++) {
            final TestClassTuple2 t = found.iterator().next();
            assertEquals(tests[i].getId(), t.getIdentificationNumber());
            assertEquals(tests[i].getName(), t.getName());
        }
        verify(queryTranslator).translate(tuple2Query);
    }
    
    @Test
    public void findOne() {
        final DBCursor cursor = new MockDBCursor(collection, toDBObject(t1));
        executor.cursor = cursor;

        final TestClass found = executor.findOne(query);
        assertEquals(t1, found);
        verify(queryTranslator).translate(query);
    }


    @Test
    public void remove() {
        final DBCursor cursor = new MockDBCursor(this.collection, toDBObject(this.t1));
        this.executor.cursor = cursor;

        TestClass found = this.executor.findOne(this.query);
        assertEquals(this.t1, found);

        this.executor.remove(
                QueryBuilder.builderFor(this.entityClass).
                add(Restrictions.eq(ID_PROPERTY_NAME, 1)).
                build());

        found = this.executor.findOne(this.query);
        assertNull(found);
    }


    @Test
    public void removeAll() {
        final DBCursor cursor = new MockDBCursor(this.collection, toDBObjectArray(this.tests));
        this.executor.cursor = cursor;

        Iterable<TestClass> found = this.executor.find(this.query);
        for (int i = 0; i < this.tests.length; i++) {
            assertEquals(this.tests[i], found.iterator().next());
        }

        this.executor.removeAll(TestClass.class);
        found = this.executor.find(this.query);
        assertFalse(found.iterator().hasNext());
    }


    @Test
    public void save() {
        final TestClass saved = executor.save(t1);
        assertEquals(t1, saved);
    }
    
    @Test
    public void save_many() {
        final Iterable<TestClass> saved = executor.save(Arrays.asList(tests));
        final Iterator<TestClass> iterator = saved.iterator();
        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], iterator.next());
        }
    }
    
}
