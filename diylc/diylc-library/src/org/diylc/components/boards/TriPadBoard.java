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
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import org.diylc.common.OrientationHV;
import org.diylc.common.SimpleComponentTransformer;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.KeywordPolicy;
import org.diylc.core.annotations.PositiveNonZeroMeasureValidator;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "TriPad Board", category = "Boards", author = "Hauke Juhls", zOrder = IDIYComponent.BOARD,
    instanceNamePrefix = "Board",
    description = "Perforated FR4 board with copper strips connecting 3 holes in a row (aka TriPad Board)",
    keywordPolicy = KeywordPolicy.SHOW_TYPE_NAME, transformer = SimpleComponentTransformer.class)
public class TriPadBoard extends AbstractBoard {

  private static final long serialVersionUID = 1L;

  public static Color BORDER_COLOR = BOARD_COLOR.darker();

  public static Size SPACING = new Size(0.1d, SizeUnit.in);
  public static Size STRIP_SIZE = new Size(0.07d, SizeUnit.in);
  public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm);

  protected int stripSpan = 3; // determines how many holes are covered by a
  // strip
  protected Size spacing = SPACING;
  protected Color stripColor = COPPER_COLOR;
  protected OrientationHV orientation = OrientationHV.HORIZONTAL;

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
      Composite oldComposite = g2d.getComposite();
      if (alpha < MAX_ALPHA) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA));
      }
      Point p = new Point(firstPoint);
      int stripSize = getClosestOdd((int) STRIP_SIZE.convertToPixels());
      int holeSize = getClosestOdd((int) HOLE_SIZE.convertToPixels());
      int spacing = (int) this.spacing.convertToPixels();

      if (orientation == OrientationHV.HORIZONTAL) {
        while (p.y < secondPoint.y - spacing) {
          p.x = firstPoint.x;
          p.y += spacing;

          while (p.x + spacing < secondPoint.x) {

            int remainingSpace = secondPoint.x - p.x;
            int spacesToDraw = stripSpan;

            if (remainingSpace < (stripSize + (stripSpan * spacing))) {
              spacesToDraw = (remainingSpace - stripSize) / spacing;
            }

            g2d.setColor(stripColor);
            drawingObserver.startTrackingContinuityArea(true);  
            g2d.fillRect(p.x + spacing - stripSize / 2, p.y - stripSize / 2, spacing * (spacesToDraw - 1) + stripSize,
                stripSize);
            drawingObserver.stopTrackingContinuityArea();
            g2d.setColor(stripColor.darker());

            g2d.drawRect(p.x + spacing - stripSize / 2, p.y - stripSize / 2, spacing * (spacesToDraw - 1) + stripSize,
                stripSize);

            p.x += spacing * spacesToDraw;
          }

          // draw holes
          p.x = firstPoint.x;

          while (p.x < secondPoint.x - spacing - holeSize) {
            p.x += spacing;
            g2d.setColor(Constants.CANVAS_COLOR);
            g2d.fillOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize);
            g2d.setColor(stripColor.darker());
            g2d.drawOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize);
          }
        }
      } else {
        while (p.x < secondPoint.x - spacing) {
          p.x += spacing;
          p.y = firstPoint.y;

          while (p.y + spacing < secondPoint.y) {

            int remainingSpace = secondPoint.y - p.y;
            int spacesToDraw = stripSpan;

            if (remainingSpace < (stripSize + (stripSpan * spacing))) {
              spacesToDraw = (remainingSpace - stripSize) / spacing;
            }

            g2d.setColor(stripColor);
            drawingObserver.startTrackingContinuityArea(true);  
            g2d.fillRect(p.x - stripSize / 2, p.y + spacing - stripSize / 2, stripSize, spacing * (spacesToDraw - 1)
                + stripSize);
            drawingObserver.stopTrackingContinuityArea();
            g2d.setColor(stripColor.darker());
            g2d.drawRect(p.x - stripSize / 2, p.y + spacing - stripSize / 2, stripSize, spacing * (spacesToDraw - 1)
                + stripSize);

            p.y += spacing * spacesToDraw;
          }

          // draw holes
          p.y = firstPoint.y;

          while (p.y < secondPoint.y - spacing - holeSize) {
            p.y += spacing;
            g2d.setColor(Constants.CANVAS_COLOR);
            g2d.fillOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize);
            g2d.setColor(stripColor.darker());
            g2d.drawOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize);
          }
        }
      }
      g2d.setComposite(oldComposite);

      super.drawCoordinates(g2d, spacing, project);
    }
  }

  @EditableProperty(name = "Strip Color")
  public Color getStripColor() {
    return stripColor;
  }

  public void setStripColor(Color padColor) {
    this.stripColor = padColor;
  }

  @EditableProperty(validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getSpacing() {
    return spacing;
  }

  public void setSpacing(Size spacing) {
    this.spacing = spacing;
  }

  @EditableProperty
  public OrientationHV getOrientation() {
    return orientation;
  }

  public void setOrientation(OrientationHV orientation) {
    this.orientation = orientation;
  }

  @Override
  public void drawIcon(Graphics2D g2d, int width, int height) {
    g2d.setColor(BOARD_COLOR);
    g2d.fillRect(0, 0, width, height);

    final int horizontalSpacing = width / 5;
    final int horizontalIndent = horizontalSpacing / 2;

    final int verticalSpacing = height / 5;
    final int verticalIndent = verticalSpacing / 2;

    for (int row = 0; row < 5; row++) {
      g2d.setColor(COPPER_COLOR);
      g2d.fillRect(0, row * verticalSpacing + 2, horizontalIndent / 2 + horizontalSpacing, verticalSpacing - 1);

      g2d.setColor(COPPER_COLOR);
      g2d.fillRect(horizontalSpacing + 2, row * verticalSpacing + 2, horizontalSpacing * 3 - 1, verticalSpacing - 1);

      g2d.fillRect(horizontalSpacing * 4 + 2, row * verticalSpacing + 2, horizontalIndent / 2 + horizontalSpacing,
          verticalSpacing - 1);
    }

    // draw dots
    for (int row = 0; row < 5; row++) {
      int y = (verticalSpacing * row) + verticalIndent;
      for (int col = 0; col < 5; col++) {
        int x = (horizontalSpacing * col) + horizontalIndent;
        g2d.setColor(Constants.CANVAS_COLOR);
        g2d.fillOval(x, y, 2, 2);
        g2d.setColor(COPPER_COLOR.darker());
        g2d.drawOval(x, y, 2, 2);
      }
    }
  }

  @EditableProperty(name = "Holes per strip")
  public int getStripSpan() {
    return stripSpan;
  }

  public void setStripSpan(int stripSpan) {
    if (stripSpan < 1) {
      this.stripSpan = 1;
    } else {
      this.stripSpan = stripSpan;
    }
  }
}
