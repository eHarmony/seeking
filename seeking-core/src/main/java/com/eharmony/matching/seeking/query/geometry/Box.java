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

/**
 * A box defined by the points a and b.
 */
public class Box<N extends Number & Comparable<N>> {

    public final Point<N> a;
    public final Point<N> b;

    public Box(Point<N> a, Point<N> b) {
        this.a = a;
        this.b = b;
    }
    
    /*
     * Because
     *      Box<Integer> box = Box.create(a,b)
     * is slightly less tedious than
     *      Box<Integer> box = new Box<Integer>(a,b)
     */
    public static <N extends Number & Comparable<N>> Box<N> create(
            Point<N> a, Point<N> b) {
        return new Box<N>(a, b);
    }
    
    public static <N extends Number & Comparable<N>> Box<N> create(
            N ax, N ay, N bx, N by) {
        return Box.create(new Point<N>(ax, ay), new Point<N>(bx, by));
    }

    public Point<N> getA() {
        return a;
    }

    public Point<N> getB() {
        return b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
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
        @SuppressWarnings("rawtypes")
        Box other = (Box) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Box [a=" + a + ", b=" + b + "]";
    }
}
