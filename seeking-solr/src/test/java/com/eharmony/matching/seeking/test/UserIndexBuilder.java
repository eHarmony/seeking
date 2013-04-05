package com.eharmony.matching.seeking.test;

import java.util.Calendar;
import java.util.Date;

import com.eharmony.matching.seeking.test.UserIndex.LatLon;

public class UserIndexBuilder {

    public static final int ENGLISH = 1;
    public static final int SPANISH = 2;
    public static final int JAPANESE = 3;

    private int user_id;
    private Date birthDate;
    private int language;
    private double lat;
    private double lon;
    private int countryID;
    private int stateID;

    private int age;

    /**
     * Creates a new builder, which will generate a user with sensible default
     * settings.
     */
    public UserIndexBuilder() {
        defaultAge();
        defaultLanguage();
        defaultGeography();
    }

    /**
     * Create the user that's been built so far.
     * 
     * @return Instantiated user object
     */
    public UserIndex build() {
        UserIndex user = new UserIndex();
        user.setUserId(user_id);
        user.setLanguage(language);
        user.setCountryID(countryID);
        user.setStateID(stateID);

        user.setLatLon(new LatLon(lat, lon));

        if (birthDate == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.roll(Calendar.YEAR, -1 * age);
            birthDate = c.getTime();
        }
        user.setBirthDate(birthDate);

        return user;
    }

    public UserIndexBuilder id(int id) {
        this.user_id = id;
        return this;
    }

    public UserIndexBuilder defaultGeography() {
        return this.country(1).state(5).ziplat(34.028).ziplong(-118.474);
    }

    public UserIndexBuilder country(int country) {
        this.countryID = country;
        return this;
    }

    public UserIndexBuilder state(int state) {
        this.stateID = state;
        return this;
    }

    public UserIndexBuilder ziplong(double ziplong) {
        this.lon = ziplong;
        return this;
    }

    public UserIndexBuilder ziplat(double ziplat) {
        this.lat = ziplat;
        return this;
    }

    public UserIndexBuilder language(int language) {
        this.language = language;
        return this;
    }

    public UserIndexBuilder defaultLanguage() {
        return language(ENGLISH);
    }

    public UserIndexBuilder defaultAge() {
        return age(30);
    }

    public UserIndexBuilder age(int age) {
        this.age = age;
        return this;
    }

    public UserIndexBuilder birthDate(Date birthDate) {
        this.birthDate = new Date(birthDate.getTime());
        return this;
    }
}
