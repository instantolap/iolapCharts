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
  private int insetTop = 10;
  private int insetBottom = 10;
  private int insetLeft = 10;
  private int insetRight = 10;
  private Renderer renderer;
  private boolean animationEnabled = true;
  private long animationTime = 1000;
  private int titlePadding = 10;
  private boolean showLegend = false;
  private int legendPosition = SOUTH;
  private int legendAlignment = CENTER;
  private boolean legendInside = false;
  private int legendSpacing = 10;
  private double lastProgress;
  private Integer contentInsetTop, contentInsetLeft, contentInsetRight, contentInsetBottom;
  private ChartColor titleColor, subTitleColor;
  private LinkOpener linkOpener;
  private int canvasX, canvasY, canvasWidth, canvasHeight;

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

  protected abstract void render(double progress, int x, int y, int width,
                                 int height) throws ChartException;

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

  protected Integer coalesce(Integer... a) {
    for (Integer v : a) {
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

  protected void setCanvasArea(int x, int y, int w, int h) {
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
  public void setTitlePadding(int spacing) {
    this.titlePadding = spacing;
  }

  @Override
  public int getTitlePadding() {
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
  public void setInsets(int top, int left, int right, int bottom) {
    this.insetTop = top;
    this.insetLeft = left;
    this.insetRight = right;
    this.insetBottom = bottom;
  }

  @Override
  public int getInsetTop() {
    return insetTop;
  }

  @Override
  public int getInsetLeft() {
    return insetLeft;
  }

  @Override
  public int getInsetRight() {
    return insetRight;
  }

  @Override
  public int getInsetBottom() {
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
  public void setLegendSpacing(int padding) {
    this.legendSpacing = padding;
  }

  @Override
  public int getLegendSpacing() {
    return legendSpacing;
  }

  @Override
  public void setContentInsets(int top, int left, int right, int bottom) {
    this.contentInsetTop = top;
    this.contentInsetLeft = left;
    this.contentInsetRight = right;
    this.contentInsetBottom = bottom;
  }

  @Override
  public Integer getContentInsetBottom() {
    return contentInsetBottom;
  }

  @Override
  public Integer getContentInsetLeft() {
    return contentInsetLeft;
  }

  @Override
  public Integer getContentInsetRight() {
    return contentInsetRight;
  }

  @Override
  public Integer getContentInsetTop() {
    return contentInsetTop;
  }


  @Override
  public void render(double progress) throws ChartException {
    this.lastProgress = progress;

    final Renderer r = getRenderer();
    r.enableHandlers(isInteractive && !isPopup);
    int width = r.getWidth();
    int height = r.getHeight();

    r.init();
    r.setColor(getBackground());
    r.fillRect(0, 0, width + 1, height + 1);

    int x = getInsetLeft();
    int y = getInsetTop();
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
      final int textHeight = r.getTextHeight(title);

      ChartColor color = getTitleColor();
      if (color == null) {
        color = getForeground();
      }

      r.setColor(color);
      r.drawText(x + width / 2, y, title, 0, Renderer.NORTH);

      final int titlePadding = getTitlePadding() + textHeight;
      y += titlePadding;
      height -= titlePadding;
    }

    // draw title
    final String subTitle = getSubTitle();
    if (subTitle != null) {
      r.setFont(subTitleFont);
      final int textHeight = r.getTextHeight(subTitle);

      ChartColor color = getSubTitleColor();
      if (color == null) {
        color = getTitleColor();
        if (color == null) {
          color = getForeground();
        }
      }

      r.setColor(color);
      r.drawText(x + width / 2, y, subTitle, 0, Renderer.NORTH);

      final int titlePadding = getTitlePadding() + textHeight;
      y += titlePadding;
      height -= titlePadding;
    }

    final int legendPadding = getLegendSpacing();
    int legendY = y;
    int legendX = x;
    int legendSpaceX = width;
    int legendSpaceY = height;

    if (legendInside) {
      int rx = x, ry = y, rWidth = width, rHeight = height;
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
        final int max = legend.isVertical() ? legendSpaceY : legendSpaceX;
        legend.setData(data);
        final int[] size = legend.getNeededSize(r, max, font);
        final int legendWidth = size[0];
        final int legendHeight = size[1];

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
      int rx = x, ry = y, rWidth = width, rHeight = height;
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
