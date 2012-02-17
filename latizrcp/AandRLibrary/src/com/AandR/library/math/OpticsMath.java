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
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:25 $
 */
public class OpticsMath {

    public static final int PHASE_STRUCTURE_FUNCTION_X = 1;
    public static final int PHASE_STRUCTURE_FUNCTION_Y = 2;
    public static final int PHASE_STRUCTURE_FUNCTION_DIAGONAL = 3;

    /**
     * STRFCN computes the theoretical structure function including the effects of an outer scale.
     * Accuracy drops off rapidly as ra decreases below 1
     * @param ra The ratio of the distance to the atmospheric coherence length
     * @param rb The ratio of the outer scale to the distance. if rb is zero an infinite outer scale is assumed.
     * @param n
     * @return
     */
    public static double structureFunction(double ra, double rb, int n) {
        if (rb > 0) {
            double fiveSixths = 5.0 / 6.0;
            double oneSixth = 1.0 / 6.0;
            double c = Math.PI * Math.PI / (rb * rb);
            double a = Math.pow(0.091650 * Math.PI * Math.PI * (Math.PI * ra), 5.0 / 3.0) / (.94065585825677 * .94065585825677); //gamma(11/6)~.9407
            double b = -Math.pow(Math.PI / rb, 1.0 / 3.0) * 1.01394443788955; //!gamma(11/6)/gamma(7/6)~1.014
            double as = a * (1.0 + b);
            for (int k = 1; k <= n; k++) {
                a *= c / (k * (k + fiveSixths));
                b *= (k + fiveSixths) / (k + oneSixth);
                as += a * (1.0 + b / (k + 1.0));
            }
            return as;
        }
        return 6.88 * Math.pow(ra, 5.0 / 3.0);
    }

    public static double[] phaseStructureFunction(double[][][] phaseScreens, int direction) {
        double[] structureFunction = null;
        double factor;
        int np = phaseScreens.length;
        int nx = phaseScreens[0].length;
        int ny = phaseScreens[0][0].length;
        int i, j, k, m;
        switch (direction) {
            case PHASE_STRUCTURE_FUNCTION_X:
                structureFunction = new double[nx - 1];
                for (m = 0; m < nx - 1; m++) {
                    structureFunction[m] = 0.0;
                    for (k = 0; k < np; k++) {
                        for (j = 0; j < ny; j++) {
                            for (i = 0; i < nx - (m + 1); i++) {
                                structureFunction[m] += (phaseScreens[k][i + (m + 1)][j] - phaseScreens[k][i][j]) * (phaseScreens[k][i + (m + 1)][j] - phaseScreens[k][i][j]);
                            }
                        }
                    }
                    factor = np * ny * (nx - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;

            case PHASE_STRUCTURE_FUNCTION_Y:
                structureFunction = new double[ny - 1];
                for (m = 0; m < ny - 1; m++) {
                    for (k = 0; k < np; k++) {
                        for (j = 0; j < ny - (m + 1); j++) {
                            for (i = 0; i < nx; i++) {
                                structureFunction[m] += (phaseScreens[k][i][j + (m + 1)] - phaseScreens[k][i][j]) * (phaseScreens[k][i][j + (m + 1)] - phaseScreens[k][i][j]);
                            }
                        }
                    }
                    factor = np * nx * (ny - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;

            case PHASE_STRUCTURE_FUNCTION_DIAGONAL:
                int nn = Math.min(nx - 1, ny - 1);
                structureFunction = new double[nn];
                for (m = 0; m < nn; m++) {
                    for (k = 0; k < np; k++) {
                        for (j = 0; j < ny - m; j++) {
                            for (i = 0; i < nx - m; i++) {
                                structureFunction[m] += (phaseScreens[k][i + (m + 1)][j + (m + 1)] - phaseScreens[k][i][j]) * (phaseScreens[k][i + (m + 1)][j + (m + 1)] - phaseScreens[k][i][j]);
                            }
                        }
                    }
                    factor = np * (ny - (m + 1)) * (nx - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;
        }
        return structureFunction;
    }

    /**
     * Compute the structure function from phase screen data.
     * @param phaseScreen The phase screen data.
     * @param direction The direction over which to compute the phase structure function. Choice can be
     * accessed via this classes static final variables.
     * @return
     */
    public static double[] phaseStructureFunction(double[][] phaseScreen, int direction) {
        double[] structureFunction = null;
        double factor;
        int nx = phaseScreen.length;
        int ny = phaseScreen[0].length;
        int i, j, m;
        switch (direction) {
            case PHASE_STRUCTURE_FUNCTION_X:
                structureFunction = new double[nx - 1];
                for (m = 0; m < nx - 1; m++) {
                    structureFunction[m] = 0.0;
                    for (j = 0; j < ny; j++) {
                        for (i = 0; i < nx - (m + 1); i++) {
                            structureFunction[m] += (phaseScreen[i + (m + 1)][j] - phaseScreen[i][j]) * (phaseScreen[i + (m + 1)][j] - phaseScreen[i][j]);
                        }
                    }
                    factor = ny * (nx - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;

            case PHASE_STRUCTURE_FUNCTION_Y:
                structureFunction = new double[ny - 1];
                for (m = 0; m < ny - 1; m++) {
                    for (j = 0; j < ny - (m + 1); j++) {
                        for (i = 0; i < nx; i++) {
                            structureFunction[m] += (phaseScreen[i][j + (m + 1)] - phaseScreen[i][j]) * (phaseScreen[i][j + (m + 1)] - phaseScreen[i][j]);
                        }
                    }
                    factor = nx * (ny - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;

            case PHASE_STRUCTURE_FUNCTION_DIAGONAL:
                int np = Math.min(nx - 1, ny - 1);
                structureFunction = new double[np];
                for (m = 0; m < np; m++) {
                    for (j = 0; j < ny - m; j++) {
                        for (i = 0; i < nx - m; i++) {
                            structureFunction[m] += (phaseScreen[i + (m + 1)][j + (m + 1)] - phaseScreen[i][j]) * (phaseScreen[i + (m + 1)][j + (m + 1)] - phaseScreen[i][j]);
                        }
                    }
                    factor = (ny - (m + 1)) * (nx - (m + 1));
                    structureFunction[m] /= factor;
                }
                break;
        }
        return structureFunction;
    }

    /**
     * Compute the intensity of the complex wavefront.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final float[][] computeIntensity(float[][] rePart, float[][] imPart) {
        return computeIntensity(rePart, imPart, 1f, 1f);
    }

    /**
     * Compute the intensity of the complex wavefront.
     * @param rePart
     * @param imPart
     * @param dx
     * @param dy
     * @return
     */
    public static final float[][] computeIntensity(float[][] rePart, float[][] imPart, float dx, float dy) {
        int nx = rePart.length;
        int ny = rePart[0].length;
        float[][] intensity = new float[nx][ny];

        int i, j;
        for (j = 0; j < ny; j++) {
            for (i = 0; i < nx; i++) {
                intensity[i][j] = (rePart[i][j] * rePart[i][j] + imPart[i][j] * imPart[i][j]) / dx / dy;
            }
        }
        return intensity;
    }

    /**
     * Compute the intensity of the complex wavefront.
     * @param rePart
     * @param imPart
     * @param dx
     * @param dy
     * @return
     */
    public static final double[][] computeIntensity(double[][] rePart, double[][] imPart, double dx, double dy) {
        int nx = rePart.length;
        int ny = rePart[0].length;
        double[][] intensity = new double[nx][ny];

        int i, j;
        for (j = 0; j < ny; j++) {
            for (i = 0; i < nx; i++) {
                intensity[i][j] = (rePart[i][j] * rePart[i][j] + imPart[i][j] * imPart[i][j]) / dx / dy;
            }
        }
        return intensity;
    }

    public static final double[][] computeAmplitude(double[][] rePart, double[][] imPart) {
        return computeIntensity(rePart, imPart, 1.0, 1.0);
    }

    public static final double[][] computeAmplitude(double[][] data) {
        double[][] amp = new double[data.length][data[0].length / 2];
        int jRe, jIm;
        for (int j = 0; j < amp[0].length; j++) {
            jRe = 2 * j;
            jIm = jRe + 1;
            for (int i = 0; i < amp.length; i++) {
                amp[i][j] = Math.sqrt(data[i][jRe] * data[i][jRe] + data[i][jIm] * data[i][jIm]);
            }
        }
        return amp;
    }

    public static final double[][] computeIntensity(double[][] data) {
        double[][] amp = new double[data.length][data[0].length / 2];
        int jRe, jIm;
        for (int j = 0; j < amp[0].length; j++) {
            jRe = 2 * j;
            jIm = jRe + 1;
            for (int i = 0; i < amp.length; i++) {
                amp[i][j] = data[i][jRe] * data[i][jRe] + data[i][jIm] * data[i][jIm];
            }
        }
        return amp;
    }

    public static final double[][] computePhase(double[][] data) {
        double[][] phase = new double[data.length][data[0].length / 2];
        int i, j, jRe, jIm;

        double piBy2 = Math.PI / 2.0;
        for (j = 0; j < phase[0].length; j++) {
            jRe = 2 * j;
            jIm = jRe + 1;
            for (i = 0; i < phase.length; i++) {
                if (data[i][jRe] != 0.0) {
                    phase[i][j] = Math.atan2(data[i][jIm], data[i][jRe]);
                } else {
                    if (data[i][jIm] != 0.0) {
                        phase[i][j] = piBy2 * Math.signum(data[i][jIm]);
                    } else {
                        phase[i][j] = 0;
                    }
                }
            }
        }
        return phase;
    }

    public static final double[][] computePistonRemovedPhase(double[][] rePart, double[][] imPart) {
        double[][] phase = computePhase(rePart, imPart);

        double average = 0;
        for (int j = 0; j < phase[0].length; j++) {
            for (int i = 0; i < phase.length; i++) {
                average += phase[i][j];
            }
        }
        average /= (double) (phase.length * phase[0].length);

        double[][] pistonRemovedPhase = new double[phase.length][phase[0].length];
        for (int j = 0; j < phase[0].length; j++) {
            for (int i = 0; i < phase.length; i++) {
                pistonRemovedPhase[i][j] = (phase[i][j] - average) - 2.0 * Math.PI * Math.round((phase[i][j] - average) / (2.0 * Math.PI));
            }
        }
        return pistonRemovedPhase;
    }

    public static final double[][] computePistonRemovedPhase(double[][] data) {
        double[][] phase = computePhase(data);

        double average = 0;
        for (int j = 0; j < phase[0].length; j++) {
            for (int i = 0; i < phase.length; i++) {
                average += phase[i][j];
            }
        }
        average /= (double) (phase.length * phase[0].length);

        double[][] pistonRemovedPhase = new double[phase.length][phase[0].length];
        for (int j = 0; j < phase[0].length; j++) {
            for (int i = 0; i < phase.length; i++) {
                pistonRemovedPhase[i][j] = (phase[i][j] - average) - 2.0 * Math.PI * Math.round((phase[i][j] - average) / (2.0 * Math.PI));
            }
        }
        return pistonRemovedPhase;
    }

    /**
     *  ses a simple algorithm to attempt to remove the phase tears from a phasefront.
     *  Original fortran version written by Dr. Donald J. Link
     *  Converted to Java by Dr. Richard St. John
     * @param data
     * @return
     */
    public static final double[][] computeUnwrappedPhase(double[][] rePart, double[][] imPart) {
        double s = 0.05; //amount of slope info to use
        double s1 = 1 + s;
        double sh = 0.5 * s;
        double sh1 = 0.5 * (1 + s);
        double pi = Math.PI;
        double t = pi + pi;

        double[][] p = computePhase(rePart, imPart);
        int i1 = 0;
        int i2 = p.length - 1;
        int j1 = 0;
        int j2 = p[0].length - 1;

        int mx = (i1 + i2 + 1) / 2;
        int my = (j1 + j2 + 1) / 2;

        p[mx - 1][my] = p[mx - 1][my] - Math.round((p[mx - 1][my] - p[mx][my]) / t) * t;
        p[mx][my - 1] = p[mx][my - 1] - Math.round((p[mx][my - 1] - p[mx][my]) / t) * t;
        p[mx - 1][my - 1] = p[mx - 1][my - 1] - Math.round((p[mx - 1][my - 1] - .5 * (p[mx][my - 1] + p[mx - 1][my])) / t) * t;

        for (int i = mx + 1; i <= i2; i++) {
            p[i][my] = p[i][my] - Math.round((p[i][my] - s1 * p[i - 1][my] + s * p[i - 2][my]) / t) * t;
            p[i][my - 1] = p[i][my - 1] - Math.round((p[i][my - 1] - .5 * p[i][my] - sh1 * p[i - 1][my - 1] + sh * p[i - 2][my - 1]) / t) * t;
        }
        for (int i = mx - 2; i >= i1; i--) {
            p[i][my] = p[i][my] - Math.round((p[i][my] - s1 * p[i + 1][my] + s * p[i + 2][my]) / t) * t;
            p[i][my - 1] = p[i][my - 1] - Math.round((p[i][my - 1] - .5 * p[i][my] - sh1 * p[i + 1][my - 1] + sh * p[i + 2][my - 1]) / t) * t;
        }
        for (int j = my + 1; j <= j2; j++) {
            p[mx][j] = p[mx][j] - Math.round((p[mx][j] - s1 * p[mx][j - 1] + s * p[mx][j - 2]) / t) * t;
            p[mx - 1][j] = p[mx - 1][j] - Math.round((p[mx - 1][j] - .5 * p[mx][j] - sh1 * p[mx - 1][j - 1] + sh * p[mx - 1][j - 2]) / t) * t;
            for (int i = mx + 1; i <= i2; i++) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i - 1][j] + p[i][j - 1]) + sh * (p[i - 2][j] + p[i][j - 2])) / t) * t;
            }
            for (int i = mx - 2; i >= i1; i--) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i + 1][j] + p[i][j - 1]) + sh * (p[i + 2][j] + p[i][j - 2])) / t) * t;
            }
        }
        for (int j = my - 1; j >= j1; j--) {
            p[mx][j] = p[mx][j] - Math.round((p[mx][j] - s1 * p[mx][j + 1] + s * p[mx][j + 2]) / t) * t;
            p[mx - 1][j] = p[mx - 1][j] - Math.round((p[mx - 1][j] - .5 * p[mx][j] - sh1 * p[mx - 1][j + 1] + sh * p[mx - 1][j + 2]) / t) * t;
            for (int i = mx + 1; i <= i2; i++) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i - 1][j] + p[i][j + 1]) + sh * (p[i - 2][j] + p[i][j + 2])) / t) * t;
            }
            for (int i = mx - 2; i >= i1; i--) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i + 1][j] + p[i][j + 1]) + sh * (p[i + 2][j] + p[i][j + 2])) / t) * t;
            }
        }
        return p;
    }

    /**
     *  ses a simple algorithm to attempt to remove the phase tears from a phasefront.
     *  Original fortran version written by Dr. Donald J. Link
     *  Converted to Java by Dr. Richard St. John
     * @param data
     * @return
     */
    public static final double[][] computeUnwrappedPhase(double[][] data) {
        double s = 0.05; //amount of slope info to use
        double s1 = 1 + s;
        double sh = 0.5 * s;
        double sh1 = 0.5 * (1 + s);
        double pi = Math.PI;
        double t = pi + pi;

        double[][] p = computePhase(data);
        int i1 = 0;
        int i2 = p.length - 1;
        int j1 = 0;
        int j2 = p[0].length - 1;

        int mx = (i1 + i2 + 1) / 2;
        int my = (j1 + j2 + 1) / 2;

        p[mx - 1][my] = p[mx - 1][my] - Math.round((p[mx - 1][my] - p[mx][my]) / t) * t;
        p[mx][my - 1] = p[mx][my - 1] - Math.round((p[mx][my - 1] - p[mx][my]) / t) * t;
        p[mx - 1][my - 1] = p[mx - 1][my - 1] - Math.round((p[mx - 1][my - 1] - .5 * (p[mx][my - 1] + p[mx - 1][my])) / t) * t;

        for (int i = mx + 1; i <= i2; i++) {
            p[i][my] = p[i][my] - Math.round((p[i][my] - s1 * p[i - 1][my] + s * p[i - 2][my]) / t) * t;
            p[i][my - 1] = p[i][my - 1] - Math.round((p[i][my - 1] - .5 * p[i][my] - sh1 * p[i - 1][my - 1] + sh * p[i - 2][my - 1]) / t) * t;
        }
        for (int i = mx - 2; i >= i1; i--) {
            p[i][my] = p[i][my] - Math.round((p[i][my] - s1 * p[i + 1][my] + s * p[i + 2][my]) / t) * t;
            p[i][my - 1] = p[i][my - 1] - Math.round((p[i][my - 1] - .5 * p[i][my] - sh1 * p[i + 1][my - 1] + sh * p[i + 2][my - 1]) / t) * t;
        }
        for (int j = my + 1; j <= j2; j++) {
            p[mx][j] = p[mx][j] - Math.round((p[mx][j] - s1 * p[mx][j - 1] + s * p[mx][j - 2]) / t) * t;
            p[mx - 1][j] = p[mx - 1][j] - Math.round((p[mx - 1][j] - .5 * p[mx][j] - sh1 * p[mx - 1][j - 1] + sh * p[mx - 1][j - 2]) / t) * t;
            for (int i = mx + 1; i <= i2; i++) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i - 1][j] + p[i][j - 1]) + sh * (p[i - 2][j] + p[i][j - 2])) / t) * t;
            }
            for (int i = mx - 2; i >= i1; i--) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i + 1][j] + p[i][j - 1]) + sh * (p[i + 2][j] + p[i][j - 2])) / t) * t;
            }
        }
        for (int j = my - 1; j >= j1; j--) {
            p[mx][j] = p[mx][j] - Math.round((p[mx][j] - s1 * p[mx][j + 1] + s * p[mx][j + 2]) / t) * t;
            p[mx - 1][j] = p[mx - 1][j] - Math.round((p[mx - 1][j] - .5 * p[mx][j] - sh1 * p[mx - 1][j + 1] + sh * p[mx - 1][j + 2]) / t) * t;
            for (int i = mx + 1; i <= i2; i++) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i - 1][j] + p[i][j + 1]) + sh * (p[i - 2][j] + p[i][j + 2])) / t) * t;
            }
            for (int i = mx - 2; i >= i1; i--) {
                p[i][j] = p[i][j] - Math.round((p[i][j] - sh1 * (p[i + 1][j] + p[i][j + 1]) + sh * (p[i + 2][j] + p[i][j + 2])) / t) * t;
            }
        }
        return p;
    }

    /**
     * Computes the amplitude of the complex wavefront.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final float[][] computeAmplitude(float[][] rePart, float[][] imPart) {
        return computeAmplitude(rePart, imPart, 1f, 1f);
    }

    /**
     * Computes the amplitude of the complex wavefront.
     * @param rePart
     * @param imPart
     * @param dx
     * @param dy
     * @return
     */
    public static final float[][] computeAmplitude(float[][] rePart, float[][] imPart, float dx, float dy) {
        int nx = rePart.length;
        int ny = rePart[0].length;
        float[][] amplitude = new float[nx][ny];

        int i, j;
        for (j = 0; j < ny; j++) {
            for (i = 0; i < nx; i++) {
                amplitude[i][j] = (float) Math.sqrt(rePart[i][j] * rePart[i][j] + imPart[i][j] * imPart[i][j]) / dx / dy;
            }
        }
        return amplitude;
    }

    /**
     * Computes the phase of the complex wavefront. Phase tears are not accounted for in this algorithm.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final float[][] computePhase(float[][] rePart, float[][] imPart) {
        int nx = rePart.length;
        int ny = rePart[0].length;

        float[][] phase = new float[nx][ny];
        float piBy2 = (float) (Math.PI / 2.0);

        int i, j;
        for (j = 0; j < phase[0].length; j++) {
            for (i = 0; i < phase.length; i++) {
                if (rePart[i][j] != 0.0) {
                    phase[i][j] = (float) Math.atan2(imPart[i][j], rePart[i][j]);
                } else {
                    if (imPart[i][j] != 0.0) {
                        phase[i][j] = piBy2 * Math.signum(imPart[i][j]);
                    } else {
                        phase[i][j] = 0f;
                    }
                }
            }
        }
        return phase;
    }

    /**
     * Computes the phase of the complex wavefront. Phase tears are not accounted for in this algorithm.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final double[][] computePhase(double[][] rePart, double[][] imPart) {
        int nx = rePart.length;
        int ny = rePart[0].length;

        double[][] phase = new double[nx][ny];
        double piBy2 = Math.PI / 2.0;

        int i, j;
        for (j = 0; j < phase[0].length; j++) {
            for (i = 0; i < phase.length; i++) {
                if (rePart[i][j] != 0.0) {
                    phase[i][j] = Math.atan2(imPart[i][j], rePart[i][j]);
                } else {
                    if (imPart[i][j] != 0.0) {
                        phase[i][j] = piBy2 * Math.signum(imPart[i][j]);
                    } else {
                        phase[i][j] = 0;
                    }
                }
            }
        }
        return phase;
    }

    /**
     * Compute the total power.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final double computeTotalPowerFromRealField(double[][] data) {
        int nx = data.length;
        int ny = data[0].length;
        int i, j;
        float sum = 0;
        for (i = 0; i < nx; i++) {
            for (j = 0; j < ny; j++) {
                sum += data[i][j] * data[i][j] + data[i][j] * data[i][j];
            }
        }
        return sum;
    }

    /**
     * Compute the total power.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final double computeTotalPowerFromComplexField(double[][] cmplxData) {
        int nx = cmplxData.length / 2;
        int ny = cmplxData[0].length;
        int i, j;
        float sum = 0;
        for (i = 0; i < nx; i++) {
            for (j = 0; j < ny; j++) {
                sum += cmplxData[2 * i][j] * cmplxData[2 * i][j] + cmplxData[2 * i + 1][j] * cmplxData[2 * i + 1][j];
            }
        }
        return sum;
    }

    /**
     * Compute the total power.
     * @param rePart
     * @param imPart
     * @return
     */
    public static final float computeTotalPower(float[][] rePart, float[][] imPart) {
        int nx = rePart.length;
        int ny = rePart[0].length;
        int i, j;
        float sum = 0;
        for (j = 0; j < ny; j++) {
            for (i = 0; i < nx; i++) {
                sum += rePart[i][j] * rePart[i][j] + imPart[i][j] * imPart[i][j];
            }
        }
        return sum;
    }
}
