package com.eharmony.matching.seeking.test;

import org.apache.solr.client.solrj.beans.Field;

public class TestClassTuple2 {

    @Field("id")
    private Long identificationNumber;
    @Field
    private String name;

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(Long identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
