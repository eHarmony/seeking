package com.eharmony.matching.seeking.test;

import com.google.code.morphia.annotations.Id;

public class TestClassTuple2 {
    
    @Id
    private Long identificationNumber;
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
