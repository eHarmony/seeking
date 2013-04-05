package com.eharmony.matching.seeking.test;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

public class TestClass implements Serializable {

    private static final long serialVersionUID = 1575247547810647214L;

    @Id
    private Long id;
    private String name;
    private Date date;
    private LatLon latLon;

    private TestClass() {
    }

    public TestClass(Long id, String name, double lat, double lon) {
        this();
        setId(id);
        setName(name);
        setDate(new Date());
        setLatLon(new LatLon(lat, lon));
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

    public LatLon getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((latLon == null) ? 0 : latLon.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (latLon == null) {
            if (other.latLon != null)
                return false;
        } else if (!latLon.equals(other.latLon))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestClass [id=" + id + ", name=" + name + ", date=" + date
                + ", latLon=" + latLon + "]";
    }
}
