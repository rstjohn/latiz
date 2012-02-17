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
public class FFT2D {

  private int direction = 1;

  private double[][] realPart;
  private double[][] imagPart;

  public static final int FORWARD = 1;
  public static final int INVERSE = -1;
  public static final int UNSCALED_INVERSE = -2;
  
  public static final int LINK_ALGORITHM = 1;

  public static final int NUMERICAL_RECIPES_ALGORITHM = 2;

  private int algorithm = LINK_ALGORITHM;

  public FFT2D() {
    algorithm = LINK_ALGORITHM;
    direction = FORWARD;
  }
  
  public FFT2D(int dir) {
    algorithm = LINK_ALGORITHM;
    direction = dir;
  }

  public FFT2D(int algorithm, int dir) {
    this.algorithm = algorithm;
    direction = dir;
  }
  
  public final void compute(double[][] real, double[][] imag) {
    compute(real, imag, direction);
  }

  public final void compute(double[][] real, double[][] imag, int direction) {
    this.direction = direction;

    if(algorithm == LINK_ALGORITHM) {
      fft2d(real, imag, direction);
      
    } else if(algorithm==NUMERICAL_RECIPES_ALGORITHM) {
      int m = real.length;
      int n = real[0].length;

      double[] data = new double[2*m*n];

      int[] dims = new int[]{m,n};
      int i, j, k = 0;
      for(j=0; j<n; j++) {
        for(i=0; i<m; i++) {
          data[k] = real[i][j];
          data[k+1] = imag[i][j];
          k+=2;
        }
      }

      data = fourn(data, dims, direction);

      realPart = new double[m][n];
      imagPart = new double[m][n];
      k = 0;
      for(j=0; j<n; j++) {
        for(i=0; i<n; i++) {
          realPart[i][j] = data[k];
          imagPart[i][j] = data[k+1];
          k+=2;
        }
      }
    }
  }

  protected final void fft2d(double[][] wRe, double[][] wIm, int direction) {
    this.direction = direction;
    int i,j,k;
    int nx = wRe.length;
    int ny = wRe[0].length;
    
    double[] wxRe = new double[nx];
    double[] wxIm = new double[nx];
    if(direction == -1) {
      double nxny = nx*ny;
      double x = 1.0/nxny;                 
      for(j = 0; j<ny; j++) {
        for(i = 0; i<nx; i++) {
          wRe[i][j] *= x;
          wIm[i][j] *= x;
        }
      }
    }
    int nhx = nx/2;
    int nhy = ny/2;

    FFT fft1d = new FFT(direction);
    fft1d.computeTwiddleFactors(direction, nx);

    for(k=0; k<ny; k++) {
      for(i=0; i<nhx; i++) {
        wxRe[i+nhx]=wRe[i][k];
        wxIm[i+nhx]=wIm[i][k];

        wxRe[i]=wRe[i+nhx][k];
        wxIm[i]=wIm[i+nhx][k];
      }
      fft1d.fft1d(wxRe, wxIm);

      for(i=0; i<nhx; i++) {
        wRe[i+nhx][k] = wxRe[i];
        wIm[i+nhx][k] = wxIm[i];

        wRe[i][k] = wxRe[i+nhx];
        wIm[i][k] = wxIm[i+nhx];
      }
    }

    if(nx!=ny) {
      wxRe = new double[ny]; 
      wxIm = new double[ny];
      fft1d.computeTwiddleFactors(direction, ny);
    }
    for(k=0; k<nx; k++) {
      for(i=0; i<nhy; i++) {
        wxRe[i+nhy]=wRe[k][i];
        wxIm[i+nhy]=wIm[k][i];

        wxRe[i]=wRe[k][i+nhy];
        wxIm[i]=wIm[k][i+nhy];
      }
      fft1d.fft1d(wxRe, wxIm);

      for(i=0; i<nhy; i++) {
        wRe[k][i+nhy] = wxRe[i];
        wIm[k][i+nhy] = wxIm[i];

        wRe[k][i] = wxRe[i+nhy];
        wIm[k][i] = wxIm[i+nhy];
      }
    }
  }

  protected double[] fourn(double[] data, int[] nn, int isign) {
    int ip1, ip2, ip3, i2rev, i3rev, i1, i2, i3, ifp1, ifp2;
    int ibit, k1, k2, n, nrem;
    double theta, wtemp, tempr, tempi, wpr, wpi, wr, wi;

    int idim;
    int ndim = nn.length;
    int ntot = data.length/2;

    int nprev = 1;
    for(idim=ndim-1; idim>=0; idim--) {
      n = nn[idim];
      nrem = ntot/(n*nprev);
      ip1 = nprev << 1;
      ip2 = ip1 * n;
      ip3 = ip2 * nrem;
      i2rev = 0;
      for(i2 = 0; i2 < ip2; i2+=ip1) {
        if(i2 < i2rev) {
          for(i1=i2; i1<i2+ip1-1; i1+=2) {
            for(i3=i1; i3<ip3; i3+=ip2) {
              i3rev = i2rev + i3 - i2;

              tempr = data[i3];
              data[i3] = data[i3rev];
              data[i3rev] = tempr;

              tempi = data[i3+1];
              data[i3+1] = data[i3rev+1];
              data[i3rev+1] = tempi;
            }
          }
        }
        ibit = ip2 >> 1;
            while(ibit >= ip1 && i2rev+1 > ibit) {
              i2rev -= ibit;
              ibit >>= 1;
            }
            i2rev += ibit;
      }
      ifp1 = ip1;
      while(ifp1 < ip2) {
        ifp2 = ifp1 << 1;
        theta = isign * MathConstants.TWOPI / (ifp2/ip1);
        wtemp = Math.sin(0.5*theta);
        wpr = -2.0 * wtemp * wtemp;
        wpi = Math.sin(theta);
        wr = 1.0;
        wi = 0.0;
        for(i3 = 0; i3 < ifp1; i3+=ip1) {
          for(i1 = i3; i1 < i3+ip1-1; i1+=2) {
            for(i2 = i1; i2 < ip3; i2+=ifp2) {
              k1 = i2;
              k2 = k1 + ifp1;
              tempr = wr*data[k2] - wi*data[k2+1];
              tempi = wr*data[k2+1] + wi*data[k2];
              data[k2] = data[k1] - tempr;
              data[k2+1] = data[k1+1] - tempi;
              data[k1] += tempr;
              data[k1+1] += tempi;
            }
          }
          wr = (wtemp = wr)*wpr - wi*wpi + wr;
          wi = wi*wpr + wtemp*wpi + wi;
        }
        ifp1 = ifp2;
      }
      nprev *= n;
    }
    return data;
  }

  /**
   * @param algorithm  The algorithm to set.
   * @uml.property  name="algorithm"
   */
  public void setAlgorithm(int algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * @param direction  The direction to set.
   * @uml.property  name="direction"
   */
  public void setDirection(int dir) {
    direction = dir;
  }

  /**
   * @return  Returns the direction.
   * @uml.property  name="direction"
   */
  public int getDirection() {
    return direction;
  }
  
  /**
   * @return  Returns the algorithm.
   * @uml.property  name="algorithm"
   */
  public int getAlgorithm() {
    return algorithm;
  }
}
