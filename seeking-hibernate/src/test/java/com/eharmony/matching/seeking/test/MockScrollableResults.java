package com.eharmony.matching.seeking.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.type.Type;

public class MockScrollableResults implements ScrollableResults {

    private final Object[] results;
    private int position = -1; 
    
    public MockScrollableResults(Object...objects) {
        this.results = objects;
    }
    
    @Override
    public void afterLast() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeFirst() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws HibernateException {
    }

    @Override
    public boolean first() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] get() throws HibernateException {
        return new Object[] { results[position] };
    }

    @Override
    public Object get(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getBigInteger(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBinary(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Blob getBlob(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean getBoolean(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte getByte(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar getCalendar(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Character getCharacter(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Clob getClob(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double getDouble(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float getFloat(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getInteger(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getLong(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowNumber() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short getShort(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimeZone getTimeZone(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type getType(int arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirst() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLast() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean last() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean next() throws HibernateException {
        return ++position < results.length;
    }

    @Override
    public boolean previous() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean scroll(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setRowNumber(int arg0) throws HibernateException {
        throw new UnsupportedOperationException();
    }

}
