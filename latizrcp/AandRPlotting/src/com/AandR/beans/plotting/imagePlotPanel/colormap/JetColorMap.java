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
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:32 $
 */
public class JetColorMap extends AbstractColorMap {

  private int[][] cmap = new int[256][3];
  
  public JetColorMap() {
    cmap[0  ] = new int[] {0  , 0  , 131};
    cmap[1  ] = new int[] {0  , 0  , 135};
    cmap[2  ] = new int[] {0  , 4  , 139};
    cmap[3  ] = new int[] {0  , 8  , 143};
    cmap[4  ] = new int[] {0  , 12 , 147};
    cmap[5  ] = new int[] {0  , 16 , 151};
    cmap[6  ] = new int[] {0  , 20 , 155};
    cmap[7  ] = new int[] {0  , 24 , 159};
    cmap[8  ] = new int[] {0  , 28 , 163};
    cmap[9  ] = new int[] {0  , 32 , 167};
    cmap[10 ] = new int[] {0  , 36 , 171};
    cmap[11 ] = new int[] {0  , 40 , 175};
    cmap[12 ] = new int[] {0  , 44 , 179};
    cmap[13 ] = new int[] {0  , 48 , 183};
    cmap[14 ] = new int[] {0  , 52 , 187};
    cmap[15 ] = new int[] {0  , 56 , 191};
    cmap[16 ] = new int[] {0  , 60 , 195};
    cmap[17 ] = new int[] {0  , 64 , 199};
    cmap[18 ] = new int[] {0  , 68 , 203};
    cmap[19 ] = new int[] {0  , 72 , 207};
    cmap[20 ] = new int[] {0  , 76 , 211};
    cmap[21 ] = new int[] {0  , 80 , 215};
    cmap[22 ] = new int[] {0  , 84 , 219};
    cmap[23 ] = new int[] {0  , 88 , 223};
    cmap[24 ] = new int[] {0  , 92 , 227};
    cmap[25 ] = new int[] {0  , 96 , 231};
    cmap[26 ] = new int[] {0  , 100, 235};
    cmap[27 ] = new int[] {0  , 104, 239};
    cmap[28 ] = new int[] {0  , 108, 243};
    cmap[29 ] = new int[] {0  , 112, 247};
    cmap[30 ] = new int[] {0  , 116, 251};
    cmap[31 ] = new int[] {0  , 120, 255};
    cmap[32 ] = new int[] {0  , 124, 255};
    cmap[33 ] = new int[] {0  , 128, 255};
    cmap[34 ] = new int[] {0  , 131, 255};
    cmap[35 ] = new int[] {0  , 135, 255};
    cmap[36 ] = new int[] {0  , 139, 255};
    cmap[37 ] = new int[] {0  , 143, 255};
    cmap[38 ] = new int[] {0  , 147, 255};
    cmap[39 ] = new int[] {0  , 151, 255};
    cmap[40 ] = new int[] {0  , 155, 255};
    cmap[41 ] = new int[] {0  , 159, 255};
    cmap[42 ] = new int[] {0  , 163, 255};
    cmap[43 ] = new int[] {0  , 167, 255};
    cmap[44 ] = new int[] {0  , 171, 255};
    cmap[45 ] = new int[] {0  , 175, 255};
    cmap[46 ] = new int[] {0  , 179, 255};
    cmap[47 ] = new int[] {0  , 183, 255};
    cmap[48 ] = new int[] {0  , 187, 255};
    cmap[49 ] = new int[] {0  , 191, 255};
    cmap[50 ] = new int[] {0  , 195, 255};
    cmap[51 ] = new int[] {0  , 199, 255};
    cmap[52 ] = new int[] {0  , 203, 255};
    cmap[53 ] = new int[] {0  , 207, 255};
    cmap[54 ] = new int[] {0  , 211, 255};
    cmap[55 ] = new int[] {0  , 215, 255};
    cmap[56 ] = new int[] {0  , 219, 255};
    cmap[57 ] = new int[] {0  , 223, 255};
    cmap[58 ] = new int[] {0  , 227, 255};
    cmap[59 ] = new int[] {0  , 231, 255};
    cmap[60 ] = new int[] {0  , 235, 255};
    cmap[61 ] = new int[] {0  , 239, 255};
    cmap[62 ] = new int[] {0  , 243, 255};
    cmap[63 ] = new int[] {0  , 247, 255};
    cmap[64 ] = new int[] {0  , 251, 255};
    cmap[65 ] = new int[] {0  , 255, 255};
    cmap[66 ] = new int[] {0  , 255, 255};
    cmap[67 ] = new int[] {0  , 255, 255};
    cmap[68 ] = new int[] {0  , 255, 255};
    cmap[69 ] = new int[] {0  , 255, 255};
    cmap[70 ] = new int[] {0  , 255, 255};
    cmap[71 ] = new int[] {0  , 255, 255};
    cmap[72 ] = new int[] {0  , 255, 255};
    cmap[73 ] = new int[] {0  , 255, 255};
    cmap[74 ] = new int[] {0  , 255, 255};
    cmap[75 ] = new int[] {0  , 255, 255};
    cmap[76 ] = new int[] {0  , 255, 255};
    cmap[77 ] = new int[] {0  , 255, 255};
    cmap[78 ] = new int[] {0  , 255, 255};
    cmap[79 ] = new int[] {0  , 255, 255};
    cmap[80 ] = new int[] {0  , 255, 255};
    cmap[81 ] = new int[] {0  , 255, 255};
    cmap[82 ] = new int[] {0  , 255, 255};
    cmap[83 ] = new int[] {0  , 255, 255};
    cmap[84 ] = new int[] {0  , 255, 255};
    cmap[85 ] = new int[] {0  , 255, 255};
    cmap[86 ] = new int[] {0  , 255, 255};
    cmap[87 ] = new int[] {0  , 255, 255};
    cmap[88 ] = new int[] {0  , 255, 255};
    cmap[89 ] = new int[] {0  , 255, 255};
    cmap[90 ] = new int[] {0  , 255, 255};
    cmap[91 ] = new int[] {0  , 255, 255};
    cmap[92 ] = new int[] {0  , 255, 255};
    cmap[93 ] = new int[] {0  , 255, 255};
    cmap[94 ] = new int[] {0  , 255, 255};
    cmap[95 ] = new int[] {0  , 255, 255};
    cmap[96 ] = new int[] {4  , 255, 251};
    cmap[97 ] = new int[] {8  , 255, 247};
    cmap[98 ] = new int[] {12 , 255, 243};
    cmap[99 ] = new int[] {16 , 255, 239};
    cmap[100] = new int[] {20 , 255, 235};
    cmap[101] = new int[] {24 , 255, 231};
    cmap[102] = new int[] {28 , 255, 227};
    cmap[103] = new int[] {32 , 255, 223};
    cmap[104] = new int[] {36 , 255, 219};
    cmap[105] = new int[] {40 , 255, 215};
    cmap[106] = new int[] {44 , 255, 211};
    cmap[107] = new int[] {48 , 255, 207};
    cmap[108] = new int[] {52 , 255, 203};
    cmap[109] = new int[] {56 , 255, 199};
    cmap[110] = new int[] {60 , 255, 195};
    cmap[111] = new int[] {64 , 255, 191};
    cmap[112] = new int[] {68 , 255, 187};
    cmap[113] = new int[] {72 , 255, 183};
    cmap[114] = new int[] {76 , 255, 179};
    cmap[115] = new int[] {80 , 255, 175};
    cmap[116] = new int[] {84 , 255, 171};
    cmap[117] = new int[] {88 , 255, 167};
    cmap[118] = new int[] {92 , 255, 163};
    cmap[119] = new int[] {96 , 255, 159};
    cmap[120] = new int[] {100, 255, 155};
    cmap[121] = new int[] {104, 255, 151};
    cmap[122] = new int[] {108, 255, 147};
    cmap[123] = new int[] {112, 255, 143};
    cmap[124] = new int[] {116, 255, 139};
    cmap[125] = new int[] {120, 255, 135};
    cmap[126] = new int[] {124, 255, 131};
    cmap[127] = new int[] {128, 255, 128};
    cmap[128] = new int[] {131, 255, 124};
    cmap[129] = new int[] {135, 255, 120};
    cmap[130] = new int[] {139, 251, 116};
    cmap[131] = new int[] {143, 247, 112};
    cmap[132] = new int[] {147, 243, 108};
    cmap[133] = new int[] {151, 239, 104};
    cmap[134] = new int[] {155, 235, 100};
    cmap[135] = new int[] {159, 231, 96 };
    cmap[136] = new int[] {163, 227, 92 };
    cmap[137] = new int[] {167, 223, 88 };
    cmap[138] = new int[] {171, 219, 84 };
    cmap[139] = new int[] {175, 215, 80 };
    cmap[140] = new int[] {179, 211, 76 };
    cmap[141] = new int[] {183, 207, 72 };
    cmap[142] = new int[] {187, 203, 68 };
    cmap[143] = new int[] {191, 199, 64 };
    cmap[144] = new int[] {195, 195, 60 };
    cmap[145] = new int[] {199, 191, 56 };
    cmap[146] = new int[] {203, 187, 52 };
    cmap[147] = new int[] {207, 183, 48 };
    cmap[148] = new int[] {211, 179, 44 };
    cmap[149] = new int[] {215, 175, 40 };
    cmap[150] = new int[] {219, 171, 36 };
    cmap[151] = new int[] {223, 167, 32 };
    cmap[152] = new int[] {227, 163, 28 };
    cmap[153] = new int[] {231, 159, 24 };
    cmap[154] = new int[] {235, 155, 20 };
    cmap[155] = new int[] {239, 151, 16 };
    cmap[156] = new int[] {243, 147, 12 };
    cmap[157] = new int[] {247, 143, 8  };
    cmap[158] = new int[] {251, 139, 4  };
    cmap[159] = new int[] {255, 135, 0  };
    cmap[160] = new int[] {255, 131, 0  };
    cmap[161] = new int[] {255, 128, 0  };
    cmap[162] = new int[] {255, 124, 0  };
    cmap[163] = new int[] {255, 120, 0  };
    cmap[164] = new int[] {255, 116, 0  };
    cmap[165] = new int[] {255, 112, 0  };
    cmap[166] = new int[] {255, 108, 0  };
    cmap[167] = new int[] {255, 104, 0  };
    cmap[168] = new int[] {255, 100, 0  };
    cmap[169] = new int[] {255, 96 , 0  };
    cmap[170] = new int[] {255, 92 , 0  };
    cmap[171] = new int[] {255, 88 , 0  };
    cmap[172] = new int[] {255, 84 , 0  };
    cmap[173] = new int[] {255, 80 , 0  };
    cmap[174] = new int[] {255, 76 , 0  };
    cmap[175] = new int[] {255, 72 , 0  };
    cmap[176] = new int[] {255, 68 , 0  };
    cmap[177] = new int[] {255, 64 , 0  };
    cmap[178] = new int[] {255, 60 , 0  };
    cmap[179] = new int[] {255, 56 , 0  };
    cmap[180] = new int[] {255, 52 , 0  };
    cmap[181] = new int[] {255, 48 , 0  };
    cmap[182] = new int[] {255, 44 , 0  };
    cmap[183] = new int[] {255, 40 , 0  };
    cmap[184] = new int[] {255, 36 , 0  };
    cmap[185] = new int[] {255, 32 , 0  };
    cmap[186] = new int[] {255, 28 , 0  };
    cmap[187] = new int[] {255, 24 , 0  };
    cmap[188] = new int[] {255, 20 , 0  };
    cmap[189] = new int[] {255, 16 , 0  };
    cmap[190] = new int[] {255, 12 , 0  };
    cmap[191] = new int[] {255, 8  , 0  };
    cmap[192] = new int[] {255, 4  , 0  };
    cmap[193] = new int[] {255, 0  , 0  };
    cmap[194] = new int[] {255, 0  , 0  };
    cmap[195] = new int[] {255, 0  , 0  };
    cmap[196] = new int[] {255, 0  , 0  };
    cmap[197] = new int[] {255, 0  , 0  };
    cmap[198] = new int[] {255, 0  , 0  };
    cmap[199] = new int[] {255, 0  , 0  };
    cmap[200] = new int[] {255, 0  , 0  };
    cmap[201] = new int[] {255, 0  , 0  };
    cmap[202] = new int[] {255, 0  , 0  };
    cmap[203] = new int[] {255, 0  , 0  };
    cmap[204] = new int[] {255, 0  , 0  };
    cmap[205] = new int[] {255, 0  , 0  };
    cmap[206] = new int[] {255, 0  , 0  };
    cmap[207] = new int[] {255, 0  , 0  };
    cmap[208] = new int[] {255, 0  , 0  };
    cmap[209] = new int[] {255, 0  , 0  };
    cmap[210] = new int[] {255, 0  , 0  };
    cmap[211] = new int[] {255, 0  , 0  };
    cmap[212] = new int[] {255, 0  , 0  };
    cmap[213] = new int[] {255, 0  , 0  };
    cmap[214] = new int[] {255, 0  , 0  };
    cmap[215] = new int[] {255, 0  , 0  };
    cmap[216] = new int[] {255, 0  , 0  };
    cmap[217] = new int[] {255, 0  , 0  };
    cmap[218] = new int[] {255, 0  , 0  };
    cmap[219] = new int[] {255, 0  , 0  };
    cmap[220] = new int[] {255, 0  , 0  };
    cmap[221] = new int[] {255, 0  , 0  };
    cmap[222] = new int[] {255, 0  , 0  };
    cmap[223] = new int[] {255, 0  , 0  };
    cmap[224] = new int[] {251, 0  , 0  };
    cmap[225] = new int[] {247, 0  , 0  };
    cmap[226] = new int[] {243, 0  , 0  };
    cmap[227] = new int[] {239, 0  , 0  };
    cmap[228] = new int[] {235, 0  , 0  };
    cmap[229] = new int[] {231, 0  , 0  };
    cmap[230] = new int[] {227, 0  , 0  };
    cmap[231] = new int[] {223, 0  , 0  };
    cmap[232] = new int[] {219, 0  , 0  };
    cmap[233] = new int[] {215, 0  , 0  };
    cmap[234] = new int[] {211, 0  , 0  };
    cmap[235] = new int[] {207, 0  , 0  };
    cmap[236] = new int[] {203, 0  , 0  };
    cmap[237] = new int[] {199, 0  , 0  };
    cmap[238] = new int[] {195, 0  , 0  };
    cmap[239] = new int[] {191, 0  , 0  };
    cmap[240] = new int[] {187, 0  , 0  };
    cmap[241] = new int[] {183, 0  , 0  };
    cmap[242] = new int[] {179, 0  , 0  };
    cmap[243] = new int[] {175, 0  , 0  };
    cmap[244] = new int[] {171, 0  , 0  };
    cmap[245] = new int[] {167, 0  , 0  };
    cmap[246] = new int[] {163, 0  , 0  };
    cmap[247] = new int[] {159, 0  , 0  };
    cmap[248] = new int[] {155, 0  , 0  };
    cmap[249] = new int[] {151, 0  , 0  };
    cmap[250] = new int[] {147, 0  , 0  };
    cmap[251] = new int[] {143, 0  , 0  };
    cmap[252] = new int[] {139, 0  , 0  };
    cmap[253] = new int[] {135, 0  , 0  };
    cmap[254] = new int[] {131, 0  , 0  };
    cmap[255] = new int[] {128, 0  , 0  };
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
