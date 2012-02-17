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
package com.AandR.beans.plotting.imagePlotPanel;


/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.2 $, $Date: 2007/09/05 18:38:19 $
 */
public interface CanvasPanelListener {
  abstract public void bufferChanged(); 
  abstract public void mouseDragged();
  abstract public void mouseMoved();
  abstract public void newDataLoaded();
  abstract public void dataChanged();
}
