package com.eharmony.matching.seeking.test;

import java.util.Date;

import com.google.code.morphia.annotations.Property;

public class TestClassTuple {

    private String name;
    @Property("date")
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
