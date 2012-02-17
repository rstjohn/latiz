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
package com.AandR.beans.plotting.imagePlotPanel.colormap;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class MatlabColoMap extends AbstractColorMap {

  private int[][] cmap = new int[256][3];

  public MatlabColoMap() {
    cmap[0]   = new int[] {0,   0,   0 };
    cmap[1]   = new int[] {7,   0,   0 };
    cmap[2]   = new int[] {14,  0,   0 };
    cmap[3]   = new int[] {21,  0,   0 };
    cmap[4]   = new int[] {28,  0,   0 };
    cmap[5]   = new int[] {35,  0,   0 };
    cmap[6]   = new int[] {43,  0,   0 };
    cmap[7]   = new int[] {50,  0,   0 };
    cmap[8]   = new int[] {57,  0,   0 };
    cmap[9]   = new int[] {64,  0,   0 };
    cmap[10]  = new int[] {71,  0,   0 };
    cmap[11]  = new int[] {78,  0,   0 };
    cmap[12]  = new int[] {85,  0,   0 };
    cmap[13]  = new int[] {92,  0,   0 };
    cmap[14]  = new int[] {99,  0,   0 };
    cmap[15]  = new int[] {106, 0,   0 };
    cmap[16]  = new int[] {113, 0,   0 };
    cmap[17]  = new int[] {120, 0,   0 };
    cmap[18]  = new int[] {128, 0,   0 };
    cmap[19]  = new int[] {135, 0,   0 };
    cmap[20]  = new int[] {142, 0,   0 };
    cmap[21]  = new int[] {149, 0,   0 };
    cmap[22]  = new int[] {156, 0,   0 };
    cmap[23]  = new int[] {163, 0,   0 };
    cmap[24]  = new int[] {170, 0,   0 };
    cmap[25]  = new int[] {177, 0,   0 };
    cmap[26]  = new int[] {184, 0,   0 };
    cmap[27]  = new int[] {191, 0,   0 };
    cmap[28]  = new int[] {198, 0,   0 };
    cmap[29]  = new int[] {205, 0,   0 };
    cmap[30]  = new int[] {212, 0,   0 };
    cmap[31]  = new int[] {220, 0,   0 };
    cmap[32]  = new int[] {227, 0,   0 };
    cmap[33]  = new int[] {234, 0,   0 };
    cmap[34]  = new int[] {241, 0,   0 };
    cmap[35]  = new int[] {248, 0,   0 };
    cmap[36]  = new int[] {255, 0,   0 };
    cmap[37]  = new int[] {255, 7,   0 };
    cmap[38]  = new int[] {255, 14,  0 };
    cmap[39]  = new int[] {255, 21,  0 };
    cmap[40]  = new int[] {255, 28,  0 };
    cmap[41]  = new int[] {255, 34,  0 };
    cmap[42]  = new int[] {255, 41,  0 };
    cmap[43]  = new int[] {255, 48,  0 };
    cmap[44]  = new int[] {255, 55,  0 };
    cmap[45]  = new int[] {255, 62,  0 };
    cmap[46]  = new int[] {255, 69,  0 };
    cmap[47]  = new int[] {255, 76,  0 };
    cmap[48]  = new int[] {255, 83,  0 };
    cmap[49]  = new int[] {255, 90,  0 };
    cmap[50]  = new int[] {255, 96,  0 };
    cmap[51]  = new int[] {255, 103, 0};
    cmap[52]  = new int[] {255, 110, 0};
    cmap[53]  = new int[] {255, 117, 0};
    cmap[54]  = new int[] {255, 124, 0};
    cmap[55]  = new int[] {255, 131, 0};
    cmap[56]  = new int[] {255, 138, 0};
    cmap[57]  = new int[] {255, 145, 0};
    cmap[58]  = new int[] {255, 152, 0};
    cmap[59]  = new int[] {255, 159, 0};
    cmap[60]  = new int[] {255, 165, 0};
    cmap[61]  = new int[] {255, 172, 0};
    cmap[62]  = new int[] {255, 179, 0};
    cmap[63]  = new int[] {255, 186, 0};
    cmap[64]  = new int[] {255, 193, 0};
    cmap[65]  = new int[] {255, 200, 0};
    cmap[66]  = new int[] {255, 207, 0};
    cmap[67]  = new int[] {255, 214, 0};
    cmap[68]  = new int[] {255, 221, 0};
    cmap[69]  = new int[] {255, 227, 0};
    cmap[70]  = new int[] {255, 234, 0};
    cmap[71]  = new int[] {255, 241, 0};
    cmap[72]  = new int[] {255, 248, 0};
    cmap[73]  = new int[] {255, 255, 0};
    cmap[74]  = new int[] {248, 255, 0};
    cmap[75]  = new int[] {241, 255, 0};
    cmap[76]  = new int[] {234, 255, 0};
    cmap[77]  = new int[] {227, 255, 0};
    cmap[78]  = new int[] {220, 255, 0};
    cmap[79]  = new int[] {212, 255, 0};
    cmap[80]  = new int[] {205, 255, 0};
    cmap[81]  = new int[] {198, 255, 0};
    cmap[82]  = new int[] {191, 255, 0};
    cmap[83]  = new int[] {184, 255, 0};
    cmap[84]  = new int[] {177, 255, 0};
    cmap[85]  = new int[] {170, 255, 0};
    cmap[86]  = new int[] {163, 255, 0};
    cmap[87]  = new int[] {156, 255, 0};
    cmap[88]  = new int[] {149, 255, 0};
    cmap[89]  = new int[] {142, 255, 0};
    cmap[90]  = new int[] {135, 255, 0};
    cmap[91]  = new int[] {128, 255, 0};
    cmap[92]  = new int[] {120, 255, 0};
    cmap[93]  = new int[] {113, 255, 0};
    cmap[94]  = new int[] {106, 255, 0};
    cmap[95]  = new int[] {99, 255, 0 };
    cmap[96]  = new int[] {92, 255, 0 };
    cmap[97]  = new int[] {85, 255, 0 };
    cmap[98]  = new int[] {78, 255, 0 };
    cmap[99]  = new int[] {71, 255, 0 };
    cmap[100] = new int[] {64, 255, 0 };
    cmap[101] = new int[] {57, 255, 0 };
    cmap[102] = new int[] {50, 255, 0 };
    cmap[103] = new int[] {43, 255, 0 };
    cmap[104] = new int[] {35, 255, 0 };
    cmap[105] = new int[] {28, 255, 0 };
    cmap[106] = new int[] {21, 255, 0 };
    cmap[107] = new int[] {14, 255, 0 };
    cmap[108] = new int[] {7,  255, 0 };
    cmap[109] = new int[] {0, 255, 0  };
    cmap[110] = new int[] {0, 255, 7  };
    cmap[111] = new int[] {0, 255, 14 };
    cmap[112] = new int[] {0, 255, 21 };
    cmap[113] = new int[] {0, 255, 28 };
    cmap[114] = new int[] {0, 255, 35 };
    cmap[115] = new int[] {0, 255, 43 };
    cmap[116] = new int[] {0, 255, 50 };
    cmap[117] = new int[] {0, 255, 57 };
    cmap[118] = new int[] {0, 255, 64 };
    cmap[119] = new int[] {0, 255, 71 };
    cmap[120] = new int[] {0, 255, 78 };
    cmap[121] = new int[] {0, 255, 85 };
    cmap[122] = new int[] {0, 255, 92 };
    cmap[123] = new int[] {0, 255, 99 };
    cmap[124] = new int[] {0, 255, 106};
    cmap[125] = new int[] {0, 255, 113};
    cmap[126] = new int[] {0, 255, 120};
    cmap[127] = new int[] {0, 255, 128};
    cmap[128] = new int[] {0, 255, 135};
    cmap[129] = new int[] {0, 255, 142};
    cmap[130] = new int[] {0, 255, 149};
    cmap[131] = new int[] {0, 255, 156};
    cmap[132] = new int[] {0, 255, 163};
    cmap[133] = new int[] {0, 255, 170};
    cmap[134] = new int[] {0, 255, 177};
    cmap[135] = new int[] {0, 255, 184};
    cmap[136] = new int[] {0, 255, 191};
    cmap[137] = new int[] {0, 255, 198};
    cmap[138] = new int[] {0, 255, 205};
    cmap[139] = new int[] {0, 255, 212};
    cmap[140] = new int[] {0, 255, 220};
    cmap[141] = new int[] {0, 255, 227};
    cmap[142] = new int[] {0, 255, 234};
    cmap[143] = new int[] {0, 255, 241};
    cmap[144] = new int[] {0, 255, 248};
    cmap[145] = new int[] {0, 255, 255};
    cmap[146] = new int[] {0, 248, 255};
    cmap[147] = new int[] {0, 241, 255};
    cmap[148] = new int[] {0, 234, 255};
    cmap[149] = new int[] {0, 227, 255};
    cmap[150] = new int[] {0, 221, 255};
    cmap[151] = new int[] {0, 214, 255};
    cmap[152] = new int[] {0, 207, 255};
    cmap[153] = new int[] {0, 200, 255};
    cmap[154] = new int[] {0, 193, 255};
    cmap[155] = new int[] {0, 186, 255};
    cmap[156] = new int[] {0, 179, 255};
    cmap[157] = new int[] {0, 172, 255};
    cmap[158] = new int[] {0, 165, 255};
    cmap[159] = new int[] {0, 159, 255};
    cmap[160] = new int[] {0, 152, 255};
    cmap[161] = new int[] {0, 145, 255};
    cmap[162] = new int[] {0, 138, 255};
    cmap[163] = new int[] {0, 131, 255};
    cmap[164] = new int[] {0, 124, 255};
    cmap[165] = new int[] {0, 117, 255};
    cmap[166] = new int[] {0, 110, 255};
    cmap[167] = new int[] {0, 103, 255};
    cmap[168] = new int[] {0, 96, 255 };
    cmap[169] = new int[] {0, 90, 255 };
    cmap[170] = new int[] {0, 83, 255 };
    cmap[171] = new int[] {0, 76, 255 };
    cmap[172] = new int[] {0, 69, 255 };
    cmap[173] = new int[] {0, 62, 255 };
    cmap[174] = new int[] {0, 55, 255 };
    cmap[175] = new int[] {0, 48, 255 };
    cmap[176] = new int[] {0, 41, 255 };
    cmap[177] = new int[] {0, 34, 255 };
    cmap[178] = new int[] {0, 28, 255 };
    cmap[179] = new int[] {0, 21, 255 };
    cmap[180] = new int[] {0, 14, 255 };
    cmap[181] = new int[] {0, 7, 255  };
    cmap[182] = new int[] {0, 0, 255  };
    cmap[183] = new int[] {7, 0, 255  };
    cmap[184] = new int[] {14, 0, 255 };
    cmap[185] = new int[] {21, 0, 255 };
    cmap[186] = new int[] {28, 0, 255 };
    cmap[187] = new int[] {34, 0, 255 };
    cmap[188] = new int[] {41, 0, 255 };
    cmap[189] = new int[] {48, 0, 255 };
    cmap[190] = new int[] {55, 0, 255 };
    cmap[191] = new int[] {62, 0, 255 };
    cmap[192] = new int[] {69, 0, 255 };
    cmap[193] = new int[] {76, 0, 255 };
    cmap[194] = new int[] {83, 0, 255 };
    cmap[195] = new int[] {90, 0, 255 };
    cmap[196] = new int[] {96, 0, 255 };
    cmap[197] = new int[] {103, 0, 255};
    cmap[198] = new int[] {110, 0, 255};
    cmap[199] = new int[] {117, 0, 255};
    cmap[200] = new int[] {124, 0, 255};
    cmap[201] = new int[] {131, 0, 255};
    cmap[202] = new int[] {138, 0, 255};
    cmap[203] = new int[] {145, 0, 255};
    cmap[204] = new int[] {152, 0, 255};
    cmap[205] = new int[] {159, 0, 255};
    cmap[206] = new int[] {165, 0, 255};
    cmap[207] = new int[] {172, 0, 255};
    cmap[208] = new int[] {179, 0, 255};
    cmap[209] = new int[] {186, 0, 255};
    cmap[210] = new int[] {193, 0, 255};
    cmap[211] = new int[] {200, 0, 255};
    cmap[212] = new int[] {207, 0, 255};
    cmap[213] = new int[] {214, 0, 255};
    cmap[214] = new int[] {221, 0, 255};
    cmap[215] = new int[] {227, 0, 255};
    cmap[216] = new int[] {234, 0, 255};
    cmap[217] = new int[] {241, 0, 255};
    cmap[218] = new int[] {248, 0, 255};
    cmap[219] = new int[] {255, 0, 255};
    cmap[220] = new int[] {255, 7, 255};
    cmap[221] = new int[] {255, 14, 255};
    cmap[222] = new int[] {255, 21, 255};
    cmap[223] = new int[] {255, 28, 255};
    cmap[224] = new int[] {255, 35, 255};
    cmap[225] = new int[] {255, 43, 255};
    cmap[226] = new int[] {255, 50, 255};
    cmap[227] = new int[] {255, 57, 255};
    cmap[228] = new int[] {255, 64, 255};
    cmap[229] = new int[] {255, 71, 255};
    cmap[230] = new int[] {255, 78, 255};
    cmap[231] = new int[] {255, 85, 255};
    cmap[232] = new int[] {255, 92, 255};
    cmap[233] = new int[] {255, 99, 255};
    cmap[234] = new int[] {255, 106, 255};
    cmap[235] = new int[] {255, 113, 255};
    cmap[236] = new int[] {255, 120, 255};
    cmap[237] = new int[] {255, 128, 255};
    cmap[238] = new int[] {255, 135, 255};
    cmap[239] = new int[] {255, 142, 255};
    cmap[240] = new int[] {255, 149, 255};
    cmap[241] = new int[] {255, 156, 255};
    cmap[242] = new int[] {255, 163, 255};
    cmap[243] = new int[] {255, 170, 255};
    cmap[244] = new int[] {255, 177, 255};
    cmap[245] = new int[] {255, 184, 255};
    cmap[246] = new int[] {255, 191, 255};
    cmap[247] = new int[] {255, 198, 255};
    cmap[248] = new int[] {255, 205, 255};
    cmap[249] = new int[] {255, 212, 255};
    cmap[250] = new int[] {255, 220, 255};
    cmap[251] = new int[] {255, 227, 255};
    cmap[252] = new int[] {255, 234, 255};
    cmap[253] = new int[] {255, 241, 255};
    cmap[254] = new int[] {255, 248, 255};
    cmap[255] = new int[] {255, 255, 255};
  }


  public int[] getColorValue(int i) {
    try {
      return cmap[i];
    } catch (IndexOutOfBoundsException e) {
      if(i<0) 
        return cmap[0];
      else
        return cmap[255];
    }
  }
}
