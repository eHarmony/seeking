package com.eharmony.matching.seeking.test;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class TestClass {

    public static class TestEmbeddedClass {
        @Field
        private int first;
        @Field
        private int second;

        private TestEmbeddedClass() {
        }

        public TestEmbeddedClass(int first, int second) {
            this();
            setFirst(first);
            setSecond(second);
        }

        public int getFirst() {
            return first;
        }

        private void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        private void setSecond(int second) {
            this.second = second;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + first;
            result = prime * result + second;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TestEmbeddedClass other = (TestEmbeddedClass) obj;
            if (first != other.first)
                return false;
            if (second != other.second)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "TestEmbeddedClass [first=" + first + ", second=" + second
                    + "]";
        }
    }

    @Field
    private Long id;
    @Field
    private String name;
    @Field
    private Date date;
    @Field
    private TestEmbeddedClass embeddedObject;
    @Field("thatProperty")
    private String thisProperty = "irrelevant";

    public TestClass() {
    }

    public TestClass(Long id, String name, int first, int second) {
        this();
        setId(id);
        setName(name);
        setDate(new Date());
        setEmbeddedObject(new TestEmbeddedClass(first, second));
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    public TestEmbeddedClass getEmbeddedObject() {
        return embeddedObject;
    }

    private void setEmbeddedObject(TestEmbeddedClass embeddedObject) {
        this.embeddedObject = embeddedObject;
    }

    public String getThisProperty() {
        return thisProperty;
    }

    public void setThisProperty(String thisProperty) {
        this.thisProperty = thisProperty;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result
                + ((embeddedObject == null) ? 0 : embeddedObject.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((thisProperty == null) ? 0 : thisProperty.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestClass other = (TestClass) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (embeddedObject == null) {
            if (other.embeddedObject != null)
                return false;
        } else if (!embeddedObject.equals(other.embeddedObject))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (thisProperty == null) {
            if (other.thisProperty != null)
                return false;
        } else if (!thisProperty.equals(other.thisProperty))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestClass [id=" + id + ", name=" + name + ", date=" + date
                + ", embeddedObject=" + embeddedObject + ", thisProperty="
                + thisProperty + "]";
    }
}
