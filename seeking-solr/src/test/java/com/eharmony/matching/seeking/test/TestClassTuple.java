package com.eharmony.matching.seeking.test;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class TestClassTuple {

    @Field
    private String name;
    @Field("date")
    private Date theDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTheDate() {
        return theDate;
    }

    public void setTheDate(Date theDate) {
        this.theDate = theDate;
    }

}
