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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import org.diylc.appframework.miscutils.ConfigurationManager;
import org.diylc.common.IPlugInPort;
import org.diylc.common.ObjectCache;
import org.diylc.common.Orientation;
import org.diylc.components.AbstractTransparentComponent;
import org.diylc.components.transform.TerminalStripTransformer;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.KeywordPolicy;
import org.diylc.core.annotations.PositiveNonZeroMeasureValidator;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "Terminal Strip", author = "Branislav Stojkovic", category = "Boards",
    instanceNamePrefix = "TS", description = "Row of terminals for point-to-point construction", stretchable = false,
    zOrder = IDIYComponent.BOARD, keywordPolicy = KeywordPolicy.SHOW_TYPE_NAME, transformer = TerminalStripTransformer.class)
public class TerminalStrip extends AbstractTransparentComponent<String> {

  private static final long serialVersionUID = 1L;

  public static Color BOARD_COLOR = Color.decode("#CD8500");
  public static Color BORDER_COLOR = BOARD_COLOR.darker();
  public static Color TERMINAL_COLOR = Color.lightGray;
  public static Color TERMINAL_BORDER_COLOR = TERMINAL_COLOR.darker();
  public static int EDGE_RADIUS = 2;
  public static Size HOLE_SIZE = new Size(0.06d, SizeUnit.in);
  public static Size MOUNTING_HOLE_SIZE = new Size(0.07d, SizeUnit.in);

  private String value = "";
  private Orientation orientation = Orientation.DEFAULT;
  private int terminalCount = 10;
  private Size boardWidth = new Size(0.35d, SizeUnit.in);
  private Size terminalSpacing = new Size(0.25d, SizeUnit.in);
  private Size holeSpacing = new Size(0.5d, SizeUnit.in);
  private Point[] controlPoints = new Point[] {new Point(0, 0)};
  private Color boardColor = BOARD_COLOR;
  private Color borderColor = BORDER_COLOR;
  private boolean centerHole = false;

  transient private Area[] body;

  public TerminalStrip() {
    super();
    updateControlPoints();
  }

  @EditableProperty
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @EditableProperty
  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
    updateControlPoints();
    // Reset body shape.
    body = null;
  }

  @EditableProperty(name = "Terminals")
  public int getTerminalCount() {
    return terminalCount;
  }

  public void setTerminalCount(int terminalCount) {
    this.terminalCount = terminalCount;
    updateControlPoints();
    // Reset body shape;
    body = null;
  }

  @EditableProperty(name = "Terminal Spacing", validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getTerminalSpacing() {
    return terminalSpacing;
  }

  public void setTerminalSpacing(Size pinSpacing) {
    this.terminalSpacing = pinSpacing;
    updateControlPoints();
    // Reset body shape;
    body = null;
  }

  @EditableProperty(name = "Hole Spacing", validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getHoleSpacing() {
    return holeSpacing;
  }

  public void setHoleSpacing(Size rowSpacing) {
    this.holeSpacing = rowSpacing;
    updateControlPoints();
    // Reset body shape;
    body = null;
  }

  @EditableProperty(name = "Center Terminal")
  public boolean getCenterHole() {
    return centerHole;
  }

  public void setCenterHole(boolean centerHole) {
    this.centerHole = centerHole;
    updateControlPoints();
    // Reset body shape;
    body = null;
  }

  @EditableProperty(name = "Board Width", validatorClass = PositiveNonZeroMeasureValidator.class)
  public Size getBoardWidth() {
    return boardWidth;
  }

  public void setBoardWidth(Size boardWidth) {
    this.boardWidth = boardWidth;
    // Reset body shape;
    body = null;
  }

  @Override
  public int getControlPointCount() {
    return controlPoints.length;
  }

  @Override
  public Point getControlPoint(int index) {
    return controlPoints[index];
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
  public void setControlPoint(Point point, int index) {
    controlPoints[index].setLocation(point);
    body = null;
  }

  private void updateControlPoints() {
    Point firstPoint = controlPoints[0];
    controlPoints = new Point[getTerminalCount() * (getCenterHole() ? 3 : 2)];
    controlPoints[0] = firstPoint;
    int pinSpacing = (int) this.terminalSpacing.convertToPixels();
    int rowSpacing = (int) this.holeSpacing.convertToPixels();
    // Update control points.
    int dx1;
    int dy1;
    int dx2;
    int dy2;
    int cx;
    int cy;
    for (int i = 0; i < getTerminalCount(); i++) {
      switch (orientation) {
        case DEFAULT:
          dx1 = 0;
          dy1 = i * pinSpacing;
          dx2 = rowSpacing;
          dy2 = i * pinSpacing;
          cx = rowSpacing / 2;
          cy = i * pinSpacing;
          break;
        case _90:
          dx1 = -i * pinSpacing;
          dy1 = 0;
          dx2 = -i * pinSpacing;
          dy2 = rowSpacing;
          cx = -i * pinSpacing;
          cy = rowSpacing / 2;
          break;
        case _180:
          dx1 = 0;
          dy1 = -i * pinSpacing;
          dx2 = -rowSpacing;
          dy2 = -i * pinSpacing;
          cx = -rowSpacing / 2;
          cy = -i * pinSpacing;
          break;
        case _270:
          dx1 = i * pinSpacing;
          dy1 = 0;
          dx2 = i * pinSpacing;
          dy2 = -rowSpacing;
          cx = i * pinSpacing;
          cy = -rowSpacing / 2;
          break;
        default:
          throw new RuntimeException("Unexpected orientation: " + orientation);
      }
      controlPoints[i] = new Point(firstPoint.x + dx1, firstPoint.y + dy1);
      controlPoints[i + getTerminalCount()] = new Point(firstPoint.x + dx2, firstPoint.y + dy2);
      if (centerHole)
        controlPoints[i + 2 * getTerminalCount()] = new Point(firstPoint.x + cx, firstPoint.y + cy);
    }
  }

  public Area[] getBody() {
    if (body == null) {
      body = new Area[2];
      int x = controlPoints[0].x;
      int y = controlPoints[0].y;
      int width;
      int height;
      int holeSize = (int) HOLE_SIZE.convertToPixels();
      int terminalSpacing = (int) getTerminalSpacing().convertToPixels();
      int holeSpacing = (int) getHoleSpacing().convertToPixels();
      int boardWidth = (int) getBoardWidth().convertToPixels();
      int boardLength = (getTerminalCount() - 1) * terminalSpacing + 2 * boardWidth;
      Area indentation = null;
      Area terminals = null;
      int mountingHoleSize = getClosestOdd(MOUNTING_HOLE_SIZE.convertToPixels());
      switch (orientation) {
        case DEFAULT:
          width = boardWidth;
          height = boardLength;
          x += holeSpacing / 2 - boardWidth / 2;
          y -= boardWidth;
          indentation =
              new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize / 2, y + boardWidth / 2
                  - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize));
          indentation.add(new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize / 2, y + boardLength
              - boardWidth / 2, mountingHoleSize, mountingHoleSize)));
          break;
        case _90:
          width = boardLength;
          height = boardWidth;
          x += boardWidth - boardLength;
          y += holeSpacing / 2 - boardWidth / 2;
          indentation =
              new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize, y + boardWidth / 2
                  - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize));
          indentation.add(new Area(new Ellipse2D.Double(x + boardLength - boardWidth / 2, y + boardWidth / 2
              - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize)));
          break;
        case _180:
          width = boardWidth;
          height = boardLength;
          x -= holeSpacing / 2 + boardWidth / 2;
          y += boardWidth - boardLength;
          indentation =
              new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize / 2, y + boardWidth / 2
                  - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize));
          indentation.add(new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize / 2, y + boardLength
              - boardWidth / 2, mountingHoleSize, mountingHoleSize)));
          break;
        case _270:
          width = boardLength;
          height = boardWidth;
          x -= boardWidth;
          y -= holeSpacing / 2 + boardWidth / 2;
          indentation =
              new Area(new Ellipse2D.Double(x + boardWidth / 2 - mountingHoleSize, y + boardWidth / 2
                  - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize));
          indentation.add(new Area(new Ellipse2D.Double(x + boardLength - boardWidth / 2, y + boardWidth / 2
              - mountingHoleSize / 2, mountingHoleSize, mountingHoleSize)));
          break;
        default:
          throw new RuntimeException("Unexpected orientation: " + orientation);
      }
      Area bodyArea = new Area(new RoundRectangle2D.Double(x, y, width, height, EDGE_RADIUS, EDGE_RADIUS));
      bodyArea.subtract(indentation);
      body[0] = bodyArea;

      terminals = new Area();
      for (int i = 0; i < getTerminalCount(); i++) {
        Point p1 = getControlPoint(i);
        Point p2 = getControlPoint(i + getTerminalCount());
        if (p2.x < p1.x || p2.y < p1.y) {
          Point p = p1;
          p1 = p2;
          p2 = p;
        }

        Area terminal =
            new Area(new RoundRectangle2D.Double(p1.x - holeSize, p1.y - holeSize, p2.x - p1.x + holeSize * 2, p2.y
                - p1.y + holeSize * 2, holeSize, holeSize));

        terminal.subtract(new Area(new Ellipse2D.Double(p1.x - holeSize / 2, p1.y - holeSize / 2, holeSize, holeSize)));
        terminal.subtract(new Area(new Ellipse2D.Double(p2.x - holeSize / 2, p2.y - holeSize / 2, holeSize, holeSize)));
        if (centerHole) {
          Point p3 = getControlPoint(i + 2 * getTerminalCount());
          Area centerHole =
              new Area(new Ellipse2D.Double(p3.x - holeSize / 2, p3.y - holeSize / 2, holeSize, holeSize));
          terminal.subtract(centerHole);
          bodyArea.subtract(centerHole);
        }

        terminals.add(terminal);
      }
      body[1] = terminals;
    }
    return body;
  }

  @Override
  public void draw(Graphics2D g2d, ComponentState componentState, boolean outlineMode, Project project,
      IDrawingObserver drawingObserver) {
    if (checkPointsClipped(g2d.getClip())) {
      return;
    }
    Area mainArea = getBody()[0];
    Area terminalArea = getBody()[1];

    Composite oldComposite = g2d.getComposite();
    if (alpha < MAX_ALPHA) {
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA));
    }
    g2d.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : getBoardColor());
    g2d.fill(mainArea);

    Color finalBorderColor;
    if (outlineMode) {
      Theme theme =
          (Theme) ConfigurationManager.getInstance().readObject(IPlugInPort.THEME_KEY, Constants.DEFAULT_THEME);
      finalBorderColor =
          componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
              : theme.getOutlineColor();
    } else {
      finalBorderColor =
          componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
              : getBorderColor();
    }
    g2d.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));

    g2d.setColor(finalBorderColor);
    g2d.draw(mainArea);

    g2d.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : TERMINAL_COLOR);
    drawingObserver.startTrackingContinuityArea(true);  
    g2d.fill(terminalArea);
    drawingObserver.stopTrackingContinuityArea();

    Color finalTerminalBorderColor;
    if (outlineMode) {
      Theme theme =
          (Theme) ConfigurationManager.getInstance().readObject(IPlugInPort.THEME_KEY, Constants.DEFAULT_THEME);
      finalTerminalBorderColor =
          componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
              : theme.getOutlineColor();
    } else {
      finalTerminalBorderColor =
          componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
              : TERMINAL_BORDER_COLOR;
    }

    g2d.setColor(finalTerminalBorderColor);
    g2d.draw(terminalArea);

    g2d.setComposite(oldComposite);
  }

  @Override
  public void drawIcon(Graphics2D g2d, int width, int height) {
    int radius = 6 * width / 32;
    int holeSize = 3 * width / 32;
    g2d.setColor(BOARD_COLOR);
    g2d.fillRect(width / 4, 1, width / 2, height - 4);
    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(width / 4, 1, width / 2, height - 4);
    int terminalSize = getClosestOdd(height / 5);
    Area terminal =
        new Area(new RoundRectangle2D.Double(2 * width / 32, height / 5, width - 4 * width / 32, terminalSize, radius,
            radius));
    terminal.subtract(new Area(new Ellipse2D.Double(2 * width / 32 + holeSize, height * 3 / 10 - holeSize / 2, holeSize, holeSize)));
    terminal.subtract(new Area(new Ellipse2D.Double(width - 2 * width / 32 - holeSize * 2, height * 3 / 10 - holeSize / 2, holeSize, holeSize)));
    
    g2d.setColor(TERMINAL_COLOR);
    g2d.fill(terminal);
    g2d.setColor(TERMINAL_BORDER_COLOR);
    g2d.draw(terminal);
    g2d.translate(0, height * 2 / 5);
    g2d.setColor(TERMINAL_COLOR);
    g2d.fill(terminal);
    g2d.setColor(TERMINAL_BORDER_COLOR);
    g2d.draw(terminal);
  }

  @EditableProperty(name = "Board")
  public Color getBoardColor() {
    if (boardColor == null) {
      boardColor = BOARD_COLOR;
    }
    return boardColor;
  }

  public void setBoardColor(Color bodyColor) {
    this.boardColor = bodyColor;
  }

  @EditableProperty(name = "Border")
  public Color getBorderColor() {
    if (borderColor == null) {
      borderColor = BORDER_COLOR;
    }
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }
}
