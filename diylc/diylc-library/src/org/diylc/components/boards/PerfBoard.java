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
package org.diylc.components.boards;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import org.diylc.common.SimpleComponentTransformer;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.KeywordPolicy;
import org.diylc.core.annotations.PositiveNonZeroMeasureValidator;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "Perf Board w/ Pads", category = "Boards", author = "Branislav Stojkovic",
    zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with solder pads",
    bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false, keywordPolicy = KeywordPolicy.SHOW_TAG,
    keywordTag = "Perf Board", transformer = SimpleComponentTransformer.class)
public class PerfBoard extends AbstractBoard {

  private static final long serialVersionUID = 1L;

  public static Color COPPER_COLOR = Color.decode("#DA8A67");

  public static Size SPACING = new Size(0.1d, SizeUnit.in);
  public static Size PAD_SIZE = new Size(0.08d, SizeUnit.in);
  public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm);

  // private Area copperArea;
  protected Size spacing = SPACING;
  protected Color padColor = COPPER_COLOR;

  @Override
  public void draw(Graphics2D g2d, ComponentState componentState, boolean outlineMode, Project project,
      IDrawingObserver drawingObserver) {
    Shape clip = g2d.getClip();
    if (checkPointsClipped(clip) && !clip.contains(firstPoint.x, secondPoint.y)
        && !clip.contains(secondPoint.x, firstPoint.y)) {
      return;
    }
    super.draw(g2d, componentState, outlineMode, project, drawingObserver);
    if (componentState != ComponentState.DRAGGING) {
      if (alpha < MAX_ALPHA) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA));
      }
      Point p = new Point(firstPoint);
      int diameter = getClosestOdd((int) PAD_SIZE.convertToPixels());
      int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels());
      int spacing = (int) this.spacing.convertToPixels();

      while (p.y < secondPoint.y - spacing) {
        p.x = firstPoint.x;
        p.y += spacing;
        while (p.x < secondPoint.x - spacing - diameter) {
          p.x += spacing;
          g2d.setColor(padColor);
          g2d.fillOval(p.x - diameter / 2, p.y - diameter / 2, diameter, diameter);
          g2d.setColor(padColor.darker());
          g2d.drawOval(p.x - diameter / 2, p.y - diameter / 2, diameter, diameter);
          g2d.setColor(Constants.CANVAS_COLOR);
          g2d.fillOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, holeDiameter);
          g2d.setColor(padColor.darker());
          g2d.drawOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, holeDiameter);
        }
      }
      super.drawCoordinates(g2d, spacing, project);
    }
  }

  @EditableProperty(name = "Pad color")
  public Color getPadColor() {
    return padColor;
  }

  public void setPadColor(Color padColor) {
    this.padColor = padColor;
  }

  @EditableProperty(validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getSpacing() {
    return spacing;
  }

  public void setSpacing(Size spacing) {
    this.spacing = spacing;
  }

  @Override
  public void drawIcon(Graphics2D g2d, int width, int height) {
    int factor = 32 / width;
    g2d.setColor(BOARD_COLOR);
    g2d.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor);
    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor);
    g2d.setColor(COPPER_COLOR);
    g2d.fillOval(width / 4, width / 4, width / 2, width / 2);
    g2d.setColor(COPPER_COLOR.darker());
    g2d.drawOval(width / 4, width / 4, width / 2, width / 2);
    g2d.setColor(Constants.CANVAS_COLOR);
    g2d.fillOval(width / 2 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor),
        getClosestOdd(5.0 / factor));
    g2d.setColor(COPPER_COLOR.darker());
    g2d.drawOval(width / 2 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor),
        getClosestOdd(5.0 / factor));
  }
}
