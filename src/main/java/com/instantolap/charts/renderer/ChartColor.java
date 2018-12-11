package com.instantolap.charts.renderer;

import com.instantolap.charts.renderer.util.StringHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class ChartColor implements Serializable {

  public static final ChartColor WHITE = new ChartColor("White");
  public static final ChartColor BLACK = new ChartColor("Black");
  public static final ChartColor LIGHT_GRAY = new ChartColor("LightGray");
  public static final ChartColor RED = new ChartColor("Red");
  public static final ChartColor GREEN = new ChartColor("Green");
  public static final ChartColor YELLOW = new ChartColor("Yellow");
  public static final ChartColor BLUE = new ChartColor("Blue");
  public static final ChartColor LIGHT_BLUE = new ChartColor("LightBlue");
  public static final ChartColor SHADOW = BLACK.setOpacity(0.33);

  private static Map<String, String> namedColors;
  private int r, g, b;
  private int a;
  private boolean gradient;

  public ChartColor() {
  }

  public ChartColor(String rgb) {
    this(rgb, false);
  }

  public ChartColor(String rgb, boolean gradient) {
    try {
      this.gradient = gradient;

      if (rgb != null) {
        if (rgb.startsWith("gradient ")) {
          this.gradient = true;
          rgb = rgb.substring(9);
        }
      }

      // opacity?
      a = 0xff;
      final int index = rgb.indexOf(",");
      if (index >= 0) {
        String o = rgb.substring(index + 1);
        o = StringHelper.trim(o);
        rgb = rgb.substring(0, index);
        a = Integer.parseInt(o);
      }

      rgb = StringHelper.trim(rgb);
      rgb = parseColor(rgb);

      if (rgb.startsWith("#")) {
        rgb = rgb.substring(1);
      }

      if (rgb.length() == 3) {
        final String rc = rgb.substring(0, 1);
        r = Integer.parseInt(rc + rc, 16);
        final String gc = rgb.substring(1, 2);
        g = Integer.parseInt(gc + gc, 16);
        final String bc = rgb.substring(2, 3);
        b = Integer.parseInt(bc + bc, 16);
      } else {
        final String rc = rgb.substring(0, 2);
        r = Integer.parseInt(rc, 16);
        final String gc = rgb.substring(2, 4);
        g = Integer.parseInt(gc, 16);
        final String bc = rgb.substring(4, 6);
        b = Integer.parseInt(bc, 16);
      }
    }
    catch (Exception e) {
      throw new RuntimeException("Invalid color-code '" + rgb + "'", e);
    }
  }

  public static String parseColor(String code) {
    if (code == null) {
      throw new IllegalArgumentException("Missing color code");
    }

    initColors();
    final String named = namedColors.get(code.toLowerCase());
    if (named != null) {
      return "#" + named;
    }
    return code;
  }

  private static void initColors() {
    if (namedColors != null) {
      return;
    }

    namedColors = new HashMap<>();
    add("AliceBlue", "F0F8FF");
    add("AntiqueWhite", "FAEBD7");
    add("Aqua", "00FFFF");
    add("Aquamarine", "7FFFD4");
    add("Azure", "F0FFFF");
    add("Beige", "F5F5DC");
    add("Bisque", "FFE4C4");
    add("Black", "000000");
    add("BlanchedAlmond", "FFEBCD");
    add("Blue", "0000FF");
    add("BlueViolet", "8A2BE2");
    add("Brown", "A52A2A");
    add("BurlyWood", "DEB887");
    add("CadetBlue", "#5F9EA0");
    add("Chartreuse", "7FFF00");
    add("Chocolate", "D2691E");
    add("Coral", "FF7F50");
    add("CornflowerBlue", "6495ED");
    add("Cornsilk", "FFF8DC");
    add("Crimson", "DC143C");
    add("Cyan", "00FFFF");
    add("DarkBlue", "00008B");
    add("DarkCyan", "008B8B");
    add("DarkGoldenRod", "B8860B");
    add("DarkGray", "A9A9A9");
    add("DarkGreen", "006400");
    add("DarkKhaki", "BDB76B");
    add("DarkMagenta", "8B008B");
    add("DarkOliveGreen", "556B2F");
    add("DarkOrange", "FF8C00");
    add("DarkOrchid", "9932CC");
    add("DarkRed", "8B0000");
    add("DarkSalmon", "E9967A");
    add("DarkSeaGreen", "8FBC8F");
    add("DarkSlateBlue", "483D8B");
    add("DarkSlateGray", "2F4F4F");
    add("DarkTurquoise", "00CED1");
    add("DarkViolet", "9400D3");
    add("DeepPink", "FF1493");
    add("DeepSkyBlue", "00BFFF");
    add("DimGray", "696969");
    add("DodgerBlue", "1E90FF");
    add("FireBrick", "B22222");
    add("FloralWhite", "FFFAF0");
    add("ForestGreen", "228B22");
    add("Fuchsia", "FF00FF");
    add("Gainsboro", "DCDCDC");
    add("GhostWhite", "F8F8FF");
    add("Gold", "FFD700");
    add("GoldenRod", "DAA520");
    add("Gray", "808080");
    add("Green", "008000");
    add("GreenYellow", "ADFF2F");
    add("HoneyDew", "F0FFF0");
    add("HotPink", "FF69B4");
    add("IndianRed", "CD5C5C");
    add("Indigo", "4B0082");
    add("Ivory", "FFFFF0");
    add("Khaki", "F0E68C");
    add("Lavender", "E6E6FA");
    add("LavenderBlush", "FFF0F5");
    add("LawnGreen", "7CFC00");
    add("LemonChiffon", "FFFACD");
    add("LightBlue", "ADD8E6");
    add("LightCoral", "F08080");
    add("LightCyan", "E0FFFF");
    add("LightGoldenRodYellow", "FAFAD2");
    add("LightGray", "D3D3D3");
    add("LightGreen", "90EE90");
    add("LightPink", "FFB6C1");
    add("LightSalmon", "FFA07A");
    add("LightSeaGreen", "20B2AA");
    add("LightSkyBlue", "87CEFA");
    add("LightSlateGray", "778899");
    add("LightSteelBlue", "B0C4DE");
    add("LightYellow", "FFFFE0");
    add("Lime", "00FF00");
    add("LimeGreen", "32CD32");
    add("Linen", "FAF0E6");
    add("Magenta", "FF00FF");
    add("Maroon", "800000");
    add("MediumAquaMarine", "66CDAA");
    add("MediumBlue", "0000CD");
    add("MediumOrchid", "BA55D3");
    add("MediumPurple", "9370DB");
    add("MediumSeaGreen", "3CB371");
    add("MediumSlateBlue", "7B68EE");
    add("MediumSpringGreen", "00FA9A");
    add("MediumTurquoise", "48D1CC");
    add("MediumVioletRed", "C71585");
    add("MidnightBlue", "191970");
    add("MintCream", "F5FFFA");
    add("MistyRose", "FFE4E1");
    add("Moccasin", "FFE4B5");
    add("NavajoWhite", "FFDEAD");
    add("Navy", "000080");
    add("OldLace", "FDF5E6");
    add("Olive", "808000");
    add("OliveDrab", "6B8E23");
    add("Orange", "FFA500");
    add("OrangeRed", "FF4500");
    add("Orchid", "DA70D6");
    add("PaleGoldenRod", "EEE8AA");
    add("PaleGreen", "98FB98");
    add("PaleTurquoise", "AFEEEE");
    add("PaleVioletRed", "DB7093");
    add("PapayaWhip", "FFEFD5");
    add("PeachPuff", "FFDAB9");
    add("Peru", "CD853F");
    add("Pink", "FFC0CB");
    add("Plum", "DDA0DD");
    add("PowderBlue", "B0E0E6");
    add("Purple", "800080");
    add("Red", "FF0000");
    add("RosyBrown", "BC8F8F");
    add("RoyalBlue", "4169E1");
    add("SaddleBrown", "8B4513");
    add("Salmon", "FA8072");
    add("SandyBrown", "F4A460");
    add("SeaGreen", "2E8B57");
    add("SeaShell", "FFF5EE");
    add("Sienna", "A0522D");
    add("Silver", "C0C0C0");
    add("SkyBlue", "87CEEB");
    add("SlateBlue", "6A5ACD");
    add("SlateGray", "708090");
    add("Snow", "FFFAFA");
    add("SpringGreen", "00FF7F");
    add("SteelBlue", "4682B4");
    add("Tan", "D2B48C");
    add("Teal", "008080");
    add("Thistle", "D8BFD8");
    add("Tomato", "FF6347");
    add("Turquoise", "40E0D0");
    add("Violet", "EE82EE");
    add("Wheat", "F5DEB3");
    add("White", "FFFFFF");
    add("WhiteSmoke", "F5F5F5");
    add("Yellow", "FFFF00");
    add("YellowGreen", "9ACD32");
  }

  private static void add(String name, String code) {
    namedColors.put(name.toLowerCase(), code.toUpperCase());
  }

  public ChartColor(int r, int g, int b) {
    this(r, g, b, 0xff, false);
  }

  public ChartColor(int r, int g, int b, int a, boolean gradient) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
    this.gradient = gradient;
  }

  public ChartColor(int r, int g, int b, int a) {
    this(r, g, b, a, false);
  }

  public static String[] parseColors(String code) {
    if (code == null) {
      return null;
    }
    final String[] codes = StringHelper.splitString(code);
    final String[] result = new String[codes.length];
    for (int n = 0; n < codes.length; n++) {
      result[n] = parseColor(codes[n]);
    }
    return result;
  }

  public int getR() {
    return r;
  }

  public int getG() {
    return g;
  }

  public int getB() {
    return b;
  }

  public int getA() {
    return a;
  }

  public boolean isGradient() {
    return gradient;
  }

  public ChartColor setGradient(boolean gradient) {
    return new ChartColor(r, g, b, a, gradient);
  }

  public ChartColor setOpacity(double progress) {
    return new ChartColor(r, g, b, (int) (a * progress), gradient);
  }

  @Override
  public String toString() {
    return "rgb(" + r + "," + g + "," + b + ")";
  }

  public double getLuminance() {
    final double r = this.r / 255.0;
    final double g = this.g / 255.0;
    final double b = this.b / 255.0;

    final double max = Math.max(Math.max(r, g), b);
    final double min = Math.min(Math.min(r, g), b);
    return (max + min) / 2;
  }

  public ChartColor lighter(double a1) {
    final double a2 = 1 - a1;
    final int r2 = (int) (0xff * a1 + r * a2);
    final int g2 = (int) (0xff * a1 + g * a2);
    final int b2 = (int) (0xff * a1 + b * a2);
    return new ChartColor(r2, g2, b2, a);
  }
}
