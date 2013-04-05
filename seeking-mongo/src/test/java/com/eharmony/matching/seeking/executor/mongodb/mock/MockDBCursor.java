package com.eharmony.matching.seeking.executor.mongodb.mock;

import java.util.Arrays;
import java.util.Iterator;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MockDBCursor extends DBCursor implements Iterator<DBObject> , Iterable<DBObject> {

    private final Iterator<DBObject> found;
    
    public MockDBCursor(DBCollection collection, DBObject... found) {
        super(collection, null, null, null);
        this.found = Arrays.asList(found).iterator();
    }
    
    @Override
    public Iterator<DBObject> iterator() {
        return found;
    }

    @Override
    public boolean hasNext() {
        return iterator().hasNext();
    }

    @Override
    public DBObject next() {
        return iterator().next();
    }

    @Override
    public void remove() {
        iterator().remove();
    }

}
