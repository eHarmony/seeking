package com.eharmony.matching.seeking.query.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eharmony.matching.seeking.query.geometry.GeoBoxMaker.DistanceUnits;

public class GeoBoxMakerTest {
    
    //private final GeoBoxMaker boxMaker = new GeoBoxMaker();
    
    @Test
    public void make_Degrees_equator() {
        GeoBoxMaker b = new GeoBoxMaker(DistanceUnits.DEGREES);
        Point<Double> p = Point.create(0D,0D);
        Box<Double> box = b.make(p, 1D);
        assertEquals(Box.create(-1D, -1D, 1D, 1D), box);
    }
    
    @Test
    public void make_Degrees_here() {
        GeoBoxMaker b = new GeoBoxMaker(DistanceUnits.DEGREES);
        Point<Double> p = new Point<Double>(34.02886,-118.473993);
        Box<Double> box = b.make(p, 1.0D);
        assertEquals(Box.create(33.02886, -119.68064899173066, 35.02886, -117.26733700826932), box);
    }
    
    @Test
    public void make_Kilometers_equator() {
        GeoBoxMaker b = new GeoBoxMaker(DistanceUnits.KILOMETERS);
        Point<Double> p = Point.create(0D,0D);
        Box<Double> box = b.make(p, 111D); // 1 degree ~ 111 kilometers
        Double almostOneDegree = 0.9982469825697909;
        assertEquals(Box.create(-almostOneDegree, -almostOneDegree,
                almostOneDegree, almostOneDegree), box);
    }

    @Test
    public void make_Miles_equator() {
        GeoBoxMaker b = new GeoBoxMaker(DistanceUnits.MILES);
        Point<Double> p = Point.create(0D,0D);
        Box<Double> box = b.make(p, 69D); // 1 degree ~ 69 miles
        Double almostOneDegree = 0.998587720738237;
        assertEquals(Box.create(-almostOneDegree, -almostOneDegree,
                almostOneDegree, almostOneDegree), box);
    }
    
    @Test
    public void degreesToRadians() {
        assertEquals(Math.PI, GeoBoxMaker.degreesToRadians(180D), 0);
        assertEquals(2 * Math.PI, GeoBoxMaker.degreesToRadians(360D), 0);
    }
    
    @Test
    public void radiansToDegrees() {
        assertEquals(180D, GeoBoxMaker.radiansToDegrees(Math.PI), 0);
        assertEquals(360D, GeoBoxMaker.radiansToDegrees(2 * Math.PI), 0);
    }
    
    @Test
    public void kilometersToRadians() {
        assertEquals(1D, GeoBoxMaker.kilometersToRadians(GeoBoxMaker.EARTH_RADIUS_KILOMETERS), 0);
    }
    
    @Test
    public void milesToRadians() {
        assertEquals(1D, GeoBoxMaker.milesToRadians(GeoBoxMaker.EARTH_RADIUS_MILES), 0);
    }

}
