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
package com.AandR.jepLibrary;

import org.nfunk.jep.JEP;

/**
 * @author  <a href="mailto://rstjohn@mza.com">Dr. Richard St. John</a>
 * @company <a href="http://www.mza.com">MZA Associates Corporation</a>
 */
public class LatizJep extends JEP {

  public LatizJep() {
    super();
    initFunTab();
    initSymTab();
    addStandardFunctions();
    addStandardConstants();
    addFunction("ele", new Ele());
    setAllowUndeclared(false);
    //addComplex();
    setAllowAssignment(true);
    setTraverse(false);
  }
}