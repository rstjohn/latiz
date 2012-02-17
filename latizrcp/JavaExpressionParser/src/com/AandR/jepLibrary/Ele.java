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

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * ele(x,i) returns the i-th element of a vector x. ele(m,[i,j]) returns the (i-th,j-th) element of a matrix m. Note this follows the mathematical
 * indexing convention with indices starting from 1 rather than the computer science convention with indices starting from 0. Hence <code>
 * a = [1,2,3,4];
 * ele(a,1); // returns 1
 * </code>
 */
public class Ele extends PostfixMathCommand {

    public Ele() {
        super();
        numberOfParameters = 2;
    }

    
    @Override
    @SuppressWarnings(value="unchecked")
    public void run(Stack inStack) throws ParseException {

        checkStack(inStack);
        Object arg2 = inStack.pop();
        Object arg1 = inStack.pop();

        if (arg1 instanceof Vector) {
            Vector v = (Vector) arg1;

            if (arg2 instanceof Number) {
                int index = ((Number) arg2).intValue() - 1;

                Object val = v.get(index);
                inStack.push(val);
            }
        }
    }
}
