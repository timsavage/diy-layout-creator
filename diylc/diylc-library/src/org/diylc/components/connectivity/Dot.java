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
package org.diylc.components.connectivity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.diylc.common.SimpleComponentTransformer;
import org.diylc.components.AbstractComponent;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.PositiveNonZeroMeasureValidator;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Dot", category = "Connectivity", author = "Branislav Stojkovic",
    description = "Connector dot", instanceNamePrefix = "Dot", stretchable = false, zOrder = IDIYComponent.COMPONENT,
    bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false, transformer = SimpleComponentTransformer.class)
public class Dot extends AbstractComponent<Void> {

  private static final long serialVersionUID = 1L;

  public static Size SIZE = new Size(1d, SizeUnit.mm);
  public static Color COLOR = Color.black;

  private Size size = SIZE;
  private Color color = COLOR;
  private Point point = new Point(0, 0);

  @Override
  public void draw(Graphics2D g2d, ComponentState componentState, boolean outlineMode, Project project,
      IDrawingObserver drawingObserver) {
    if (checkPointsClipped(g2d.getClip())) {
      return;
    }
    int diameter = getClosestOdd((int) getSize().convertToPixels());
    g2d.setColor(componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
        : color);
    g2d.fillOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter);
  }

  @Override
  public void drawIcon(Graphics2D g2d, int width, int height) {
    int diameter = 7 * width / 32;
    g2d.setColor(COLOR);
    g2d.fillOval((width - diameter) / 2, (height - diameter) / 2, diameter, diameter);
  }

  @EditableProperty(validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  @Override
  public String getName() {
    return super.getName();
  }

  @Override
  public int getControlPointCount() {
    return 1;
  }

  @Override
  public boolean isControlPointSticky(int index) {
    return true;
  }

  @Override
  public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
    return VisibilityPolicy.NEVER;
  }

  @Override
  public Point getControlPoint(int index) {
    return point;
  }

  @Override
  public void setControlPoint(Point point, int index) {
    this.point.setLocation(point);
  }

  @EditableProperty(name = "Color")
  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public Void getValue() {
    return null;
  }

  @Override
  public void setValue(Void value) {}
}
