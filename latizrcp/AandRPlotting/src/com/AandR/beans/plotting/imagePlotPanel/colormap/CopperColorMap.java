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
public class CopperColorMap extends AbstractColorMap {

  private int[][] cmap = new int[256][3];

  public CopperColorMap() {
    cmap[0] = new int[] {0, 0, 0};
    cmap[1] = new int[] {1, 1, 1};
    cmap[2] = new int[] {2, 2, 1};
    cmap[3] = new int[] {4, 2, 2};
    cmap[4] = new int[] {5, 3, 2};
    cmap[5] = new int[] {6, 4, 2};
    cmap[6] = new int[] {7, 5, 3};
    cmap[7] = new int[] {9, 5, 3};
    cmap[8] = new int[] {10, 6, 4};
    cmap[9] = new int[] {11, 7, 4};
    cmap[10] = new int[] {12, 8, 5};
    cmap[11] = new int[] {14, 9, 5};
    cmap[12] = new int[] {15, 9, 6};
    cmap[13] = new int[] {16, 10, 6};
    cmap[14] = new int[] {17, 11, 7};
    cmap[15] = new int[] {19, 12, 7};
    cmap[16] = new int[] {20, 12, 8};
    cmap[17] = new int[] {21, 13, 8};
    cmap[18] = new int[] {22, 14, 9};
    cmap[19] = new int[] {24, 15, 9};
    cmap[20] = new int[] {25, 16, 10};
    cmap[21] = new int[] {26, 16, 10};
    cmap[22] = new int[] {27, 17, 11};
    cmap[23] = new int[] {29, 18, 11};
    cmap[24] = new int[] {30, 19, 12};
    cmap[25] = new int[] {31, 20, 12};
    cmap[26] = new int[] {33, 20, 13};
    cmap[27] = new int[] {34, 21, 13};
    cmap[28] = new int[] {35, 22, 14};
    cmap[29] = new int[] {36, 23, 14};
    cmap[30] = new int[] {38, 23, 15};
    cmap[31] = new int[] {39, 24, 15};
    cmap[32] = new int[] {40, 25, 16};
    cmap[33] = new int[] {41, 26, 16};
    cmap[34] = new int[] {43, 27, 17};
    cmap[35] = new int[] {44, 27, 17};
    cmap[36] = new int[] {45, 28, 18};
    cmap[37] = new int[] {46, 29, 18};
    cmap[38] = new int[] {48, 30, 19};
    cmap[39] = new int[] {49, 30, 19};
    cmap[40] = new int[] {50, 31, 20};
    cmap[41] = new int[] {51, 32, 20};
    cmap[42] = new int[] {53, 33, 21};
    cmap[43] = new int[] {54, 34, 21};
    cmap[44] = new int[] {55, 34, 22};
    cmap[45] = new int[] {56, 35, 22};
    cmap[46] = new int[] {58, 36, 23};
    cmap[47] = new int[] {59, 37, 23};
    cmap[48] = new int[] {60, 37, 24};
    cmap[49] = new int[] {61, 38, 24};
    cmap[50] = new int[] {63, 39, 25};
    cmap[51] = new int[] {64, 40, 25};
    cmap[52] = new int[] {65, 41, 26};
    cmap[53] = new int[] {66, 41, 26};
    cmap[54] = new int[] {67, 42, 27};
    cmap[55] = new int[] {69, 43, 27};
    cmap[56] = new int[] {70, 44, 28};
    cmap[57] = new int[] {71, 45, 28};
    cmap[58] = new int[] {72, 45, 29};
    cmap[59] = new int[] {74, 46, 29};
    cmap[60] = new int[] {75, 47, 30};
    cmap[61] = new int[] {76, 48, 30};
    cmap[62] = new int[] {77, 48, 31};
    cmap[63] = new int[] {79, 49, 31};
    cmap[64] = new int[] {80, 50, 32};
    cmap[65] = new int[] {81, 51, 32};
    cmap[66] = new int[] {82, 52, 33};
    cmap[67] = new int[] {84, 52, 33};
    cmap[68] = new int[] {85, 53, 34};
    cmap[69] = new int[] {86, 54, 34};
    cmap[70] = new int[] {87, 55, 35};
    cmap[71] = new int[] {89, 55, 35};
    cmap[72] = new int[] {90, 56, 36};
    cmap[73] = new int[] {91, 57, 36};
    cmap[74] = new int[] {92, 58, 37};
    cmap[75] = new int[] {94, 59, 37};
    cmap[76] = new int[] {95, 59, 38};
    cmap[77] = new int[] {96, 60, 38};
    cmap[78] = new int[] {98, 61, 39};
    cmap[79] = new int[] {99, 62, 39};
    cmap[80] = new int[] {100, 63, 40};
    cmap[81] = new int[] {101, 63, 40};
    cmap[82] = new int[] {103, 64, 41};
    cmap[83] = new int[] {104, 65, 41};
    cmap[84] = new int[] {105, 66, 42};
    cmap[85] = new int[] {106, 66, 42};
    cmap[86] = new int[] {108, 67, 43};
    cmap[87] = new int[] {109, 68, 43};
    cmap[88] = new int[] {110, 69, 44};
    cmap[89] = new int[] {111, 70, 44};
    cmap[90] = new int[] {113, 70, 45};
    cmap[91] = new int[] {114, 71, 45};
    cmap[92] = new int[] {115, 72, 46};
    cmap[93] = new int[] {116, 73, 46};
    cmap[94] = new int[] {118, 73, 47};
    cmap[95] = new int[] {119, 74, 47};
    cmap[96] = new int[] {120, 75, 48};
    cmap[97] = new int[] {121, 76, 48};
    cmap[98] = new int[] {123, 77, 49};
    cmap[99] = new int[] {124, 77, 49};
    cmap[100] = new int[] {125, 78, 50};
    cmap[101] = new int[] {126, 79, 50};
    cmap[102] = new int[] {128, 80, 51};
    cmap[103] = new int[] {129, 80, 51};
    cmap[104] = new int[] {130, 81, 52};
    cmap[105] = new int[] {131, 82, 52};
    cmap[106] = new int[] {132, 83, 53};
    cmap[107] = new int[] {134, 84, 53};
    cmap[108] = new int[] {135, 84, 54};
    cmap[109] = new int[] {136, 85, 54};
    cmap[110] = new int[] {137, 86, 55};
    cmap[111] = new int[] {139, 87, 55};
    cmap[112] = new int[] {140, 87, 56};
    cmap[113] = new int[] {141, 88, 56};
    cmap[114] = new int[] {142, 89, 57};
    cmap[115] = new int[] {144, 90, 57};
    cmap[116] = new int[] {145, 91, 58};
    cmap[117] = new int[] {146, 91, 58};
    cmap[118] = new int[] {147, 92, 59};
    cmap[119] = new int[] {149, 93, 59};
    cmap[120] = new int[] {150, 94, 60};
    cmap[121] = new int[] {151, 95, 60};
    cmap[122] = new int[] {152, 95, 61};
    cmap[123] = new int[] {154, 96, 61};
    cmap[124] = new int[] {155, 97, 62};
    cmap[125] = new int[] {156, 98, 62};
    cmap[126] = new int[] {157, 98, 63};
    cmap[127] = new int[] {159, 99, 63};
    cmap[128] = new int[] {160, 100, 64};
    cmap[129] = new int[] {161, 101, 64};
    cmap[130] = new int[] {163, 102, 65};
    cmap[131] = new int[] {164, 102, 65};
    cmap[132] = new int[] {165, 103, 66};
    cmap[133] = new int[] {166, 104, 66};
    cmap[134] = new int[] {168, 105, 67};
    cmap[135] = new int[] {169, 105, 67};
    cmap[136] = new int[] {170, 106, 68};
    cmap[137] = new int[] {171, 107, 68};
    cmap[138] = new int[] {173, 108, 69};
    cmap[139] = new int[] {174, 109, 69};
    cmap[140] = new int[] {175, 109, 70};
    cmap[141] = new int[] {176, 110, 70};
    cmap[142] = new int[] {178, 111, 71};
    cmap[143] = new int[] {179, 112, 71};
    cmap[144] = new int[] {180, 112, 72};
    cmap[145] = new int[] {181, 113, 72};
    cmap[146] = new int[] {183, 114, 73};
    cmap[147] = new int[] {184, 115, 73};
    cmap[148] = new int[] {185, 116, 74};
    cmap[149] = new int[] {186, 116, 74};
    cmap[150] = new int[] {188, 117, 75};
    cmap[151] = new int[] {189, 118, 75};
    cmap[152] = new int[] {190, 119, 76};
    cmap[153] = new int[] {191, 120, 76};
    cmap[154] = new int[] {192, 120, 77};
    cmap[155] = new int[] {194, 121, 77};
    cmap[156] = new int[] {195, 122, 78};
    cmap[157] = new int[] {196, 123, 78};
    cmap[158] = new int[] {197, 123, 79};
    cmap[159] = new int[] {199, 124, 79};
    cmap[160] = new int[] {200, 125, 80};
    cmap[161] = new int[] {201, 126, 80};
    cmap[162] = new int[] {202, 127, 81};
    cmap[163] = new int[] {204, 127, 81};
    cmap[164] = new int[] {205, 128, 82};
    cmap[165] = new int[] {206, 129, 82};
    cmap[166] = new int[] {207, 130, 83};
    cmap[167] = new int[] {209, 130, 83};
    cmap[168] = new int[] {210, 131, 84};
    cmap[169] = new int[] {211, 132, 84};
    cmap[170] = new int[] {212, 133, 85};
    cmap[171] = new int[] {214, 134, 85};
    cmap[172] = new int[] {215, 134, 86};
    cmap[173] = new int[] {216, 135, 86};
    cmap[174] = new int[] {217, 136, 87};
    cmap[175] = new int[] {219, 137, 87};
    cmap[176] = new int[] {220, 137, 88};
    cmap[177] = new int[] {221, 138, 88};
    cmap[178] = new int[] {222, 139, 89};
    cmap[179] = new int[] {224, 140, 89};
    cmap[180] = new int[] {225, 141, 90};
    cmap[181] = new int[] {226, 141, 90};
    cmap[182] = new int[] {228, 142, 91};
    cmap[183] = new int[] {229, 143, 91};
    cmap[184] = new int[] {230, 144, 92};
    cmap[185] = new int[] {231, 145, 92};
    cmap[186] = new int[] {233, 145, 93};
    cmap[187] = new int[] {234, 146, 93};
    cmap[188] = new int[] {235, 147, 94};
    cmap[189] = new int[] {236, 148, 94};
    cmap[190] = new int[] {238, 148, 95};
    cmap[191] = new int[] {239, 149, 95};
    cmap[192] = new int[] {240, 150, 96};
    cmap[193] = new int[] {241, 151, 96};
    cmap[194] = new int[] {243, 152, 97};
    cmap[195] = new int[] {244, 152, 97};
    cmap[196] = new int[] {245, 153, 98};
    cmap[197] = new int[] {246, 154, 98};
    cmap[198] = new int[] {248, 155, 99};
    cmap[199] = new int[] {249, 155, 99};
    cmap[200] = new int[] {250, 156, 100};
    cmap[201] = new int[] {251, 157, 100};
    cmap[202] = new int[] {253, 158, 100};
    cmap[203] = new int[] {254, 159, 101};
    cmap[204] = new int[] {255, 159, 101};
    cmap[205] = new int[] {255, 160, 102};
    cmap[206] = new int[] {255, 161, 102};
    cmap[207] = new int[] {255, 162, 103};
    cmap[208] = new int[] {255, 162, 103};
    cmap[209] = new int[] {255, 163, 104};
    cmap[210] = new int[] {255, 164, 104};
    cmap[211] = new int[] {255, 165, 105};
    cmap[212] = new int[] {255, 166, 105};
    cmap[213] = new int[] {255, 166, 106};
    cmap[214] = new int[] {255, 167, 106};
    cmap[215] = new int[] {255, 168, 107};
    cmap[216] = new int[] {255, 169, 107};
    cmap[217] = new int[] {255, 170, 108};
    cmap[218] = new int[] {255, 170, 108};
    cmap[219] = new int[] {255, 171, 109};
    cmap[220] = new int[] {255, 172, 109};
    cmap[221] = new int[] {255, 173, 110};
    cmap[222] = new int[] {255, 173, 110};
    cmap[223] = new int[] {255, 174, 111};
    cmap[224] = new int[] {255, 175, 111};
    cmap[225] = new int[] {255, 176, 112};
    cmap[226] = new int[] {255, 177, 112};
    cmap[227] = new int[] {255, 177, 113};
    cmap[228] = new int[] {255, 178, 113};
    cmap[229] = new int[] {255, 179, 114};
    cmap[230] = new int[] {255, 180, 114};
    cmap[231] = new int[] {255, 180, 115};
    cmap[232] = new int[] {255, 181, 115};
    cmap[233] = new int[] {255, 182, 116};
    cmap[234] = new int[] {255, 183, 116};
    cmap[235] = new int[] {255, 184, 117};
    cmap[236] = new int[] {255, 184, 117};
    cmap[237] = new int[] {255, 185, 118};
    cmap[238] = new int[] {255, 186, 118};
    cmap[239] = new int[] {255, 187, 119};
    cmap[240] = new int[] {255, 187, 119};
    cmap[241] = new int[] {255, 188, 120};
    cmap[242] = new int[] {255, 189, 120};
    cmap[243] = new int[] {255, 190, 121};
    cmap[244] = new int[] {255, 191, 121};
    cmap[245] = new int[] {255, 191, 122};
    cmap[246] = new int[] {255, 192, 122};
    cmap[247] = new int[] {255, 193, 123};
    cmap[248] = new int[] {255, 194, 123};
    cmap[249] = new int[] {255, 195, 124};
    cmap[250] = new int[] {255, 195, 124};
    cmap[251] = new int[] {255, 196, 125};
    cmap[252] = new int[] {255, 197, 125};
    cmap[253] = new int[] {255, 198, 126};
    cmap[254] = new int[] {255, 198, 126};
    cmap[255] = new int[] {255, 199, 127};
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
