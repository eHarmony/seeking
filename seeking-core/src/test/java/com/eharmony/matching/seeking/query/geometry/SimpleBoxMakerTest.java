package com.eharmony.matching.seeking.query.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimpleBoxMakerTest {
    
    private final SimpleBoxMaker boxMaker = new SimpleBoxMaker();
    
    private final Point<Double> p = new Point<Double>(34.02886,-118.473988);
    private final Double radius = 3.1415926535897931;
    
    @Test
    public void makeBox() {
        Box<Double> expected = new Box<Double>(
            new Point<Double>(p.getX() - radius, p.getY() - radius),
            new Point<Double>(p.getX() + radius, p.getY() + radius)
        );
        assertEquals(expected, boxMaker.make(p, radius));
    }

    @Test
    public void makeBox_negativeRadius() {
        Box<Double> expected = new Box<Double>(
            new Point<Double>(p.getX() - radius, p.getY() - radius),
            new Point<Double>(p.getX() + radius, p.getY() + radius)
        );
        assertEquals(expected, boxMaker.make(p, -radius));
    }
}
