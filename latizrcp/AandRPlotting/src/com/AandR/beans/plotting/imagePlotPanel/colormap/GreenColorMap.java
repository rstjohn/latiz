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
public class GreenColorMap extends AbstractColorMap {
  
  private int[][] cmap = new int[256][3];

  public GreenColorMap() {
    cmap[0]   = new int[] {0  , 0  , 0  };
    cmap[1]   = new int[] {0  , 2  , 0  };
    cmap[2]   = new int[] {0  , 4  , 0  };
    cmap[3]   = new int[] {0  , 6  , 0  };
    cmap[4]   = new int[] {0  , 8  , 0  };
    cmap[5]   = new int[] {0  , 10 , 0  };
    cmap[6]   = new int[] {0  , 12 , 0  };
    cmap[7]   = new int[] {0  , 14 , 0  };
    cmap[8]   = new int[] {0  , 16 , 0  };
    cmap[9]   = new int[] {0  , 18 , 0  };
    cmap[10]  = new int[] {0  , 20 , 0  };
    cmap[11]  = new int[] {0  , 22 , 0  };
    cmap[12]  = new int[] {0  , 24 , 0  };
    cmap[13]  = new int[] {0  , 26 , 0  };
    cmap[14]  = new int[] {0  , 28 , 0  };
    cmap[15]  = new int[] {0  , 30 , 0  };
    cmap[16]  = new int[] {0  , 32 , 0  };
    cmap[17]  = new int[] {0  , 34 , 0  };
    cmap[18]  = new int[] {0  , 36 , 0  };
    cmap[19]  = new int[] {0  , 38 , 0  };
    cmap[20]  = new int[] {0  , 40 , 0  };
    cmap[21]  = new int[] {0  , 42 , 0  };
    cmap[22]  = new int[] {0  , 44 , 0  };
    cmap[23]  = new int[] {0  , 46 , 0  };
    cmap[24]  = new int[] {0  , 48 , 0  };
    cmap[25]  = new int[] {0  , 50 , 0  };
    cmap[26]  = new int[] {0  , 52 , 0  };
    cmap[27]  = new int[] {0  , 54 , 0  };
    cmap[28]  = new int[] {0  , 56 , 0  };
    cmap[29]  = new int[] {0  , 58 , 0  };
    cmap[30]  = new int[] {0  , 60 , 0  };
    cmap[31]  = new int[] {0  , 62 , 0  };
    cmap[32]  = new int[] {0  , 64 , 0  };
    cmap[33]  = new int[] {0  , 66 , 0  };
    cmap[34]  = new int[] {0  , 68 , 0  };
    cmap[35]  = new int[] {0  , 70 , 0  };
    cmap[36]  = new int[] {0  , 72 , 0  };
    cmap[37]  = new int[] {0  , 74 , 0  };
    cmap[38]  = new int[] {0  , 76 , 0  };
    cmap[39]  = new int[] {0  , 78 , 0  };
    cmap[40]  = new int[] {0  , 80 , 0  };
    cmap[41]  = new int[] {0  , 82 , 0  };
    cmap[42]  = new int[] {0  , 84 , 0  };
    cmap[43]  = new int[] {0  , 86 , 0  };
    cmap[44]  = new int[] {0  , 88 , 0  };
    cmap[45]  = new int[] {0  , 90 , 0  };
    cmap[46]  = new int[] {0  , 92 , 0  };
    cmap[47]  = new int[] {0  , 94 , 0  };
    cmap[48]  = new int[] {0  , 96 , 0  };
    cmap[49]  = new int[] {0  , 98 , 0  };
    cmap[50]  = new int[] {0  , 100, 0  };
    cmap[51]  = new int[] {0  , 102, 0  };
    cmap[52]  = new int[] {0  , 104, 0  };
    cmap[53]  = new int[] {0  , 106, 0  };
    cmap[54]  = new int[] {0  , 108, 0  };
    cmap[55]  = new int[] {0  , 110, 0  };
    cmap[56]  = new int[] {0  , 112, 0  };
    cmap[57]  = new int[] {0  , 114, 0  };
    cmap[58]  = new int[] {0  , 116, 0  };
    cmap[59]  = new int[] {0  , 118, 0  };
    cmap[60]  = new int[] {0  , 120, 0  };
    cmap[61]  = new int[] {0  , 122, 0  };
    cmap[62]  = new int[] {0  , 124, 0  };
    cmap[63]  = new int[] {0  , 126, 0  };
    cmap[64]  = new int[] {0  , 128, 0  };
    cmap[65]  = new int[] {0  , 130, 0  };
    cmap[66]  = new int[] {0  , 132, 0  };
    cmap[67]  = new int[] {0  , 134, 0  };
    cmap[68]  = new int[] {0  , 136, 0  };
    cmap[69]  = new int[] {0  , 138, 0  };
    cmap[70]  = new int[] {0  , 140, 0  };
    cmap[71]  = new int[] {0  , 142, 0  };
    cmap[72]  = new int[] {0  , 144, 0  };
    cmap[73]  = new int[] {0  , 146, 0  };
    cmap[74]  = new int[] {0  , 148, 0  };
    cmap[75]  = new int[] {0  , 150, 0  };
    cmap[76]  = new int[] {0  , 152, 0  };
    cmap[77]  = new int[] {0  , 154, 0  };
    cmap[78]  = new int[] {0  , 156, 0  };
    cmap[79]  = new int[] {0  , 158, 0  };
    cmap[80]  = new int[] {0  , 160, 0  };
    cmap[81]  = new int[] {0  , 162, 0  };
    cmap[82]  = new int[] {0  , 164, 0  };
    cmap[83]  = new int[] {0  , 166, 0  };
    cmap[84]  = new int[] {0  , 168, 0  };
    cmap[85]  = new int[] {0  , 170, 0  };
    cmap[86]  = new int[] {0  , 172, 0  };
    cmap[87]  = new int[] {0  , 174, 0  };
    cmap[88]  = new int[] {0  , 176, 0  };
    cmap[89]  = new int[] {0  , 178, 0  };
    cmap[90]  = new int[] {0  , 180, 0  };
    cmap[91]  = new int[] {0  , 182, 0  };
    cmap[92]  = new int[] {0  , 184, 0  };
    cmap[93]  = new int[] {0  , 186, 0  };
    cmap[94]  = new int[] {0  , 188, 0  };
    cmap[95]  = new int[] {0  , 190, 0  };
    cmap[96]  = new int[] {0  , 192, 0  };
    cmap[97]  = new int[] {0  , 194, 0  };
    cmap[98]  = new int[] {0  , 196, 0  };
    cmap[99]  = new int[] {0  , 198, 0  };
    cmap[100] = new int[] {0  , 200, 0  };
    cmap[101] = new int[] {0  , 202, 0  };
    cmap[102] = new int[] {0  , 204, 0  };
    cmap[103] = new int[] {0  , 206, 0  };
    cmap[104] = new int[] {0  , 208, 0  };
    cmap[105] = new int[] {0  , 210, 0  };
    cmap[106] = new int[] {0  , 212, 0  };
    cmap[107] = new int[] {0  , 214, 0  };
    cmap[108] = new int[] {0  , 216, 0  };
    cmap[109] = new int[] {0  , 218, 0  };
    cmap[110] = new int[] {0  , 220, 0  };
    cmap[111] = new int[] {0  , 222, 0  };
    cmap[112] = new int[] {0  , 224, 0  };
    cmap[113] = new int[] {0  , 226, 0  };
    cmap[114] = new int[] {0  , 228, 0  };
    cmap[115] = new int[] {0  , 230, 0  };
    cmap[116] = new int[] {0  , 232, 0  };
    cmap[117] = new int[] {0  , 234, 0  };
    cmap[118] = new int[] {0  , 236, 0  };
    cmap[119] = new int[] {0  , 238, 0  };
    cmap[120] = new int[] {0  , 240, 0  };
    cmap[121] = new int[] {0  , 242, 0  };
    cmap[122] = new int[] {0  , 244, 0  };
    cmap[123] = new int[] {0  , 246, 0  };
    cmap[124] = new int[] {0  , 248, 0  };
    cmap[125] = new int[] {0  , 250, 0  };
    cmap[126] = new int[] {0  , 252, 0  };
    cmap[127] = new int[] {0  , 254, 0  };
    cmap[128] = new int[] {2  , 255, 2  };
    cmap[129] = new int[] {4  , 255, 4  };
    cmap[130] = new int[] {6  , 255, 6  };
    cmap[131] = new int[] {8  , 255, 8  };
    cmap[132] = new int[] {10 , 255, 10 };
    cmap[133] = new int[] {12 , 255, 12 };
    cmap[134] = new int[] {14 , 255, 14 };
    cmap[135] = new int[] {16 , 255, 16 };
    cmap[136] = new int[] {18 , 255, 18 };
    cmap[137] = new int[] {20 , 255, 20 };
    cmap[138] = new int[] {22 , 255, 22 };
    cmap[139] = new int[] {24 , 255, 24 };
    cmap[140] = new int[] {26 , 255, 26 };
    cmap[141] = new int[] {28 , 255, 28 };
    cmap[142] = new int[] {30 , 255, 30 };
    cmap[143] = new int[] {32 , 255, 32 };
    cmap[144] = new int[] {34 , 255, 34 };
    cmap[145] = new int[] {36 , 255, 36 };
    cmap[146] = new int[] {38 , 255, 38 };
    cmap[147] = new int[] {40 , 255, 40 };
    cmap[148] = new int[] {42 , 255, 42 };
    cmap[149] = new int[] {44 , 255, 44 };
    cmap[150] = new int[] {46 , 255, 46 };
    cmap[151] = new int[] {48 , 255, 48 };
    cmap[152] = new int[] {50 , 255, 50 };
    cmap[153] = new int[] {52 , 255, 52 };
    cmap[154] = new int[] {54 , 255, 54 };
    cmap[155] = new int[] {56 , 255, 56 };
    cmap[156] = new int[] {58 , 255, 58 };
    cmap[157] = new int[] {60 , 255, 60 };
    cmap[158] = new int[] {62 , 255, 62 };
    cmap[159] = new int[] {64 , 255, 64 };
    cmap[160] = new int[] {66 , 255, 66 };
    cmap[161] = new int[] {68 , 255, 68 };
    cmap[162] = new int[] {70 , 255, 70 };
    cmap[163] = new int[] {72 , 255, 72 };
    cmap[164] = new int[] {74 , 255, 74 };
    cmap[165] = new int[] {76 , 255, 76 };
    cmap[166] = new int[] {78 , 255, 78 };
    cmap[167] = new int[] {80 , 255, 80 };
    cmap[168] = new int[] {82 , 255, 82 };
    cmap[169] = new int[] {84 , 255, 84 };
    cmap[170] = new int[] {86 , 255, 86 };
    cmap[171] = new int[] {88 , 255, 88 };
    cmap[172] = new int[] {90 , 255, 90 };
    cmap[173] = new int[] {92 , 255, 92 };
    cmap[174] = new int[] {94 , 255, 94 };
    cmap[175] = new int[] {96 , 255, 96 };
    cmap[176] = new int[] {98 , 255, 98 };
    cmap[177] = new int[] {100, 255, 100};
    cmap[178] = new int[] {102, 255, 102};
    cmap[179] = new int[] {104, 255, 104};
    cmap[180] = new int[] {106, 255, 106};
    cmap[181] = new int[] {108, 255, 108};
    cmap[182] = new int[] {110, 255, 110};
    cmap[183] = new int[] {112, 255, 112};
    cmap[184] = new int[] {114, 255, 114};
    cmap[185] = new int[] {116, 255, 116};
    cmap[186] = new int[] {118, 255, 118};
    cmap[187] = new int[] {120, 255, 120};
    cmap[188] = new int[] {122, 255, 122};
    cmap[189] = new int[] {124, 255, 124};
    cmap[190] = new int[] {126, 255, 126};
    cmap[191] = new int[] {128, 255, 128};
    cmap[192] = new int[] {130, 255, 130};
    cmap[193] = new int[] {132, 255, 132};
    cmap[194] = new int[] {134, 255, 134};
    cmap[195] = new int[] {136, 255, 136};
    cmap[196] = new int[] {138, 255, 138};
    cmap[197] = new int[] {140, 255, 140};
    cmap[198] = new int[] {142, 255, 142};
    cmap[199] = new int[] {144, 255, 144};
    cmap[200] = new int[] {146, 255, 146};
    cmap[201] = new int[] {148, 255, 148};
    cmap[202] = new int[] {150, 255, 150};
    cmap[203] = new int[] {152, 255, 152};
    cmap[204] = new int[] {154, 255, 154};
    cmap[205] = new int[] {156, 255, 156};
    cmap[206] = new int[] {158, 255, 158};
    cmap[207] = new int[] {160, 255, 160};
    cmap[208] = new int[] {162, 255, 162};
    cmap[209] = new int[] {164, 255, 164};
    cmap[210] = new int[] {166, 255, 166};
    cmap[211] = new int[] {168, 255, 168};
    cmap[212] = new int[] {170, 255, 170};
    cmap[213] = new int[] {172, 255, 172};
    cmap[214] = new int[] {174, 255, 174};
    cmap[215] = new int[] {176, 255, 176};
    cmap[216] = new int[] {178, 255, 178};
    cmap[217] = new int[] {180, 255, 180};
    cmap[218] = new int[] {182, 255, 182};
    cmap[219] = new int[] {184, 255, 184};
    cmap[220] = new int[] {186, 255, 186};
    cmap[221] = new int[] {188, 255, 188};
    cmap[222] = new int[] {190, 255, 190};
    cmap[223] = new int[] {192, 255, 192};
    cmap[224] = new int[] {194, 255, 194};
    cmap[225] = new int[] {196, 255, 196};
    cmap[226] = new int[] {198, 255, 198};
    cmap[227] = new int[] {200, 255, 200};
    cmap[228] = new int[] {202, 255, 202};
    cmap[229] = new int[] {204, 255, 204};
    cmap[230] = new int[] {206, 255, 206};
    cmap[231] = new int[] {208, 255, 208};
    cmap[232] = new int[] {210, 255, 210};
    cmap[233] = new int[] {212, 255, 212};
    cmap[234] = new int[] {214, 255, 214};
    cmap[235] = new int[] {216, 255, 216};
    cmap[236] = new int[] {218, 255, 218};
    cmap[237] = new int[] {220, 255, 220};
    cmap[238] = new int[] {222, 255, 222};
    cmap[239] = new int[] {224, 255, 224};
    cmap[240] = new int[] {226, 255, 226};
    cmap[241] = new int[] {228, 255, 228};
    cmap[242] = new int[] {230, 255, 230};
    cmap[243] = new int[] {232, 255, 232};
    cmap[244] = new int[] {234, 255, 234};
    cmap[245] = new int[] {236, 255, 236};
    cmap[246] = new int[] {238, 255, 238};
    cmap[247] = new int[] {240, 255, 240};
    cmap[248] = new int[] {242, 255, 242};
    cmap[249] = new int[] {244, 255, 244};
    cmap[250] = new int[] {246, 255, 246};
    cmap[251] = new int[] {248, 255, 248};
    cmap[252] = new int[] {250, 255, 250};
    cmap[253] = new int[] {252, 255, 252};
    cmap[254] = new int[] {254, 255, 254};
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
