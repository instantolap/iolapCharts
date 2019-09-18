package com.instantolap.charts.impl.chart;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.data.PartialCube;
import com.instantolap.charts.impl.legend.BasicLegendImpl;
import com.instantolap.charts.impl.legend.LegendImpl;
import com.instantolap.charts.renderer.*;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicChartImpl implements Chart, HasAnimation, RendererListener {

  private final Theme theme;
  private final BasicLegendImpl legend;
  private final List<Content> contents = new ArrayList<>();
  private boolean isPopup = false;
  private boolean isInteractive = true;
  private ChartColor background;
  private ChartColor foreground;
  private ChartFont font;
  private String title, subTitle;
  private ChartFont titleFont;
  private ChartFont subTitleFont;
  private Data data;
  private double insetTop = 10;
  private double insetBottom = 10;
  private double insetLeft = 10;
  private double insetRight = 10;
  private Renderer renderer;
  private boolean animationEnabled = true;
  private long animationTime = 1000;
  private double titlePadding = 10;
  private boolean showLegend = false;
  private int legendPosition = SOUTH;
  private int legendAlignment = CENTER;
  private boolean legendInside = false;
  private double legendSpacing = 10;
  private double lastProgress;
  private Double contentInsetTop, contentInsetLeft, contentInsetRight, contentInsetBottom;
  private ChartColor titleColor, subTitleColor;
  private LinkOpener linkOpener;
  private double canvasX, canvasY, canvasWidth, canvasHeight;
  private String timezoneId;

  public BasicChartImpl(Theme theme) {
    this.theme = theme;
    this.legend = new LegendImpl(theme);
    this.animationEnabled = theme.isAnimationEnabled();
  }

  public Theme getTheme() {
    return theme;
  }

  @Override
  public boolean isPopup() {
    return isPopup;
  }

  @Override
  public void setPopup(boolean popup) {
    this.isPopup = popup;
  }

  @Override
  public void render() throws ChartException {
    buildCubes();

    final Renderer r = getRenderer();
    r.animate(this, (isPopup || !animationEnabled) ? 0 : animationTime);
  }

  @Override
  public void setInteractive(boolean isInteractive) {
    this.isInteractive = isInteractive;
  }

  protected void buildCubes() {
    buildContentCubes(getData().getCurrentCube());
  }

  @Override
  public boolean isInteractive() {
    return isInteractive;
  }

  public Renderer getRenderer() {
    return renderer;
  }

  @Override
  public void addContent(Content content) {
    contents.add(content);
  }

  @Override
  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
    this.lastProgress = 0;
    renderer.addListener(this);
  }

  @Override
  public List<Content> getContents() {
    return contents;
  }

  @Override
  public void renderUnanimated() throws ChartException {
    buildCubes();
    render(1);
  }

  @Override
  public void setAnimationEnabled(boolean enabled) {
    this.animationEnabled = enabled;
  }

  @Override
  public ChartColor getBackground() {
    return background != null ? background : getTheme().getBackground();
  }

  @Override
  public boolean isAnimationEnabled() {
    return animationEnabled;
  }

  @Override
  public void setBackground(ChartColor background) {
    this.background = background;
  }

  @Override
  public void setAnimationTime(long animationTime) {
    this.animationTime = animationTime;
  }

  protected abstract void render(double progress, double x, double y, double width,
                                 double height) throws ChartException;

  @Override
  public long getAnimationTime() {
    return animationTime;
  }

  protected boolean needsSampleLegend() {
    for (Content c : contents) {
      if (c.needsSampleLegend()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void repaint(boolean buildCubes) throws ChartException {
    if (buildCubes) {
      buildCubes();
    }

    render(lastProgress);
  }

  protected void buildContentCubes(Cube cube) {
    for (Content content : getContents()) {

      // build subcube?
      final int[] serieses = content.getDisplaySamples(1);
      if (serieses != null) {
        final PartialCube contentCube = new PartialCube(cube);
        contentCube.keepVisible(1, serieses, false);
        content.setCube(contentCube);
      } else {
        content.setCube(cube);
      }
    }
  }

  @Override
  public void setForeground(ChartColor color) {
    this.foreground = color;
  }

  @Override
  public void openLink(String url, String target) {
    if (linkOpener != null) {
      linkOpener.openLink(url, target);
    }
  }

  @Override
  public ChartColor getForeground() {
    return foreground == null ? theme.getTextColor() : foreground;
  }

  protected Double coalesce(Double... a) {
    for (Double v : a) {
      if (v != null) {
        return v;
      }
    }
    return null;
  }

  @Override
  public void setFont(ChartFont font) {
    this.font = font;
  }

  protected void setCanvasArea(double x, double y, double w, double h) {
    this.canvasX = x;
    this.canvasY = y;
    this.canvasWidth = w;
    this.canvasHeight = h;
  }

  @Override
  public ChartFont getFont() {
    return font != null ? font : getTheme().getDefaultFont();
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitleFont(ChartFont font) {
    this.titleFont = font;
  }

  @Override
  public ChartFont getTitleFont() {
    return titleFont != null ? titleFont : getTheme().getTitleFont();
  }

  @Override
  public void setTitleColor(ChartColor color) {
    this.titleColor = color;
  }

  @Override
  public ChartColor getTitleColor() {
    return titleColor;
  }

  @Override
  public void setSubTitle(String title) {
    this.subTitle = title;
  }

  @Override
  public String getSubTitle() {
    return subTitle;
  }

  @Override
  public void setSubTitleFont(ChartFont font) {
    this.subTitleFont = font;
  }

  @Override
  public ChartFont getSubTitleFont() {
    return subTitleFont != null ? subTitleFont : getTheme().getSubTitleFont();
  }

  @Override
  public void setSubTitleColor(ChartColor color) {
    this.subTitleColor = color;
  }

  @Override
  public ChartColor getSubTitleColor() {
    return subTitleColor;
  }

  @Override
  public void setTitlePadding(double titlePadding) {
    this.titlePadding = titlePadding;
  }

  @Override
  public double getTitlePadding() {
    return titlePadding;
  }

  @Override
  public void setData(Data data) {
    this.data = data;
  }

  @Override
  public Data getData() {
    return data;
  }

  @Override
  public void setInsets(double top, double left, double right, double bottom) {
    this.insetTop = top;
    this.insetLeft = left;
    this.insetRight = right;
    this.insetBottom = bottom;
  }

  @Override
  public double getInsetTop() {
    return insetTop;
  }

  @Override
  public double getInsetLeft() {
    return insetLeft;
  }

  @Override
  public double getInsetRight() {
    return insetRight;
  }

  @Override
  public double getInsetBottom() {
    return insetBottom;
  }


  @Override
  public Legend getLegend() {
    return legend;
  }

  @Override
  public void setShowLegend(boolean show) {
    this.showLegend = show;
  }

  @Override
  public boolean isShowLegend() {
    return showLegend;
  }

  @Override
  public void setLegendPosition(int position) {
    this.legendPosition = position;
  }

  @Override
  public int getLegendPosition() {
    return legendPosition;
  }

  @Override
  public void setLegendAlignment(int legendAlignment) {
    this.legendAlignment = legendAlignment;
  }

  @Override
  public int getLegendAlignment() {
    return legendAlignment;
  }

  @Override
  public void setLegendInside(boolean legendInside) {
    this.legendInside = legendInside;
  }

  @Override
  public boolean isLegendInside() {
    return legendInside;
  }

  @Override
  public void setLegendSpacing(double padding) {
    this.legendSpacing = padding;
  }

  @Override
  public double getLegendSpacing() {
    return legendSpacing;
  }

  @Override
  public void setContentInsets(double top, double left, double right, double bottom) {
    this.contentInsetTop = top;
    this.contentInsetLeft = left;
    this.contentInsetRight = right;
    this.contentInsetBottom = bottom;
  }

  @Override
  public Double getContentInsetBottom() {
    return contentInsetBottom;
  }

  @Override
  public Double getContentInsetLeft() {
    return contentInsetLeft;
  }

  @Override
  public Double getContentInsetRight() {
    return contentInsetRight;
  }

  @Override
  public Double getContentInsetTop() {
    return contentInsetTop;
  }


  @Override
  public void render(double progress) throws ChartException {
    this.lastProgress = progress;

    final Renderer r = getRenderer();
    r.enableHandlers(isInteractive && !isPopup);
    double width = r.getWidth();
    double height = r.getHeight();

    r.init();
    r.setColor(getBackground());
    r.fillRect(0, 0, width + 1, height + 1);

    double x = getInsetLeft();
    double y = getInsetTop();
    width -= (getInsetLeft() + getInsetRight());
    height -= (getInsetTop() + getInsetBottom());

    final ChartFont font = getFont();

    ChartFont titleFont = getTitleFont();
    if (titleFont == null) {
      titleFont = font;
    }

    ChartFont subTitleFont = getSubTitleFont();
    if (subTitleFont == null) {
      subTitleFont = titleFont;
    }

    // draw title
    final String title = getTitle();
    if (title != null) {
      r.setFont(titleFont);
      final double textHeight = r.getTextHeight(title);

      ChartColor color = getTitleColor();
      if (color == null) {
        color = getForeground();
      }

      r.setColor(color);
      r.drawText(x + width / 2, y, title, 0, Renderer.NORTH, false);

      final double titlePadding = getTitlePadding() + textHeight;
      y += titlePadding;
      height -= titlePadding;
    }

    // draw title
    final String subTitle = getSubTitle();
    if (subTitle != null) {
      r.setFont(subTitleFont);
      final double textHeight = r.getTextHeight(subTitle);

      ChartColor color = getSubTitleColor();
      if (color == null) {
        color = getTitleColor();
        if (color == null) {
          color = getForeground();
        }
      }

      r.setColor(color);
      r.drawText(x + width / 2, y, subTitle, 0, Renderer.NORTH, false);

      final double titlePadding = getTitlePadding() + textHeight;
      y += titlePadding;
      height -= titlePadding;
    }

    final double legendPadding = getLegendSpacing();
    double legendY = y;
    double legendX = x;
    double legendSpaceX = width;
    double legendSpaceY = height;

    if (legendInside) {
      double rx = x, ry = y, rWidth = width, rHeight = height;
      if (contentInsetLeft != null) {
        rx = contentInsetLeft;
      }
      if (contentInsetTop != null) {
        ry = contentInsetTop;
      }
      if (contentInsetRight != null) {
        rWidth = r.getWidth() - contentInsetRight - rx;
      }
      if (contentInsetRight != null) {
        rHeight = r.getHeight() - contentInsetBottom - ry;
      }
      render(progress, rx, ry, rWidth, rHeight);

      legendX = canvasX + legendPadding;
      legendY = canvasY + legendPadding;
      legendSpaceX = canvasWidth - 2 * legendPadding;
      legendSpaceY = canvasHeight - 2 * legendPadding;
    }

    // draw legend
    if (showLegend) {
      if (legend != null) {
        legend.setDimension(needsSampleLegend() ? 0 : 1);
        final int pos = getLegendPosition();
        legend.setVertical((pos == WEST) || (pos == EAST));
        final double max = legend.isVertical() ? legendSpaceY : legendSpaceX;
        legend.setData(data);
        final double[] size = legend.getNeededSize(r, max, font);
        final double legendWidth = size[0];
        final double legendHeight = size[1];

        switch (pos) {
          case WEST:
            if (!legendInside) {
              width -= legendWidth + legendPadding;
              x += legendWidth + legendPadding;
            }
            break;
          case EAST:
            legendX += legendSpaceX - legendWidth;
            if (!legendInside) {
              width -= legendWidth + legendPadding;
            }
            break;
          case NORTH:
            if (!legendInside) {
              height -= legendHeight + legendPadding;
              y += legendHeight + legendPadding;
            }
            break;
          default:
            legendY += legendSpaceY - legendHeight;
            if (!legendInside) {
              height -= legendHeight + legendPadding;
            }
            break;
        }

        switch (pos) {
          case WEST:
          case EAST:
            switch (getLegendAlignment()) {
              case NORTH:
                break;
              case SOUTH:
                legendY += legendSpaceY - legendHeight;
                break;
              default:
                legendY += (legendSpaceY / 2) - (legendHeight / 2);
                break;
            }
            break;
          case NORTH:
          case SOUTH:
            switch (getLegendAlignment()) {
              case WEST:
                break;
              case EAST:
                legendX += legendSpaceX - legendWidth;
                break;
              default:
                legendX += (legendSpaceX / 2) - (legendWidth / 2);
                break;
            }
            break;
        }

        legend.render(
          progress,
          r,
          legendX, legendY,
          legendWidth, legendHeight,
          getForeground(),
          getBackground(),
          font
        );
      }
    }

    if (!legendInside) {
      double rx = x, ry = y, rWidth = width, rHeight = height;
      if (contentInsetLeft != null) {
        rx = contentInsetLeft;
      }
      if (contentInsetTop != null) {
        ry = contentInsetTop;
      }
      if (contentInsetRight != null) {
        rWidth = r.getWidth() - contentInsetRight - rx;
      }
      if (contentInsetRight != null) {
        rHeight = r.getHeight() - contentInsetBottom - ry;
      }
      render(progress, rx, ry, rWidth, rHeight);
    }

    // add popup click?
    if (isPopup) {
      final Runnable onClick = () -> {
        try {
          r.openPopup(BasicChartImpl.this);
        } catch (Exception e) {
          r.showError(e);
        }
      };

      r.enableHandlers(true);
      r.addPopup(0, 0, r.getWidth(), r.getHeight(), 0, 0, null, null, null, null, onClick);
      r.enableHandlers(false);
    }

    r.finish();
  }


  @Override
  public void setLinkOpener(LinkOpener linkOpener) {
    this.linkOpener = linkOpener;
  }

  @Override
  public LinkOpener getLinkOpener() {
    return linkOpener;
  }
}
