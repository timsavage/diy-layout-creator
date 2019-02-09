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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import org.diylc.common.SimpleComponentTransformer;
import org.diylc.components.AbstractSchematicLeadedSymbol;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.KeywordPolicy;
import org.diylc.core.annotations.PositiveMeasureValidator;
import org.diylc.core.measures.Capacitance;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Capacitor", author = "Branislav Stojkovic", category = "Schematic Symbols",
    creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C",
    description = "Capacitor schematic symbol with an optional polarity sign", zOrder = IDIYComponent.COMPONENT,
    keywordPolicy = KeywordPolicy.SHOW_TAG, keywordTag = "Schematic", transformer = SimpleComponentTransformer.class)
public class CapacitorSymbol extends AbstractSchematicLeadedSymbol<Capacitance> {

  private static final long serialVersionUID = 1L;

  public static Size DEFAULT_LENGTH = new Size(0.05, SizeUnit.in);
  public static Size DEFAULT_WIDTH = new Size(0.15, SizeUnit.in);

  private Capacitance value = null;
  @Deprecated
  private Voltage voltage = Voltage._63V;
  private org.diylc.core.measures.Voltage voltageNew = null;
  private boolean polarized = false;

  @EditableProperty(validatorClass = PositiveMeasureValidator.class)
  public Capacitance getValue() {
    return value;
  }

  public void setValue(Capacitance value) {
    this.value = value;
  }

  @Override
  public String getValueForDisplay() {
    return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString());
  }

  @Deprecated
  public Voltage getVoltage() {
    return voltage;
  }

  @Deprecated
  public void setVoltage(Voltage voltage) {
    this.voltage = voltage;
  }

  @EditableProperty(name = "Voltage")
  public org.diylc.core.measures.Voltage getVoltageNew() {
    return voltageNew;
  }

  public void setVoltageNew(org.diylc.core.measures.Voltage voltageNew) {
    this.voltageNew = voltageNew;
  }

  @EditableProperty
  public boolean getPolarized() {
    return polarized;
  }

  public void setPolarized(boolean polarized) {
    this.polarized = polarized;
  }

  public void drawIcon(Graphics2D g2d, int width, int height) {
    g2d.rotate(-Math.PI / 4, width / 2, height / 2);
    g2d.setColor(LEAD_COLOR);
    g2d.drawLine(0, height / 2, 13, height / 2);
    g2d.drawLine(width - 13, height / 2, width, height / 2);
    g2d.setColor(COLOR);
    g2d.drawLine(14, height / 2 - 6, 14, height / 2 + 6);
    g2d.drawLine(width - 14, height / 2 - 6, width - 14, height / 2 + 6);
  }

  @Override
  protected Size getDefaultWidth() {
    return DEFAULT_WIDTH;
  }

  @Override
  protected Size getDefaultLength() {
    return DEFAULT_LENGTH;
  }

  @Override
  protected Shape getBodyShape() {
    GeneralPath polyline = new GeneralPath();
    double length = getLength().convertToPixels();
    double width = getWidth().convertToPixels();
    polyline.moveTo(0, 0);
    polyline.lineTo(0, width);
    polyline.moveTo(length, 0);
    polyline.lineTo(length, width);
    return polyline;
  }

  @Override
  protected void decorateComponentBody(Graphics2D g2d, boolean outlineMode) {
    if (polarized) {
      // Draw + sign.
      g2d.setColor(getBorderColor());
      int plusSize = getClosestOdd(getWidth().convertToPixels() / 4);
      int x = -plusSize;
      int y = plusSize;
      g2d.drawLine(x - plusSize / 2, y, x + plusSize / 2, y);
      g2d.drawLine(x, y - plusSize / 2, x, y + plusSize / 2);
    }
  }
}
