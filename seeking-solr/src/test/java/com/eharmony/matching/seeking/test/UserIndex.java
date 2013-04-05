package com.eharmony.matching.seeking.test;

import java.io.Serializable;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.eharmony.matching.seeking.query.geometry.XField;
import com.eharmony.matching.seeking.query.geometry.YField;

public class UserIndex implements Serializable {

    public static class LatLon {

        @XField
        private double lat;
        @YField
        private double lon;

        public LatLon() {
        }

        public LatLon(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(lat);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(lon);
            result = prime * result + (int) (temp ^ (temp >>> 32));
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
            LatLon other = (LatLon) obj;
            if (Double.doubleToLongBits(lat) != Double
                    .doubleToLongBits(other.lat))
                return false;
            if (Double.doubleToLongBits(lon) != Double
                    .doubleToLongBits(other.lon))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "LatLon [lat=" + lat + ", lon=" + lon + "]";
        }

    }

    private static final long serialVersionUID = 1650521149393159447L;
    @Field("user_id")
    private int userId;
    @Field("id")
    private String idString;
    @Field("birth_date")
    private Date birthDate;
    @Field
    private int language;
    private LatLon latLon = new LatLon();
    @Field("country")
    private int countryID;
    @Field("state")
    private int stateID;

    public UserIndex() {
    }

    // Solr: The format is "$latitude,$longitude"
    public String getLatLonString() {
        return getLat() + "," + getLon();
    }

    @Field("location")
    public void setLatLonString(String latLonString) {
        String[] strings = latLonString.split(",");
        setLat(Double.parseDouble(strings[0].trim()));
        setLon(Double.parseDouble(strings[1].trim()));
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        this.idString = Integer.toString(userId);
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public double getLat() {
        return getLatLon().getLat();
    }

    public void setLat(double lat) {
        getLatLon().setLat(lat);
    }

    public double getLon() {
        return getLatLon().getLon();
    }

    public void setLon(double lon) {
        getLatLon().setLon(lon);
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((birthDate == null) ? 0 : birthDate.hashCode());
        result = prime * result + countryID;
        result = prime * result
                + ((idString == null) ? 0 : idString.hashCode());
        result = prime * result + language;
        result = prime * result + ((latLon == null) ? 0 : latLon.hashCode());
        result = prime * result + stateID;
        result = prime * result + userId;
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
        UserIndex other = (UserIndex) obj;
        if (birthDate == null) {
            if (other.birthDate != null)
                return false;
        } else if (!birthDate.equals(other.birthDate))
            return false;
        if (countryID != other.countryID)
            return false;
        if (idString == null) {
            if (other.idString != null)
                return false;
        } else if (!idString.equals(other.idString))
            return false;
        if (language != other.language)
            return false;
        if (latLon == null) {
            if (other.latLon != null)
                return false;
        } else if (!latLon.equals(other.latLon))
            return false;
        if (stateID != other.stateID)
            return false;
        if (userId != other.userId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserIndex [userId=" + userId + ", idString=" + idString
                + ", birthDate=" + birthDate + ", language=" + language
                + ", latLon=" + latLon + ", countryID=" + countryID
                + ", stateID=" + stateID + "]";
    }

}