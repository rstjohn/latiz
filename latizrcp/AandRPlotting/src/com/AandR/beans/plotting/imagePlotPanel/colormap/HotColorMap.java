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
public class HotColorMap extends AbstractColorMap {

  private int[][] cmap = new int[256][3];
  
  public HotColorMap() {
    cmap[0]  = new int[]  {3,0,0};         
    cmap[1]  = new int[]  {5,0,0};
    cmap[2]  = new int[]  {8,0,0};
    cmap[3]  = new int[]  {11,0,0};          
    cmap[4]  = new int[]  {13,0,0};
    cmap[5]  = new int[]  {16,0,0};           
    cmap[6]  = new int[]  {19,0,0};
    cmap[7]  = new int[]  {21,0,0};
    cmap[8]  = new int[]  {24,0,0};
    cmap[9]  = new int[]  {27,0,0};
    cmap[10] = new int[]  {29,0,0};
    cmap[11] = new int[]  {32,0,0};
    cmap[12] = new int[]  {35,0,0};
    cmap[13] = new int[]  {37,0,0};
    cmap[14] = new int[]  {40,0,0};
    cmap[15] = new int[]  {43,0,0};
    cmap[16] = new int[]  {45,0,0};
    cmap[17] = new int[]  {48,0,0};
    cmap[18] = new int[]  {50,0,0};
    cmap[19] = new int[]  {53,0,0};
    cmap[20] = new int[]  {56,0,0};
    cmap[21] = new int[]  {58,0,0};
    cmap[22] = new int[]  {61,0,0};
    cmap[23] = new int[]  {64,0,0};
    cmap[24] = new int[]  {66,0,0};
    cmap[25] = new int[]  {69,0,0};
    cmap[26] = new int[]  {72,0,0};
    cmap[27] = new int[]  {74,0,0};
    cmap[28] = new int[]  {77,0,0};
    cmap[29] = new int[]  {80,0,0};
    cmap[30] = new int[]  {82,0,0};
    cmap[31] = new int[]  {85,0,0};
    cmap[32] = new int[]  {88,0,0};
    cmap[33] = new int[]  {90,0,0};
    cmap[34] = new int[]  {93,0,0};
    cmap[35] = new int[]  {96,0,0};
    cmap[36] = new int[]  {98,0,0};
    cmap[37] = new int[] {101,0,0};
    cmap[38] = new int[] {104,0,0};
    cmap[39] = new int[] {106,0,0};
    cmap[40] = new int[] {109,0,0};
    cmap[41] = new int[] {112,0,0};
    cmap[42] = new int[] {114,0,0};
    cmap[43] = new int[] {117,0,0};
    cmap[44] = new int[] {120,0,0};
    cmap[45] = new int[] {122,0,0};
    cmap[46] = new int[] {125,0,0};
    cmap[47] = new int[] {128,0,0};
    cmap[48] = new int[] {130,0,0};
    cmap[49] = new int[] {133,0,0};
    cmap[50] = new int[] {135,0,0};
    cmap[51] = new int[] {138,0,0};
    cmap[52] = new int[] {141,0,0};
    cmap[53] = new int[] {143,0,0};
    cmap[54] = new int[] {146,0,0};
    cmap[55] = new int[] {149,0,0};
    cmap[56] = new int[] {151,0,0};
    cmap[57] = new int[] {154,0,0};
    cmap[58] = new int[] {157,0,0};
    cmap[59] = new int[] {159,0,0};
    cmap[60] = new int[] {162,0,0};
    cmap[61] = new int[] {165,0,0};
    cmap[62] = new int[] {167,0,0};
    cmap[63] = new int[] {170,0,0};
    cmap[64] = new int[] {173,0,0};
    cmap[65] = new int[] {175,0,0};
    cmap[66] = new int[] {178,0,0};
    cmap[67] = new int[] {181,0,0};
    cmap[68] = new int[] {183,0,0};
    cmap[69] = new int[] {186,0,0};
    cmap[70] = new int[] {189,0,0};
    cmap[71] = new int[] {191,0,0};
    cmap[72] = new int[] {194,0,0};
    cmap[73] = new int[] {197,0,0};
    cmap[74] = new int[] {199,0,0};
    cmap[75] = new int[] {202,0,0};
    cmap[76] = new int[] {205,0,0};
    cmap[77] = new int[] {207,0,0};
    cmap[78] = new int[] {210,0,0};
    cmap[79] = new int[] {212,0,0};
    cmap[80] = new int[] {215,0,0};
    cmap[81] = new int[] {218,0,0};
    cmap[82] = new int[] {220,0,0};
    cmap[83] = new int[] {223,0,0};
    cmap[84] = new int[] {226,0,0};
    cmap[85] = new int[] {228,0,0};
    cmap[86] = new int[] {231,0,0};
    cmap[87] = new int[] {234,0,0};
    cmap[88] = new int[] {236,0,0};
    cmap[89] = new int[] {239,0,0};
    cmap[90] = new int[] {242,0,0};
    cmap[91] = new int[] {244,0,0};
    cmap[92] = new int[] {247,0,0};
    cmap[93] = new int[] {250,0,0};
    cmap[94] = new int[] {252,0,0};
    cmap[95] = new int[] {255,0,0};
    cmap[96] = new int[] {255,3,0};
    cmap[97] = new int[] {255,5,0};
    cmap[98] = new int[] {255,8,0};
    cmap[99] = new int[] {255,11,0};
    cmap[100]= new int[] {255,13,0};
    cmap[101]= new int[] {255,16,0};
    cmap[102]= new int[] {255,19,0};
    cmap[103]= new int[] {255,21,0};
    cmap[104]= new int[] {255,24,0};
    cmap[105]= new int[] {255,27,0};
    cmap[106]= new int[] {255,29,0};
    cmap[107]= new int[] {255,32,0};
    cmap[108]= new int[] {255,35,0};
    cmap[109]= new int[] {255,37,0};
    cmap[110]= new int[] {255,40,0};
    cmap[111]= new int[] {255,43,0};
    cmap[112]= new int[] {255,45,0};
    cmap[113]= new int[] {255,48,0};
    cmap[114]= new int[] {255,50,0};
    cmap[115]= new int[] {255,53,0};
    cmap[116]= new int[] {255,56,0};
    cmap[117]= new int[] {255,58,0};
    cmap[118]= new int[] {255,61,0};
    cmap[119]= new int[] {255,64,0};
    cmap[120]= new int[] {255,66,0};
    cmap[121]= new int[] {255,69,0};
    cmap[122]= new int[] {255,72,0};
    cmap[123]= new int[] {255,74,0};
    cmap[124]= new int[] {255,77,0};
    cmap[125]= new int[] {255,80,0};
    cmap[126]= new int[] {255,82,0};
    cmap[127]= new int[] {255,85,0};
    cmap[128]= new int[] {255,88,0};
    cmap[129]= new int[] {255,90,0};
    cmap[130]= new int[] {255,93,0};
    cmap[131]= new int[] {255,96,0};
    cmap[132]= new int[] {255,98,0};
    cmap[133]= new int[] {255,101,0};
    cmap[134]= new int[] {255,104,0};
    cmap[135]= new int[] {255,106,0};
    cmap[136]= new int[] {255,109,0};
    cmap[137]= new int[] {255,112,0};
    cmap[138]= new int[] {255,114,0};
    cmap[139]= new int[] {255,117,0};
    cmap[140]= new int[] {255,120,0};
    cmap[141]= new int[] {255,122,0};
    cmap[142]= new int[] {255,125,0};
    cmap[143]= new int[] {255,128,0};
    cmap[144]= new int[] {255,130,0};
    cmap[145]= new int[] {255,133,0};
    cmap[146]= new int[] {255,135,0};
    cmap[147]= new int[] {255,138,0};
    cmap[148]= new int[] {255,141,0};
    cmap[149]= new int[] {255,143,0};
    cmap[150]= new int[] {255,146,0};
    cmap[151]= new int[] {255,149,0};
    cmap[152]= new int[] {255,151,0};
    cmap[153]= new int[] {255,154,0};
    cmap[154]= new int[] {255,157,0};
    cmap[155]= new int[] {255,159,0};
    cmap[156]= new int[] {255,162,0};
    cmap[157]= new int[] {255,165,0};
    cmap[158]= new int[] {255,167,0};
    cmap[159]= new int[] {255,170,0};
    cmap[160]= new int[] {255,173,0};
    cmap[161]= new int[] {255,175,0};
    cmap[162]= new int[] {255,178,0};
    cmap[163]= new int[] {255,181,0};
    cmap[164]= new int[] {255,183,0};
    cmap[165]= new int[] {255,186,0};
    cmap[166]= new int[] {255,189,0};
    cmap[167]= new int[] {255,191,0};
    cmap[168]= new int[] {255,194,0};
    cmap[169]= new int[] {255,197,0};
    cmap[170]= new int[] {255,199,0};
    cmap[171]= new int[] {255,202,0};
    cmap[172]= new int[] {255,205,0};
    cmap[173]= new int[] {255,207,0};
    cmap[174]= new int[] {255,210,0};
    cmap[175]= new int[] {255,212,0};
    cmap[176]= new int[] {255,215,0};
    cmap[177]= new int[] {255,218,0};
    cmap[178]= new int[] {255,220,0};
    cmap[179]= new int[] {255,223,0};
    cmap[180]= new int[] {255,226,0};
    cmap[181]= new int[] {255,228,0};
    cmap[182]= new int[] {255,231,0};
    cmap[183]= new int[] {255,234,0};
    cmap[184]= new int[] {255,236,0};
    cmap[185]= new int[] {255,239,0};
    cmap[186]= new int[] {255,242,0};
    cmap[187]= new int[] {255,244,0};
    cmap[188]= new int[] {255,247,0};
    cmap[189]= new int[] {255,250,0};
    cmap[190]= new int[] {255,252,0};
    cmap[191]= new int[] {255,255,0};
    cmap[192]= new int[] {255,255,4};
    cmap[193]= new int[] {255,255,8};
    cmap[194]= new int[] {255,255,12};
    cmap[195]= new int[] {255,255,16};
    cmap[196]= new int[] {255,255,20};
    cmap[197]= new int[] {255,255,24};
    cmap[198]= new int[] {255,255,28};
    cmap[199]= new int[] {255,255,32};
    cmap[200]= new int[] {255,255,36};
    cmap[201]= new int[] {255,255,40};
    cmap[202]= new int[] {255,255,44};
    cmap[203]= new int[] {255,255,48};
    cmap[204]= new int[] {255,255,52};
    cmap[205]= new int[] {255,255,56};
    cmap[206]= new int[] {255,255,60};
    cmap[207]= new int[] {255,255,64};
    cmap[208]= new int[] {255,255,68};
    cmap[209]= new int[] {255,255,72};
    cmap[210]= new int[] {255,255,76};
    cmap[211]= new int[] {255,255,80};
    cmap[212]= new int[] {255,255,84};
    cmap[213]= new int[] {255,255,88};
    cmap[214]= new int[] {255,255,92};
    cmap[215]= new int[] {255,255,96};
    cmap[216]= new int[] {255,255,100};
    cmap[217]= new int[] {255,255,104};
    cmap[218]= new int[] {255,255,108};
    cmap[219]= new int[] {255,255,112};
    cmap[220]= new int[] {255,255,116};
    cmap[221]= new int[] {255,255,120};
    cmap[222]= new int[] {255,255,124};
    cmap[223]= new int[] {255,255,128};
    cmap[224]= new int[] {255,255,131};
    cmap[225]= new int[] {255,255,135};
    cmap[226]= new int[] {255,255,139};
    cmap[227]= new int[] {255,255,143};
    cmap[228]= new int[] {255,255,147};
    cmap[229]= new int[] {255,255,151};
    cmap[230]= new int[] {255,255,155};
    cmap[231]= new int[] {255,255,159};
    cmap[232]= new int[] {255,255,163};
    cmap[233]= new int[] {255,255,167};
    cmap[234]= new int[] {255,255,171};
    cmap[235]= new int[] {255,255,175};
    cmap[236]= new int[] {255,255,179};
    cmap[237]= new int[] {255,255,183};
    cmap[238]= new int[] {255,255,187};
    cmap[239]= new int[] {255,255,191};
    cmap[240]= new int[] {255,255,195};
    cmap[241]= new int[] {255,255,199};
    cmap[242]= new int[] {255,255,203};
    cmap[243]= new int[] {255,255,207};
    cmap[244]= new int[] {255,255,211};
    cmap[245]= new int[] {255,255,215};
    cmap[246]= new int[] {255,255,219};
    cmap[247]= new int[] {255,255,223};
    cmap[248]= new int[] {255,255,227};
    cmap[249]= new int[] {255,255,231};
    cmap[250]= new int[] {255,255,235};
    cmap[251]= new int[] {255,255,239};
    cmap[252]= new int[] {255,255,243};
    cmap[253]= new int[] {255,255,247};
    cmap[254]= new int[] {255,255,251};
    cmap[255]= new int[] {255,255,255};
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
