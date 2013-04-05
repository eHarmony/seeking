/*
 *  Copyright 2012 eHarmony, Inc
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.eharmony.matching.seeking.query.geometry;

import static com.eharmony.matching.seeking.query.geometry.GenericMath.*;

/**
 * Create a box that encloses a geo-spherical distance search.
 * 
 * Math at http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
 */
public class GeoBoxMaker implements BoxMaker {

    public static enum DistanceUnits {
        DEGREES, KILOMETERS, MILES
    }

    protected static final int EARTH_RADIUS_KILOMETERS = 6371;
    protected static final int EARTH_RADIUS_MILES = 3959;
    protected static final double MIN_LAT = -Math.PI / 2;
    protected static final double MAX_LAT = Math.PI / 2;
    protected static final double MIN_LON = -Math.PI;
    protected static final double MAX_LON = Math.PI;
    
    protected static final double TWO_PI = 2D * Math.PI;

    private final DistanceUnits units;

    public GeoBoxMaker(DistanceUnits units) {
        this.units = units;
    }

    @Override
    // http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
    public <N extends Number & Comparable<N>> Box<N> make(Point<N> center,
            N radius) {
        // angular distance in radians on a great circle
        double radDist = radiansFromUnits(radius.doubleValue());

        double radLat = degreesToRadians(center.getX().doubleValue());
        double radLon = degreesToRadians(center.getY().doubleValue());
        double minLat = radLat - radDist;
        double maxLat = radLat + radDist;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) / Math.cos(radLat));
            minLon = radLon - deltaLon;
            maxLon = radLon + deltaLon;
            if (minLon < MIN_LON) {
                minLon += TWO_PI;
            }
            if (maxLon > MAX_LON) {
                maxLon -= TWO_PI;
            }
        } else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }
        
        // hack for the generic numbers
        N zero = subtract(radius, radius);
        N minLatN = add(zero, radiansToDegrees(minLat));
        N maxLatN = add(zero, radiansToDegrees(maxLat));
        N minLonN = add(zero, radiansToDegrees(minLon));
        N maxLonN = add(zero, radiansToDegrees(maxLon));

        return new Box<N>(
                new Point<N>(minLatN,minLonN),
                new Point<N>(maxLatN,maxLonN));
    }
    
    private double radiansFromUnits(double magnitude) {
        if (units == DistanceUnits.DEGREES) {
            return degreesToRadians(magnitude);
        } else if (units == DistanceUnits.KILOMETERS) {
            return kilometersToRadians(magnitude);
        } else if (units == DistanceUnits.MILES) {
            return milesToRadians(magnitude);
        } else {
            throw new IllegalArgumentException("Invalid distance units");
        }
    }

    public static double degreesToRadians(double degrees) {
        return (degrees * Math.PI) / 180;
    }

    public static double radiansToDegrees(double radians) {
        return (radians * 180) / Math.PI;
    }

    public static double kilometersToRadians(double kilometers) {
        return kilometers / EARTH_RADIUS_KILOMETERS;
    }

    public static double milesToRadians(double miles) {
        return miles / EARTH_RADIUS_MILES;
    }

}
