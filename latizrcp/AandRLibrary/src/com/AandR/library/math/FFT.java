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
 * @author  Dr. Richard St. John
 * @version  $Revision: 1.1 $, $Date: 2007/05/25 00:12:25 $
 */
public class FFT {

  public static final int FORWARD = 1;
  public static final int INVERSE = -1;
  public static final int UNSCALED_INVERSE = -2;
  public static final int LINK_ALGORITHM = 1;
  public static final int NUMERICAL_RECIPES_ALGORITHM = 2;
  
  private int direction, algorithm;

  private int[] twiddleFactors;
  
  private double[] twiddleReal, twiddleImag;

  public FFT(int dir) {
    direction = dir;
    algorithm = LINK_ALGORITHM;
  }

  public FFT() {
    direction = 1;
    algorithm = LINK_ALGORITHM;
  }

  /**
   * @param algorithm  The algorithm to set.
   */
  public void setAlgorithm(int alg) {
    algorithm = alg;
  }

  /**
   * @return  Returns the algorithm.
   */
  public int getAlgorithm() {
    return algorithm;
  }

  /**
   * @param direction  The direction to set.
   */
  public void setDirection(int dir) {
    direction = dir;
  }

  /**
   * @return  Returns the direction.
   */
  public int getDirection() {
    return direction;
  }

/**
 * Compute the FFT
 * @param real
 * @param imag
 */  
  public void compute(double[] real, double[] imag) {
    compute(real, imag, direction);
  }

/**
 * Compute the FFT using the given algorithm. The transformed data is returned in the original arrays.
 * @param real Must be defined with length nx
 * @param imag Must be defined with length nx
 * @param isign 1 for forward and -1 for inverse
 */  
  public void compute(double[] real, double[] imag, int isign) {
    if(algorithm == LINK_ALGORITHM) {
      direction = isign;
      int nx = real.length;
      
      computeTwiddleFactors(direction, nx);
      fft1d(real, imag);
      
    } else if(algorithm == NUMERICAL_RECIPES_ALGORITHM) {
      int n = real.length;
      double[] data = new double[2*n];

      int k = 0;
      for(int i=0; i<real.length; i++) {
        data[k] = real[i];
        data[k+1] = imag[i];
        k+=2;
      }

      data = four1(data, isign);

      k = 0;
      for(int i=0; i<n; i++) {
        real[i] = data[k];
        imag[i] = data[k+1];
        k+=2;
      }
    }
  }

  protected double[] four1(double[] data, int isign) {
    int i, j, n, nn, m, mmax, istep;
    double theta, wtemp, wpr, wpi, wr, wi, tempr, tempi;
    double[] dataLocal = new double[data.length];
    System.arraycopy(data, 0, dataLocal, 0, data.length);

    nn = dataLocal.length / 2;
    n = nn << 1;
    j = 1;
    double temp;
    for(i=1; i<n; i+=2) {
      if(j > i) {
        temp = dataLocal[i-1];
        dataLocal[i-1] = dataLocal[j-1];
        dataLocal[j-1] = temp;

        temp = dataLocal[i];
        dataLocal[i] = dataLocal[j];
        dataLocal[j] = temp;
      }
      m = nn;
      while( m >= 2 && j > m) {
        j -= m;
        m >>= 1;
      }
      j += m;
    }

//  Here begins he Danielson-Lanczos section of the routine
    mmax = 2;
    while(n > mmax) {
      istep = mmax << 1;
      theta = isign * (6.28318530717959 / mmax);
      wtemp = Math.sin(0.5 * theta);
      wpr = -2.0 * wtemp * wtemp;
      wpi = Math.sin(theta);
      wr = 1.0;
      wi = 0.0;
      for(m=1; m<mmax; m+=2) {
        for(i=m; i<=n; i+=istep) {
          j = i + mmax;
          tempr = wr*dataLocal[j-1] - wi*dataLocal[j];
          tempi = wr*dataLocal[j] + wi*dataLocal[j-1];
          dataLocal[j-1] = dataLocal[i-1] - tempr;
          dataLocal[j] = dataLocal[i] - tempi;
          dataLocal[i-1] += tempr;
          dataLocal[i] += tempi;
        }
        wr = (wtemp = wr)*wpr - wi*wpi + wr;
        wi = wi*wpr + wtemp*wpi + wi;
      }
      mmax = istep;
    }
    return dataLocal;
  }
  
  public final void computeTwiddleFactors(int direction, int size) {
    double x;
    this.direction = direction;
    twiddleFactors = new int[40];
    twiddleReal = new double[size];
    twiddleImag = new double[size];
    int nn = size;
    int k = 0;
    int j = 5;
    int i4 = 0;
    twiddleFactors[19] = 1;
    while(nn>1) {
      while(nn%j==0) {
        k++;
        nn /= j;
        twiddleFactors[k-1] = j;
        twiddleFactors[k+19] = twiddleFactors[k+18]*j;
      }
      j--;
    }
    k+=i4;
    if(i4==1) {
      twiddleFactors[k-1] = 4;
      twiddleFactors[k+19] = twiddleFactors[k+18]*4;
    }
    twiddleFactors[39] = k;
    x = -MathConstants.TWOPI/size;
    if(direction<0) x = -x;
    for(int i=0; i<size; i++) {
      twiddleReal[i] = Math.cos(i*x);
      twiddleImag[i] = Math.sin(i*x);
    }
  }

  /**
   * @return  Returns the twiddleFactors.
   */
  public int[] getTwiddleFactors() {
    return twiddleFactors;
  }

  /**
   * @return  Returns the twiddleImag.
   */
  public double[] getTwiddleImag() {
    return twiddleImag;
  }

  /**
   * @return  Returns the twiddleReal.
   */
  public double[] getTwiddleReal() {
    return twiddleReal;
  }
  
  protected final void fft1d(double[] yRe, double[] yIm) {
    int n = yRe.length;

    int l;
    int k = twiddleFactors[39];
    
    double[] wyRe = new double[yRe.length];
    double[] wyIm = new double[yRe.length];
    System.arraycopy(yRe, 0, wyRe, 0, n);
    System.arraycopy(yIm, 0, wyIm, 0, n);
    
    int j=1;
    for(int i=0; i<n-1; i++) {
      yRe[i] = wyRe[j-1];
      yIm[i] = wyIm[j-1];
      l = k+18;
      while(twiddleFactors[l] + j > twiddleFactors[l+1]) {
        j += (1-twiddleFactors[l-19])*twiddleFactors[l];
        l--;
      }
      j += twiddleFactors[l];
    }

    boolean lneg = (twiddleImag[n/4]>0);
    int la, la2, la3, la4, le, lx, lxj, i, i1, i2, i3, i4;
    double t1Re,t1Im,t2Re,t2Im,t3Re,t3Im,t4Re,t4Im,w1Re,w1Im,w2Re,w2Im,w3Re,w3Im,w4Re,w4Im;
    for(l = k; l>=1; l--) {
      la = n/twiddleFactors[19+l];
      la2 = la + la;
      la3 = la2 + la;
      la4 = la3 + la;
      le = la*twiddleFactors[l-1];
      lx = n/le;
      lxj = 0;

      if(twiddleFactors[l-1]==2) {
        for(i=0; i<n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];
          
          yRe[i+la] = yRe[i] - t1Re;
          yIm[i+la] = yIm[i] - t1Im;

          yRe[i] += t1Re;
          yIm[i] += t1Im;
        }

        for(j=0; j<(la-1); j++) {
          lxj += lx;
          w1Re = twiddleReal[lxj];
          w1Im = twiddleImag[lxj];

          for(i=j+1; i<=n; i+=le) {
            t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
            t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

            yRe[i+la] = yRe[i] - t1Re;
            yIm[i+la] = yIm[i] - t1Im;

            yRe[i] += t1Re;
            yIm[i] += t1Im;
          }
        }
      } else if(twiddleFactors[l-1]==3) {
        int a1 = n/3; 
        int a2 = (n+n)/3;
        w1Re = twiddleReal[a1];
        w1Im = twiddleImag[a1];

        w2Re = twiddleReal[a2];
        w2Im = twiddleImag[a2];

        for(i=0; i<n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];

          t2Re = yRe[i+la2];
          t2Im = yIm[i+la2];

          yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im);
          yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im);

          yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w1Re*t2Re-w1Im*t2Im);
          yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w1Im*t2Re+w1Re*t2Im);

          yRe[i] += t1Re + t2Re;
          yIm[i] += t1Im + t2Im;
        }

        for(j=0; j<(la-1); j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          for(i=j+1; i<=n; i+=le) {
            t1Re = twiddleReal[i1]*yRe[i+la] - twiddleImag[i1]*yIm[i+la];
            t1Im = twiddleImag[i1]*yRe[i+la] + twiddleReal[i1]*yIm[i+la];

            t2Re = twiddleReal[i2]*yRe[i+la2] - twiddleImag[i2]*yIm[i+la2];
            t2Im = twiddleImag[i2]*yRe[i+la2] + twiddleReal[i2]*yIm[i+la2];

            yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im);
            yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im);

            yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w1Re*t2Re-w1Im*t2Im);
            yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w1Im*t2Re+w1Re*t2Im);

            yRe[i] += t1Re + t2Re;
            yIm[i] += t1Im + t2Im;
          }
        }
      } else if(twiddleFactors[l-1]==4) {
        if(lneg) { //inverse
          for(i=0; i<n; i+=le) {
            t1Re = yRe[i+la]; 
            t1Im = yIm[i+la]; 

            t2Re = yRe[i+la2]; 
            t2Im = yIm[i+la2]; 

            t3Re = yRe[i+la3]; 
            t3Im = yIm[i+la3]; 

            t4Re = t3Im - t1Im; 
            t4Im = t1Re - t3Re;

            t3Re += t1Re;
            t3Im += t1Im;

            t1Re = yRe[i] + t2Re;
            t1Im = yIm[i] + t2Im;

            t2Re = yRe[i] - t2Re;
            t2Im = yIm[i] - t2Im;

            yRe[i+la] = t2Re + t4Re;
            yIm[i+la] = t2Im + t4Im;

            yRe[i+la2] = t1Re - t3Re;
            yIm[i+la2] = t1Im - t3Im;

            yRe[i+la3] = t2Re - t4Re;
            yIm[i+la3] = t2Im - t4Im;

            yRe[i] = t1Re + t3Re;
            yIm[i] = t1Im + t3Im;
          }
        } else {
          for(i=0; i<n; i+=le) {
            t1Re = yRe[i+la]; 
            t1Im = yIm[i+la]; 

            t2Re = yRe[i+la2]; 
            t2Im = yIm[i+la2]; 

            t3Re = yRe[i+la3]; 
            t3Im = yIm[i+la3]; 

            t4Re = t3Im - t1Im; 
            t4Im = t1Re - t3Re;

            t3Re += t1Re;
            t3Im += t1Im;

            t1Re = yRe[i] + t2Re;
            t1Im = yIm[i] + t2Im;

            t2Re = yRe[i] - t2Re;
            t2Im = yIm[i] - t2Im;

            yRe[i+la] = t2Re - t4Re;
            yIm[i+la] = t2Im - t4Im;

            yRe[i+la2] = t1Re - t3Re;
            yIm[i+la2] = t1Im - t3Im;

            yRe[i+la3] = t2Re + t4Re;
            yIm[i+la3] = t2Im + t4Im;

            yRe[i] = t1Re + t3Re;
            yIm[i] = t1Im + t3Im;
          }
        } // endif for direction
        for(j=0; j<la-1; j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          i3 = i2 + lxj;

          w1Re = twiddleReal[i1];
          w1Im = twiddleImag[i1];

          w2Re = twiddleReal[i2];
          w2Im = twiddleImag[i2];

          w3Re = twiddleReal[i3];
          w3Im = twiddleImag[i3];

          if(lneg) {
            for(i=j+1; i<=n; i+=le) {
              t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
              t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

              t2Re = w2Re*yRe[i+la2] - w2Im*yIm[i+la2];
              t2Im = w2Im*yRe[i+la2] + w2Re*yIm[i+la2];

              t3Re = w3Re*yRe[i+la3] - w3Im*yIm[i+la3];
              t3Im = w3Im*yRe[i+la3] + w3Re*yIm[i+la3];

              t4Re = t3Im - t1Im;
              t4Im = t1Re - t3Re;

              t3Re += t1Re;
              t3Im += t1Im;

              t1Re = yRe[i] + t2Re;
              t1Im = yIm[i] + t2Im;

              t2Re = yRe[i] - t2Re;
              t2Im = yIm[i] - t2Im;

              yRe[i+la] = t2Re + t4Re;
              yIm[i+la] = t2Im + t4Im;

              yRe[i+la2] = t1Re - t3Re;
              yIm[i+la2] = t1Im - t3Im;

              yRe[i+la3] = t2Re - t4Re;
              yIm[i+la3] = t2Im - t4Im;

              yRe[i] = t1Re + t3Re;
              yIm[i] = t1Im + t3Im;
            }
          } else {
            for(i=j+1; i<=n; i+=le) {
              t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
              t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

              t2Re = w2Re*yRe[i+la2] - w2Im*yIm[i+la2];
              t2Im = w2Im*yRe[i+la2] + w2Re*yIm[i+la2];

              t3Re = w3Re*yRe[i+la3] - w3Im*yIm[i+la3];
              t3Im = w3Im*yRe[i+la3] + w3Re*yIm[i+la3];

              t4Re = t3Im - t1Im;
              t4Im = t1Re - t3Re;

              t3Re += t1Re;
              t3Im += t1Im;

              t1Re = yRe[i] + t2Re;
              t1Im = yIm[i] + t2Im;

              t2Re = yRe[i] - t2Re;
              t2Im = yIm[i] - t2Im;

              yRe[i+la] = t2Re - t4Re;
              yIm[i+la] = t2Im - t4Im;

              yRe[i+la2] = t1Re - t3Re;
              yIm[i+la2] = t1Im - t3Im;

              yRe[i+la3] = t2Re + t4Re;
              yIm[i+la3] = t2Im + t4Im;

              yRe[i] = t1Re + t3Re;
              yIm[i] = t1Im + t3Im;
            }
          } //endif for direction
        } //end for loop
      } else if(twiddleFactors[l-1]==5) {
        w1Re = twiddleReal[n/5];
        w1Im = twiddleImag[n/5];

        w2Re = twiddleReal[(n+n)/5];
        w2Im = twiddleImag[(n+n)/5];

        w3Re = twiddleReal[(n+n+n)/5];
        w3Im = twiddleImag[(n+n+n)/5];

        w4Re = twiddleReal[(n+n+n+n)/5];
        w4Im = twiddleImag[(n+n+n+n)/5];

        for(i=0; i<n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];

          t2Re = yRe[i+la2];
          t2Im = yIm[i+la2];

          t3Re = yRe[i+la3];
          t3Im = yIm[i+la3];

          t4Re = yRe[i+la4];
          t4Im = yIm[i+la4];

          yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im) + (w3Re*t3Re-w3Im*t3Im) + (w4Re*t4Re-w4Im*t4Im); 
          yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im) + (w3Im*t3Re+w3Re*t3Im) + (w4Im*t4Re+w4Re*t4Im);  

          yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w4Re*t2Re-w4Im*t2Im) + (w1Re*t3Re-w1Im*t3Im) + (w3Re*t4Re-w3Im*t4Im); 
          yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w4Im*t2Re+w4Re*t2Im) + (w1Im*t3Re+w1Re*t3Im) + (w3Im*t4Re+w3Re*t4Im);  

          yRe[i+la3] = yRe[i] + (w3Re*t1Re-w3Im*t1Im) + (w1Re*t2Re-w1Im*t2Im) + (w4Re*t3Re-w4Im*t3Im) + (w2Re*t4Re-w2Im*t4Im); 
          yIm[i+la3] = yIm[i] + (w3Im*t1Re+w3Re*t1Im) + (w1Im*t2Re+w1Re*t2Im) + (w4Im*t3Re+w4Re*t3Im) + (w2Im*t4Re+w2Re*t4Im);  

          yRe[i+la4] = yRe[i] + (w4Re*t1Re-w4Im*t1Im) + (w3Re*t2Re-w3Im*t2Im) + (w2Re*t3Re-w2Im*t3Im) + (w1Re*t4Re-w1Im*t4Im); 
          yIm[i+la4] = yIm[i] + (w4Im*t1Re+w4Re*t1Im) + (w3Im*t2Re+w3Re*t2Im) + (w2Im*t3Re+w2Re*t3Im) + (w1Im*t4Re+w1Re*t4Im);  

          yRe[i] += t1Re + t2Re + t3Re + t4Re;
          yIm[i] += t1Im + t2Im + t3Im + t4Im;
        }
        for(j=0; j<la-1; j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          i3 = i2 + lxj;
          i4 = i3 + lxj;
          for(i=j+1; i<=n; i+=le) {
            t1Re = twiddleReal[i1]*yRe[i+la] - twiddleImag[i1]*yIm[i+la];
            t1Im = twiddleReal[i1]*yIm[i+la] + twiddleImag[i1]*yRe[i+la];

            t2Re = twiddleReal[i2]*yRe[i+la2] - twiddleImag[i2]*yIm[i+la2];
            t2Im = twiddleReal[i2]*yIm[i+la2] + twiddleImag[i2]*yRe[i+la2];

            t3Re = twiddleReal[i3]*yRe[i+la3] - twiddleImag[i3]*yIm[i+la3];
            t3Im = twiddleReal[i3]*yIm[i+la3] + twiddleImag[i3]*yRe[i+la3];

            t4Re = twiddleReal[i4]*yRe[i+la4] - twiddleImag[i4]*yIm[i+la4];
            t4Im = twiddleReal[i4]*yIm[i+la4] + twiddleImag[i4]*yRe[i+la4];

            yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im) + (w3Re*t3Re-w3Im*t3Im) + (w4Re*t4Re-w4Im*t4Im); 
            yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im) + (w3Im*t3Re+w3Re*t3Im) + (w4Im*t4Re+w4Re*t4Im);  

            yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w4Re*t2Re-w4Im*t2Im) + (w1Re*t3Re-w1Im*t3Im) + (w3Re*t4Re-w3Im*t4Im); 
            yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w4Im*t2Re+w4Re*t2Im) + (w1Im*t3Re+w1Re*t3Im) + (w3Im*t4Re+w3Re*t4Im);  

            yRe[i+la3] = yRe[i] + (w3Re*t1Re-w3Im*t1Im) + (w1Re*t2Re-w1Im*t2Im) + (w4Re*t3Re-w4Im*t3Im) + (w2Re*t4Re-w2Im*t4Im); 
            yIm[i+la3] = yIm[i] + (w3Im*t1Re+w3Re*t1Im) + (w1Im*t2Re+w1Re*t2Im) + (w4Im*t3Re+w4Re*t3Im) + (w2Im*t4Re+w2Re*t4Im);  

            yRe[i+la4] = yRe[i] + (w4Re*t1Re-w4Im*t1Im) + (w3Re*t2Re-w3Im*t2Im) + (w2Re*t3Re-w2Im*t3Im) + (w1Re*t4Re-w1Im*t4Im); 
            yIm[i+la4] = yIm[i] + (w4Im*t1Re+w4Re*t1Im) + (w3Im*t2Re+w3Re*t2Im) + (w2Im*t3Re+w2Re*t3Im) + (w1Im*t4Re+w1Re*t4Im);  

            yRe[i] += t1Re + t2Re + t3Re + t4Re;
            yIm[i] += t1Im + t2Im + t3Im + t4Im;
          }
        }
      }
    }
  }
}

/*  
  protected void fft1m(double[] yRe, double[] yIm) {
    double[] wyRe = new double[yRe.length];
    double[] wyIm = new double[yRe.length];
    int n = yRe.length-1;

    int l;
    int k = twiddleFactors[39];
    
    System.arraycopy(yRe, 0, wyRe, 0, n);
    System.arraycopy(yIm, 0, wyIm, 0, n);
    
    int j=1;
    for(int i=1; i<=n-1; i++) {
      yRe[i] = wyRe[j];
      yIm[i] = wyIm[j];
      l = k+18;
      while(twiddleFactors[l] + j > twiddleFactors[l+1]) {
        j += (1-twiddleFactors[l-19])*twiddleFactors[l];
        l--;
      }

      j += twiddleFactors[l];
    }

    boolean lneg = (twiddleImag[n/4]>0);
    int la, la2, la3, la4, le, lx, lxj, i, i1, i2, i3, i4;
    double t1Re,t1Im,t2Re,t2Im,t3Re,t3Im,t4Re,t4Im,w1Re,w1Im,w2Re,w2Im,w3Re,w3Im,w4Re,w4Im;
    for(l = k; l>=1; l--) {
      la = n/twiddleFactors[19+l];
      la2 = la + la;
      la3 = la2 + la;
      la4 = la3 + la;
      le = la*twiddleFactors[l-1];
      lx = n/le;
      lxj = 0;

      if(twiddleFactors[l-1]==2) {
        for(i=1; i<=n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];
          
          yRe[i+la] = yRe[i] - t1Re;
          yIm[i+la] = yIm[i] - t1Im;

          yRe[i] += t1Re;
          yIm[i] += t1Im;
        }

        for(j=1; j<=(la-1); j++) {
          lxj += lx;
          w1Re = twiddleReal[lxj];
          w1Im = twiddleImag[lxj];

          for(i=j+1; i<=n; i+=le) {
            t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
            t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

            yRe[i+la] = yRe[i] - t1Re;
            yIm[i+la] = yIm[i] - t1Im;

            yRe[i] += t1Re;
            yIm[i] += t1Im;
          }
        }
      } else if(twiddleFactors[l-1]==3) {
        int a1 = n/3; 
        int a2 = (n+n)/3;
        w1Re = twiddleReal[a1];
        w1Im = twiddleImag[a1];

        w2Re = twiddleReal[a2];
        w2Im = twiddleImag[a2];

        for(i=1; i<=n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];

          t2Re = yRe[i+la2];
          t2Im = yIm[i+la2];

          yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im);
          yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im);

          yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w1Re*t2Re-w1Im*t2Im);
          yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w1Im*t2Re+w1Re*t2Im);

          yRe[i] += t1Re + t2Re;
          yIm[i] += t1Im + t2Im;
        }

        for(j=1; j<=(la-1); j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          for(i=(j+1); i<=n; i+=le) {
            t1Re = twiddleReal[i1]*yRe[i+la] - twiddleImag[i1]*yIm[i+la];
            t1Im = twiddleImag[i1]*yRe[i+la] + twiddleReal[i1]*yIm[i+la];

            t2Re = twiddleReal[i2]*yRe[i+la2] - twiddleImag[i2]*yIm[i+la2];
            t2Im = twiddleImag[i2]*yRe[i+la2] + twiddleReal[i2]*yIm[i+la2];

            yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im);
            yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im);

            yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w1Re*t2Re-w1Im*t2Im);
            yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w1Im*t2Re+w1Re*t2Im);

            yRe[i] += t1Re + t2Re;
            yIm[i] += t1Im + t2Im;
          }
        }
      } else if(twiddleFactors[l-1]==4) {
        if(lneg) { //inverse
          for(i=1; i<=n; i+=le) {
            t1Re = yRe[i+la]; 
            t1Im = yIm[i+la]; 

            t2Re = yRe[i+la2]; 
            t2Im = yIm[i+la2]; 

            t3Re = yRe[i+la3]; 
            t3Im = yIm[i+la3]; 

            t4Re = t3Im - t1Im; 
            t4Im = t1Re - t3Re;

            t3Re += t1Re;
            t3Im += t1Im;

            t1Re = yRe[i] + t2Re;
            t1Im = yIm[i] + t2Im;

            t2Re = yRe[i] - t2Re;
            t2Im = yIm[i] - t2Im;

            yRe[i+la] = t2Re + t4Re;
            yIm[i+la] = t2Im + t4Im;

            yRe[i+la2] = t1Re - t3Re;
            yIm[i+la2] = t1Im - t3Im;

            yRe[i+la3] = t2Re - t4Re;
            yIm[i+la3] = t2Im - t4Im;

            yRe[i] = t1Re + t3Re;
            yIm[i] = t1Im + t3Im;
          }
        } else {
          for(i=1;i<=n;i+=le) {
            t1Re = yRe[i+la]; 
            t1Im = yIm[i+la]; 

            t2Re = yRe[i+la2]; 
            t2Im = yIm[i+la2]; 

            t3Re = yRe[i+la3]; 
            t3Im = yIm[i+la3]; 

            t4Re = t3Im - t1Im; 
            t4Im = t1Re - t3Re;

            t3Re += t1Re;
            t3Im += t1Im;

            t1Re = yRe[i] + t2Re;
            t1Im = yIm[i] + t2Im;

            t2Re = yRe[i] - t2Re;
            t2Im = yIm[i] - t2Im;

            yRe[i+la] = t2Re - t4Re;
            yIm[i+la] = t2Im - t4Im;

            yRe[i+la2] = t1Re - t3Re;
            yIm[i+la2] = t1Im - t3Im;

            yRe[i+la3] = t2Re + t4Re;
            yIm[i+la3] = t2Im + t4Im;

            yRe[i] = t1Re + t3Re;
            yIm[i] = t1Im + t3Im;
          }
        } // endif for direction
        for(j=1; j<=la-1; j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          i3 = i2 + lxj;

          w1Re = twiddleReal[i1];
          w1Im = twiddleImag[i1];

          w2Re = twiddleReal[i2];
          w2Im = twiddleImag[i2];

          w3Re = twiddleReal[i3];
          w3Im = twiddleImag[i3];

          if(lneg) {
            for(i=j+1; i<=n; i+=le) {
              t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
              t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

              t2Re = w2Re*yRe[i+la2] - w2Im*yIm[i+la2];
              t2Im = w2Im*yRe[i+la2] + w2Re*yIm[i+la2];

              t3Re = w3Re*yRe[i+la3] - w3Im*yIm[i+la3];
              t3Im = w3Im*yRe[i+la3] + w3Re*yIm[i+la3];

              t4Re = t3Im - t1Im;
              t4Im = t1Re - t3Re;

              t3Re += t1Re;
              t3Im += t1Im;

              t1Re = yRe[i] + t2Re;
              t1Im = yIm[i] + t2Im;

              t2Re = yRe[i] - t2Re;
              t2Im = yIm[i] - t2Im;

              yRe[i+la] = t2Re + t4Re;
              yIm[i+la] = t2Im + t4Im;

              yRe[i+la2] = t1Re - t3Re;
              yIm[i+la2] = t1Im - t3Im;

              yRe[i+la3] = t2Re - t4Re;
              yIm[i+la3] = t2Im - t4Im;

              yRe[i] = t1Re + t3Re;
              yIm[i] = t1Im + t3Im;
            }
          } else {
            for(i=j+1; i<=n; i+=le) {
              t1Re = w1Re*yRe[i+la] - w1Im*yIm[i+la];
              t1Im = w1Im*yRe[i+la] + w1Re*yIm[i+la];

              t2Re = w2Re*yRe[i+la2] - w2Im*yIm[i+la2];
              t2Im = w2Im*yRe[i+la2] + w2Re*yIm[i+la2];

              t3Re = w3Re*yRe[i+la3] - w3Im*yIm[i+la3];
              t3Im = w3Im*yRe[i+la3] + w3Re*yIm[i+la3];

              t4Re = t3Im - t1Im;
              t4Im = t1Re - t3Re;

              t3Re += t1Re;
              t3Im += t1Im;

              t1Re = yRe[i] + t2Re;
              t1Im = yIm[i] + t2Im;

              t2Re = yRe[i] - t2Re;
              t2Im = yIm[i] - t2Im;

              yRe[i+la] = t2Re - t4Re;
              yIm[i+la] = t2Im - t4Im;

              yRe[i+la2] = t1Re - t3Re;
              yIm[i+la2] = t1Im - t3Im;

              yRe[i+la3] = t2Re + t4Re;
              yIm[i+la3] = t2Im + t4Im;

              yRe[i] = t1Re + t3Re;
              yIm[i] = t1Im + t3Im;
            }
          } //endif for direction
        } //end for loop
      } else if(twiddleFactors[l-1]==5) {
        w1Re = twiddleReal[n/5];
        w1Im = twiddleImag[n/5];

        w2Re = twiddleReal[(n+n)/5];
        w2Im = twiddleImag[(n+n)/5];

        w3Re = twiddleReal[(n+n+n)/5];
        w3Im = twiddleImag[(n+n+n)/5];

        w4Re = twiddleReal[(n+n+n+n)/5];
        w4Im = twiddleImag[(n+n+n+n)/5];

        for(i=1; i<=n; i+=le) {
          t1Re = yRe[i+la];
          t1Im = yIm[i+la];

          t2Re = yRe[i+la2];
          t2Im = yIm[i+la2];

          t3Re = yRe[i+la3];
          t3Im = yIm[i+la3];

          t4Re = yRe[i+la4];
          t4Im = yIm[i+la4];

          yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im) + (w3Re*t3Re-w3Im*t3Im) + (w4Re*t4Re-w4Im*t4Im); 
          yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im) + (w3Im*t3Re+w3Re*t3Im) + (w4Im*t4Re+w4Re*t4Im);  

          yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w4Re*t2Re-w4Im*t2Im) + (w1Re*t3Re-w1Im*t3Im) + (w3Re*t4Re-w3Im*t4Im); 
          yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w4Im*t2Re+w4Re*t2Im) + (w1Im*t3Re+w1Re*t3Im) + (w3Im*t4Re+w3Re*t4Im);  

          yRe[i+la3] = yRe[i] + (w3Re*t1Re-w3Im*t1Im) + (w1Re*t2Re-w1Im*t2Im) + (w4Re*t3Re-w4Im*t3Im) + (w2Re*t4Re-w2Im*t4Im); 
          yIm[i+la3] = yIm[i] + (w3Im*t1Re+w3Re*t1Im) + (w1Im*t2Re+w1Re*t2Im) + (w4Im*t3Re+w4Re*t3Im) + (w2Im*t4Re+w2Re*t4Im);  

          yRe[i+la4] = yRe[i] + (w4Re*t1Re-w4Im*t1Im) + (w3Re*t2Re-w3Im*t2Im) + (w2Re*t3Re-w2Im*t3Im) + (w1Re*t4Re-w1Im*t4Im); 
          yIm[i+la4] = yIm[i] + (w4Im*t1Re+w4Re*t1Im) + (w3Im*t2Re+w3Re*t2Im) + (w2Im*t3Re+w2Re*t3Im) + (w1Im*t4Re+w1Re*t4Im);  

          yRe[i] += t1Re + t2Re + t3Re + t4Re;
          yIm[i] += t1Im + t2Im + t3Im + t4Im;
        }
        for(j=1; j<=la-1; j++) {
          lxj += lx;
          i1 = lxj;
          i2 = i1 + lxj;
          i3 = i2 + lxj;
          i4 = i3 + lxj;
          for(i=j+1; i<=n; i+=le) {
            t1Re = twiddleReal[i1]*yRe[i+la] - twiddleImag[i1]*yIm[i+la];
            t1Im = twiddleReal[i1]*yIm[i+la] + twiddleImag[i1]*yRe[i+la];

            t2Re = twiddleReal[i2]*yRe[i+la2] - twiddleImag[i2]*yIm[i+la2];
            t2Im = twiddleReal[i2]*yIm[i+la2] + twiddleImag[i2]*yRe[i+la2];

            t3Re = twiddleReal[i3]*yRe[i+la3] - twiddleImag[i3]*yIm[i+la3];
            t3Im = twiddleReal[i3]*yIm[i+la3] + twiddleImag[i3]*yRe[i+la3];

            t4Re = twiddleReal[i4]*yRe[i+la4] - twiddleImag[i4]*yIm[i+la4];
            t4Im = twiddleReal[i4]*yIm[i+la4] + twiddleImag[i4]*yRe[i+la4];

            yRe[i+la] = yRe[i] + (w1Re*t1Re-w1Im*t1Im) + (w2Re*t2Re-w2Im*t2Im) + (w3Re*t3Re-w3Im*t3Im) + (w4Re*t4Re-w4Im*t4Im); 
            yIm[i+la] = yIm[i] + (w1Im*t1Re+w1Re*t1Im) + (w2Im*t2Re+w2Re*t2Im) + (w3Im*t3Re+w3Re*t3Im) + (w4Im*t4Re+w4Re*t4Im);  

            yRe[i+la2] = yRe[i] + (w2Re*t1Re-w2Im*t1Im) + (w4Re*t2Re-w4Im*t2Im) + (w1Re*t3Re-w1Im*t3Im) + (w3Re*t4Re-w3Im*t4Im); 
            yIm[i+la2] = yIm[i] + (w2Im*t1Re+w2Re*t1Im) + (w4Im*t2Re+w4Re*t2Im) + (w1Im*t3Re+w1Re*t3Im) + (w3Im*t4Re+w3Re*t4Im);  

            yRe[i+la3] = yRe[i] + (w3Re*t1Re-w3Im*t1Im) + (w1Re*t2Re-w1Im*t2Im) + (w4Re*t3Re-w4Im*t3Im) + (w2Re*t4Re-w2Im*t4Im); 
            yIm[i+la3] = yIm[i] + (w3Im*t1Re+w3Re*t1Im) + (w1Im*t2Re+w1Re*t2Im) + (w4Im*t3Re+w4Re*t3Im) + (w2Im*t4Re+w2Re*t4Im);  

            yRe[i+la4] = yRe[i] + (w4Re*t1Re-w4Im*t1Im) + (w3Re*t2Re-w3Im*t2Im) + (w2Re*t3Re-w2Im*t3Im) + (w1Re*t4Re-w1Im*t4Im); 
            yIm[i+la4] = yIm[i] + (w4Im*t1Re+w4Re*t1Im) + (w3Im*t2Re+w3Re*t2Im) + (w2Im*t3Re+w2Re*t3Im) + (w1Im*t4Re+w1Re*t4Im);  

            yRe[i] += t1Re + t2Re + t3Re + t4Re;
            yIm[i] += t1Im + t2Im + t3Im + t4Im;
          }
        }
      }
    }
  }
  
  public void mkwm(int direction, int size) {
    double x;
    this.direction = direction;
    twiddleFactors = new int[41];
    twiddleReal = new double[size+1];
    twiddleImag = new double[size+1];
    int nn = size;
    int k = 0;
    int j = 5;
    int i4 = 0;
    twiddleFactors[20] = 1;
    while(nn>1) {
      while(nn%j==0) {
        k++;
        nn /= j;
        twiddleFactors[k] = j;
        twiddleFactors[k+20] = twiddleFactors[k+19]*j;
      }
      j--;
    }
    k+=i4;
    if(i4==1) {
      twiddleFactors[k] = 4;
      twiddleFactors[k+20] = twiddleFactors[k+19]*4;
    }
    twiddleFactors[40] = k;
    x = -MathConstants.TWOPI/size;
    if(direction<0) x = -x;
    for(int i=0; i<size; i++) {
      twiddleReal[i+1] = Math.cos(i*x);
      twiddleImag[i+1] = Math.sin(i*x);
    }
  }
  
 */
