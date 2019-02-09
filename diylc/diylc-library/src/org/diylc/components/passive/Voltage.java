/*

    DIY Layout Creator (DIYLC).
    Copyright (c) 2009-2018 held jointly by the individual authors.

    This file is part of DIYLC.

    DIYLC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DIYLC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DIYLC.  If not, see <http://www.gnu.org/licenses/>.

*/
package org.diylc.components.passive;

import org.diylc.core.measures.VoltageUnit;

public enum Voltage {

  _16V, _25V, _63V, _100V, _160V, _250V, _300V, _350V, _400V, _500V, _630V, _1KV;

  @Override
  public String toString() {
    return name().replace("_", "");
  }

  public org.diylc.core.measures.Voltage convertToNewFormat() {
    org.diylc.core.measures.Voltage voltageNew;
    switch (this) {
      case _100V:
        voltageNew = new org.diylc.core.measures.Voltage(100d, VoltageUnit.V);
        break;
      case _160V:
        voltageNew = new org.diylc.core.measures.Voltage(160d, VoltageUnit.V);
        break;
      case _16V:
        voltageNew = new org.diylc.core.measures.Voltage(16d, VoltageUnit.V);
        break;
      case _1KV:
        voltageNew = new org.diylc.core.measures.Voltage(1d, VoltageUnit.KV);
        break;
      case _250V:
        voltageNew = new org.diylc.core.measures.Voltage(250d, VoltageUnit.V);
        break;
      case _25V:
        voltageNew = new org.diylc.core.measures.Voltage(25d, VoltageUnit.V);
        break;
      case _300V:
        voltageNew = new org.diylc.core.measures.Voltage(300d, VoltageUnit.V);
        break;
      case _350V:
        voltageNew = new org.diylc.core.measures.Voltage(350d, VoltageUnit.V);
        break;
      case _400V:
        voltageNew = new org.diylc.core.measures.Voltage(400d, VoltageUnit.V);
        break;
      case _500V:
        voltageNew = new org.diylc.core.measures.Voltage(500d, VoltageUnit.V);
        break;
      case _630V:
        voltageNew = new org.diylc.core.measures.Voltage(630d, VoltageUnit.V);
        break;
      case _63V:
        voltageNew = new org.diylc.core.measures.Voltage(63d, VoltageUnit.V);
        break;
      default:
        voltageNew = new org.diylc.core.measures.Voltage(100d, VoltageUnit.V);
    }
    return voltageNew;
  }
}
