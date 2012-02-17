/**
 *  Copyright 2010 Latiz Technologies, LLC
 *
 *  This file is part of Latiz.
 *
 *  Latiz is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Latiz is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Latiz.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.AandR.library.math;

/**
 *
 * @author Aaron Masino
 */
public class RandomNumber extends MTRandom {

    public RandomNumber() {
        super();
    }

    public RandomNumber(long seed) {
        super(seed);
    }

    /**
     * Creates a symmetric random triangular distribution between -0.5 and 0.5.
     * @ref http://physics.indiana.edu/~sg/p609/nov26.02
     * @param mean
     * @param std
     * @return
     */
    public double nextTriangular(double mean, double std) {
        double x = nextDouble();
        double y = nextDouble();
        return 0.5 * (x + y - 1.0) * std + mean;
    }

    /**
     * sigma = std(HWHM)/2.35 from Fundamentals of Photonics p.448
     * @param mean
     * @param std
     * @return
     */
    public double nextGaussian(double mean, double std) {
        return super.nextGaussian() * std / 2.35 + mean;
    }

    /**
     * Generates a random number given the mean and standard deviation.
     * @param mean
     * @param std
     * @return
     */
    public double nextUniform(double mean, double std) {
        return (nextDouble() - 0.5) * std + mean;
    }
}
