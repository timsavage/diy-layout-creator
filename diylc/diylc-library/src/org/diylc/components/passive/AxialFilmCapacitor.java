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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.diylc.common.SimpleComponentTransformer;
import org.diylc.components.AbstractLeadedComponent;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.PositiveMeasureValidator;
import org.diylc.core.measures.Capacitance;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Film Capacitor (Axial)", author = "Branislav Stojkovic", category = "Passive",
    creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C",
    description = "Axial film capacitor, similar to Mallory 150s", zOrder = IDIYComponent.COMPONENT,
    transformer = SimpleComponentTransformer.class)
public class AxialFilmCapacitor extends AbstractLeadedComponent<Capacitance> {

  private static final long serialVersionUID = 1L;

  public static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in);
  public static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in);
  public static Color BODY_COLOR = Color.decode("#FFE303");
  public static Color BORDER_COLOR = BODY_COLOR.darker();

  private Capacitance value = null;
  @Deprecated
  private Voltage voltage = Voltage._63V;
  private org.diylc.core.measures.Voltage voltageNew = null;

  public AxialFilmCapacitor() {
    super();
    this.bodyColor = BODY_COLOR;
    this.borderColor = BORDER_COLOR;
  }

  @Override
  protected boolean supportsStandingMode() {
    return true;
  }

  @EditableProperty(validatorClass = PositiveMeasureValidator.class)
  public Capacitance getValue() {
    return value;
  }

  public void setValue(Capacitance value) {
    this.value = value;
  }

  @Deprecated
  public Voltage getVoltage() {
    return voltage;
  }

  @Deprecated
  public void setVoltage(Voltage voltage) {
    this.voltage = voltage;
  }

  @Override
  public String getValueForDisplay() {
    return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString());
  }

  @EditableProperty(name = "Voltage")
  public org.diylc.core.measures.Voltage getVoltageNew() {
    return voltageNew;
  }

  public void setVoltageNew(org.diylc.core.measures.Voltage voltageNew) {
    this.voltageNew = voltageNew;
  }

  public void drawIcon(Graphics2D g2d, int width, int height) {
    g2d.rotate(-Math.PI / 4, width / 2, height / 2);
    g2d.setColor(LEAD_COLOR_ICON);
    g2d.drawLine(0, height / 2, width, height / 2);
    g2d.setColor(BODY_COLOR);
    g2d.fillRect(4, height / 2 - 3, width - 8, 6);
    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(4, height / 2 - 3, width - 8, 6);
  }

  @Override
  protected Size getDefaultWidth() {
    return DEFAULT_HEIGHT;
  }

  @Override
  protected Size getDefaultLength() {
    return DEFAULT_WIDTH;
  }

  @Override
  protected Shape getBodyShape() {
    return new Rectangle2D.Double(0f, 0f, getLength().convertToPixels(), getClosestOdd(getWidth().convertToPixels()));
  }

  @EditableProperty(name = "Reverse (standing)")
  public boolean getFlipStanding() {
    return super.getFlipStanding();
  }
}
