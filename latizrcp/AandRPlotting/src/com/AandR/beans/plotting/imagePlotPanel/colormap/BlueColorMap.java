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
 * @version $Revision: 1.1 $, $Date: 2007/05/25 00:12:33 $
 */
public class BlueColorMap extends AbstractColorMap {
  
  private int[][] cmap = new int[256][3];

  public BlueColorMap() {
    cmap[0]   = new int[] {0  , 0  , 0  };
    cmap[1]   = new int[] {0  , 0  , 2  };
    cmap[2]   = new int[] {0  , 0  , 4  };
    cmap[3]   = new int[] {0  , 0  , 6  };
    cmap[4]   = new int[] {0  , 0  , 8  };
    cmap[5]   = new int[] {0  , 0  , 10 };
    cmap[6]   = new int[] {0  , 0  , 12 };
    cmap[7]   = new int[] {0  , 0  , 14 };
    cmap[8]   = new int[] {0  , 0  , 16 };
    cmap[9]   = new int[] {0  , 0  , 18 };
    cmap[10]  = new int[] {0  , 0  , 20 };
    cmap[11]  = new int[] {0  , 0  , 22 };
    cmap[12]  = new int[] {0  , 0  , 24 };
    cmap[13]  = new int[] {0  , 0  , 26 };
    cmap[14]  = new int[] {0  , 0  , 28 };
    cmap[15]  = new int[] {0  , 0  , 30 };
    cmap[16]  = new int[] {0  , 0  , 32 };
    cmap[17]  = new int[] {0  , 0  , 34 };
    cmap[18]  = new int[] {0  , 0  , 36 };
    cmap[19]  = new int[] {0  , 0  , 38 };
    cmap[20]  = new int[] {0  , 0  , 40 };
    cmap[21]  = new int[] {0  , 0  , 42 };
    cmap[22]  = new int[] {0  , 0  , 44 };
    cmap[23]  = new int[] {0  , 0  , 46 };
    cmap[24]  = new int[] {0  , 0  , 48 };
    cmap[25]  = new int[] {0  , 0  , 50 };
    cmap[26]  = new int[] {0  , 0  , 52 };
    cmap[27]  = new int[] {0  , 0  , 54 };
    cmap[28]  = new int[] {0  , 0  , 56 };
    cmap[29]  = new int[] {0  , 0  , 58 };
    cmap[30]  = new int[] {0  , 0  , 60 };
    cmap[31]  = new int[] {0  , 0  , 62 };
    cmap[32]  = new int[] {0  , 0  , 64 };
    cmap[33]  = new int[] {0  , 0  , 66 };
    cmap[34]  = new int[] {0  , 0  , 68 };
    cmap[35]  = new int[] {0  , 0  , 70 };
    cmap[36]  = new int[] {0  , 0  , 72 };
    cmap[37]  = new int[] {0  , 0  , 74 };
    cmap[38]  = new int[] {0  , 0  , 76 };
    cmap[39]  = new int[] {0  , 0  , 78 };
    cmap[40]  = new int[] {0  , 0  , 80 };
    cmap[41]  = new int[] {0  , 0  , 82 };
    cmap[42]  = new int[] {0  , 0  , 84 };
    cmap[43]  = new int[] {0  , 0  , 86 };
    cmap[44]  = new int[] {0  , 0  , 88 };
    cmap[45]  = new int[] {0  , 0  , 90 };
    cmap[46]  = new int[] {0  , 0  , 92 };
    cmap[47]  = new int[] {0  , 0  , 94 };
    cmap[48]  = new int[] {0  , 0  , 96 };
    cmap[49]  = new int[] {0  , 0  , 98 };
    cmap[50]  = new int[] {0  , 0  , 100};
    cmap[51]  = new int[] {0  , 0  , 102};
    cmap[52]  = new int[] {0  , 0  , 104};
    cmap[53]  = new int[] {0  , 0  , 106};
    cmap[54]  = new int[] {0  , 0  , 108};
    cmap[55]  = new int[] {0  , 0  , 110};
    cmap[56]  = new int[] {0  , 0  , 112};
    cmap[57]  = new int[] {0  , 0  , 114};
    cmap[58]  = new int[] {0  , 0  , 116};
    cmap[59]  = new int[] {0  , 0  , 118};
    cmap[60]  = new int[] {0  , 0  , 120};
    cmap[61]  = new int[] {0  , 0  , 122};
    cmap[62]  = new int[] {0  , 0  , 124};
    cmap[63]  = new int[] {0  , 0  , 126};
    cmap[64]  = new int[] {0  , 0  , 128};
    cmap[65]  = new int[] {0  , 0  , 130};
    cmap[66]  = new int[] {0  , 0  , 132};
    cmap[67]  = new int[] {0  , 0  , 134};
    cmap[68]  = new int[] {0  , 0  , 136};
    cmap[69]  = new int[] {0  , 0  , 138};
    cmap[70]  = new int[] {0  , 0  , 140};
    cmap[71]  = new int[] {0  , 0  , 142};
    cmap[72]  = new int[] {0  , 0  , 144};
    cmap[73]  = new int[] {0  , 0  , 146};
    cmap[74]  = new int[] {0  , 0  , 148};
    cmap[75]  = new int[] {0  , 0  , 150};
    cmap[76]  = new int[] {0  , 0  , 152};
    cmap[77]  = new int[] {0  , 0  , 154};
    cmap[78]  = new int[] {0  , 0  , 156};
    cmap[79]  = new int[] {0  , 0  , 158};
    cmap[80]  = new int[] {0  , 0  , 160};
    cmap[81]  = new int[] {0  , 0  , 162};
    cmap[82]  = new int[] {0  , 0  , 164};
    cmap[83]  = new int[] {0  , 0  , 166};
    cmap[84]  = new int[] {0  , 0  , 168};
    cmap[85]  = new int[] {0  , 0  , 170};
    cmap[86]  = new int[] {0  , 0  , 172};
    cmap[87]  = new int[] {0  , 0  , 174};
    cmap[88]  = new int[] {0  , 0  , 176};
    cmap[89]  = new int[] {0  , 0  , 178};
    cmap[90]  = new int[] {0  , 0  , 180};
    cmap[91]  = new int[] {0  , 0  , 182};
    cmap[92]  = new int[] {0  , 0  , 184};
    cmap[93]  = new int[] {0  , 0  , 186};
    cmap[94]  = new int[] {0  , 0  , 188};
    cmap[95]  = new int[] {0  , 0  , 190};
    cmap[96]  = new int[] {0  , 0  , 192};
    cmap[97]  = new int[] {0  , 0  , 194};
    cmap[98]  = new int[] {0  , 0  , 196};
    cmap[99]  = new int[] {0  , 0  , 198};
    cmap[100] = new int[] {0  , 0  , 200};
    cmap[101] = new int[] {0  , 0  , 202};
    cmap[102] = new int[] {0  , 0  , 204};
    cmap[103] = new int[] {0  , 0  , 206};
    cmap[104] = new int[] {0  , 0  , 208};
    cmap[105] = new int[] {0  , 0  , 210};
    cmap[106] = new int[] {0  , 0  , 212};
    cmap[107] = new int[] {0  , 0  , 214};
    cmap[108] = new int[] {0  , 0  , 216};
    cmap[109] = new int[] {0  , 0  , 218};
    cmap[110] = new int[] {0  , 0  , 220};
    cmap[111] = new int[] {0  , 0  , 222};
    cmap[112] = new int[] {0  , 0  , 224};
    cmap[113] = new int[] {0  , 0  , 226};
    cmap[114] = new int[] {0  , 0  , 228};
    cmap[115] = new int[] {0  , 0  , 230};
    cmap[116] = new int[] {0  , 0  , 232};
    cmap[117] = new int[] {0  , 0  , 234};
    cmap[118] = new int[] {0  , 0  , 236};
    cmap[119] = new int[] {0  , 0  , 238};
    cmap[120] = new int[] {0  , 0  , 240};
    cmap[121] = new int[] {0  , 0  , 242};
    cmap[122] = new int[] {0  , 0  , 244};
    cmap[123] = new int[] {0  , 0  , 246};
    cmap[124] = new int[] {0  , 0  , 248};
    cmap[125] = new int[] {0  , 0  , 250};
    cmap[126] = new int[] {0  , 0  , 252};
    cmap[127] = new int[] {0  , 0  , 254};
    cmap[128] = new int[] {2  , 2  , 255};
    cmap[129] = new int[] {4  , 4  , 255};
    cmap[130] = new int[] {6  , 6  , 255};
    cmap[131] = new int[] {8  , 8  , 255};
    cmap[132] = new int[] {10 , 10 , 255};
    cmap[133] = new int[] {12 , 12 , 255};
    cmap[134] = new int[] {14 , 14 , 255};
    cmap[135] = new int[] {16 , 16 , 255};
    cmap[136] = new int[] {18 , 18 , 255};
    cmap[137] = new int[] {20 , 20 , 255};
    cmap[138] = new int[] {22 , 22 , 255};
    cmap[139] = new int[] {24 , 24 , 255};
    cmap[140] = new int[] {26 , 26 , 255};
    cmap[141] = new int[] {28 , 28 , 255};
    cmap[142] = new int[] {30 , 30 , 255};
    cmap[143] = new int[] {32 , 32 , 255};
    cmap[144] = new int[] {34 , 34 , 255};
    cmap[145] = new int[] {36 , 36 , 255};
    cmap[146] = new int[] {38 , 38 , 255};
    cmap[147] = new int[] {40 , 40 , 255};
    cmap[148] = new int[] {42 , 42 , 255};
    cmap[149] = new int[] {44 , 44 , 255};
    cmap[150] = new int[] {46 , 46 , 255};
    cmap[151] = new int[] {48 , 48 , 255};
    cmap[152] = new int[] {50 , 50 , 255};
    cmap[153] = new int[] {52 , 52 , 255};
    cmap[154] = new int[] {54 , 54 , 255};
    cmap[155] = new int[] {56 , 56 , 255};
    cmap[156] = new int[] {58 , 58 , 255};
    cmap[157] = new int[] {60 , 60 , 255};
    cmap[158] = new int[] {62 , 62 , 255};
    cmap[159] = new int[] {64 , 64 , 255};
    cmap[160] = new int[] {66 , 66 , 255};
    cmap[161] = new int[] {68 , 68 , 255};
    cmap[162] = new int[] {70 , 70 , 255};
    cmap[163] = new int[] {72 , 72 , 255};
    cmap[164] = new int[] {74 , 74 , 255};
    cmap[165] = new int[] {76 , 76 , 255};
    cmap[166] = new int[] {78 , 78 , 255};
    cmap[167] = new int[] {80 , 80 , 255};
    cmap[168] = new int[] {82 , 82 , 255};
    cmap[169] = new int[] {84 , 84 , 255};
    cmap[170] = new int[] {86 , 86 , 255};
    cmap[171] = new int[] {88 , 88 , 255};
    cmap[172] = new int[] {90 , 90 , 255};
    cmap[173] = new int[] {92 , 92 , 255};
    cmap[174] = new int[] {94 , 94 , 255};
    cmap[175] = new int[] {96 , 96 , 255};
    cmap[176] = new int[] {98 , 98 , 255};
    cmap[177] = new int[] {100, 100, 255};
    cmap[178] = new int[] {102, 102, 255};
    cmap[179] = new int[] {104, 104, 255};
    cmap[180] = new int[] {106, 106, 255};
    cmap[181] = new int[] {108, 108, 255};
    cmap[182] = new int[] {110, 110, 255};
    cmap[183] = new int[] {112, 112, 255};
    cmap[184] = new int[] {114, 114, 255};
    cmap[185] = new int[] {116, 116, 255};
    cmap[186] = new int[] {118, 118, 255};
    cmap[187] = new int[] {120, 120, 255};
    cmap[188] = new int[] {122, 122, 255};
    cmap[189] = new int[] {124, 124, 255};
    cmap[190] = new int[] {126, 126, 255};
    cmap[191] = new int[] {128, 128, 255};
    cmap[192] = new int[] {130, 130, 255};
    cmap[193] = new int[] {132, 132, 255};
    cmap[194] = new int[] {134, 134, 255};
    cmap[195] = new int[] {136, 136, 255};
    cmap[196] = new int[] {138, 138, 255};
    cmap[197] = new int[] {140, 140, 255};
    cmap[198] = new int[] {142, 142, 255};
    cmap[199] = new int[] {144, 144, 255};
    cmap[200] = new int[] {146, 146, 255};
    cmap[201] = new int[] {148, 148, 255};
    cmap[202] = new int[] {150, 150, 255};
    cmap[203] = new int[] {152, 152, 255};
    cmap[204] = new int[] {154, 154, 255};
    cmap[205] = new int[] {156, 156, 255};
    cmap[206] = new int[] {158, 158, 255};
    cmap[207] = new int[] {160, 160, 255};
    cmap[208] = new int[] {162, 162, 255};
    cmap[209] = new int[] {164, 164, 255};
    cmap[210] = new int[] {166, 166, 255};
    cmap[211] = new int[] {168, 168, 255};
    cmap[212] = new int[] {170, 170, 255};
    cmap[213] = new int[] {172, 172, 255};
    cmap[214] = new int[] {174, 174, 255};
    cmap[215] = new int[] {176, 176, 255};
    cmap[216] = new int[] {178, 178, 255};
    cmap[217] = new int[] {180, 180, 255};
    cmap[218] = new int[] {182, 182, 255};
    cmap[219] = new int[] {184, 184, 255};
    cmap[220] = new int[] {186, 186, 255};
    cmap[221] = new int[] {188, 188, 255};
    cmap[222] = new int[] {190, 190, 255};
    cmap[223] = new int[] {192, 192, 255};
    cmap[224] = new int[] {194, 194, 255};
    cmap[225] = new int[] {196, 196, 255};
    cmap[226] = new int[] {198, 198, 255};
    cmap[227] = new int[] {200, 200, 255};
    cmap[228] = new int[] {202, 202, 255};
    cmap[229] = new int[] {204, 204, 255};
    cmap[230] = new int[] {206, 206, 255};
    cmap[231] = new int[] {208, 208, 255};
    cmap[232] = new int[] {210, 210, 255};
    cmap[233] = new int[] {212, 212, 255};
    cmap[234] = new int[] {214, 214, 255};
    cmap[235] = new int[] {216, 216, 255};
    cmap[236] = new int[] {218, 218, 255};
    cmap[237] = new int[] {220, 220, 255};
    cmap[238] = new int[] {222, 222, 255};
    cmap[239] = new int[] {224, 224, 255};
    cmap[240] = new int[] {226, 226, 255};
    cmap[241] = new int[] {228, 228, 255};
    cmap[242] = new int[] {230, 230, 255};
    cmap[243] = new int[] {232, 232, 255};
    cmap[244] = new int[] {234, 234, 255};
    cmap[245] = new int[] {236, 236, 255};
    cmap[246] = new int[] {238, 238, 255};
    cmap[247] = new int[] {240, 240, 255};
    cmap[248] = new int[] {242, 242, 255};
    cmap[249] = new int[] {244, 244, 255};
    cmap[250] = new int[] {246, 246, 255};
    cmap[251] = new int[] {248, 248, 255};
    cmap[252] = new int[] {250, 250, 255};
    cmap[253] = new int[] {252, 252, 255};
    cmap[254] = new int[] {254, 254, 255};
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
