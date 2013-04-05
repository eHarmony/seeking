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
 * A box maker that creates a box of size radius x radius around a center point.
 */
public class SimpleBoxMaker implements BoxMaker {

    @Override
    public <N extends Number & Comparable<N>> Box<N> make(Point<N> center, N radius) {
        N x0 = subtract(center.getX(), radius);
        N x1 = add(center.getX(), radius);
        N y0 = subtract(center.getY(), radius);
        N y1 = add(center.getY(), radius);
        Point<N> a = new Point<N>(min(x0, x1), min(y0, y1));
        Point<N> b = new Point<N>(max(x0, x1), max(y0, y1));
        return new Box<N>(a, b);
    }
}
