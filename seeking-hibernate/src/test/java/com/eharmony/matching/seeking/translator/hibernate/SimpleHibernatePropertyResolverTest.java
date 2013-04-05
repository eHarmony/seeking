package com.eharmony.matching.seeking.translator.hibernate;

import static org.junit.Assert.assertEquals;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

import com.eharmony.matching.seeking.query.geometry.XField;
import com.eharmony.matching.seeking.query.geometry.YField;

@SuppressWarnings("unused")
public class SimpleHibernatePropertyResolverTest {
    
    public static class LatLon {
        @XField
        private double latitude;
        @YField
        private double longitude;
    }
    
    public static class LatLonCustom {
        @XField("lat")
        private double latitude;
        @YField("lon")
        private double longitude;
    }
    
    public static class LatLonSub extends LatLon {
    }
    
    @Entity(name = "without")
    public static class NoGeo {
        private Object property;
    }
    
    @Table(name = "with")
    public static class WithLatLon {
        private String name;
        private LatLon latlon;     
    }
    
    public static class WithLatLonCustom {
        private String name;
        private LatLonCustom latlon;    
    }
    
    public static class WithLatLonSub {
        private String name;
        private LatLonSub latlon;    
    }
    
    private final SimpleHibernatePropertyResolver resolver = new SimpleHibernatePropertyResolver();
    
    @Test
    public void testXField_LatLon() {
        String resolved = resolver.resolveXField("latlon", WithLatLon.class);
        assertEquals("latlon.latitude", resolved);
    }
    
    @Test
    public void testYField_LatLon() {
        String resolved = resolver.resolveYField("latlon", WithLatLon.class);
        assertEquals("latlon.longitude", resolved);
    }
    
    @Test
    public void testXField_LatLonCustom() {
        String resolved = resolver.resolveXField("latlon", WithLatLonCustom.class);
        assertEquals("latlon.lat", resolved);
    }
    
    @Test
    public void testYField_LatLonCustom() {
        String resolved = resolver.resolveYField("latlon", WithLatLonCustom.class);
        assertEquals("latlon.lon", resolved);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testXField_LatLonSub_IllegalArgumentException() {
        resolver.resolveXField("latlon", WithLatLonSub.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testYField_LatLonSub_IllegalArgumentException() {
        resolver.resolveYField("latlon", WithLatLonSub.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testXField_NoGeo_IllegalArgumentException_noSubFieldAnnotation() {
        resolver.resolveXField("property", NoGeo.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testXField_NoGeo_IllegalArgumentException_notValidProperty() {
        resolver.resolveXField("notvalidproperty", NoGeo.class);
    }
}
