package com.instantolap.charts.factories;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.Transformation;
import com.instantolap.charts.WriteableCube;
import com.instantolap.charts.impl.data.DataImpl;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.impl.data.transform.*;
import com.instantolap.charts.json.JSONArray;
import com.instantolap.charts.json.JSONException;
import com.instantolap.charts.json.JSONObject;

import java.util.Iterator;


public class JSONDataFactory {

  public static Data parseData(JSONObject json, Palette palette) throws JSONException {
    final Data data = new DataImpl(palette);
    final WriteableCube cube = data.getMainCube();

    // read dimensions
    final JSONArray dimensions = json.optJSONArray("dimensions");
    if (dimensions != null) {
      for (int dim = 0; dim < dimensions.length(); dim++) {

        // generate at least dimension with 0 samples
        cube.setDimensionSize(dim, 0);

        // add samples
        final JSONArray keys = dimensions.getJSONArray(dim);
        for (int n = 0; n < keys.length(); n++) {
          cube.addSample(dim, keys.getString(n));
        }
      }
    }

    // read values
    final JSONArray values = json.optJSONArray("values");
    if (values != null) {
      final int dimensionCount = cube.getDimensionCount();
      final int[] pos = new int[dimensionCount];
      parseValues(cube, dimensionCount - 1, pos, values);
    }

    addTransformations(json, data);
    return data;
  }

  private static void parseValues(WriteableCube cube, int axis, int[] pos, JSONArray values)
    throws JSONException
  {
    if (axis < 0) {
      throw new JSONException(
        "Value depth does not match dimension count");
    }
    if (axis == 0) {
      cube.setDimensionSize(axis, values.length());
    }
    for (int n = 0; n < values.length(); n++) {
      pos[axis] = n;
      final Object o = values.get(n);
      if (o instanceof JSONArray) {
        parseValues(cube, axis - 1, pos, (JSONArray) o);
      } else if (o instanceof Number) {
        if (axis != 0) {
          throw new JSONException("Value depth does not match dimension count");
        }
        final Number number = (Number) o;
        cube.set(Cube.MEASURE_VALUE, number.doubleValue(), pos);
      } else if (o instanceof JSONObject) {
        if (axis != 0) {
          throw new JSONException("Value depth does not match dimension count");
        }

        final JSONObject obj = (JSONObject) o;
        for (final Iterator<String> i = obj.keys(); i.hasNext(); ) {
          final String m = i.next();
          if (obj.isNull(m)) {
            cube.set(m, (Double) null, pos);
          } else {
            cube.set(m, obj.getDouble(m), pos);
          }
        }
      }
    }
  }

  public static void addTransformations(JSONObject json, Data data) throws JSONException {
    Cube currentCube = data.getCurrentCube();
    final JSONArray transforms = json.optJSONArray("transform");
    if (transforms != null) {
      for (int n = 0; n < transforms.length(); n++) {
        final Transformation t = parseTransformation(transforms.getJSONObject(n));
        currentCube = t.transform(currentCube);
      }
    }
    data.setCurrentCube(currentCube);
  }

  private static Transformation parseTransformation(JSONObject json) throws JSONException {
    final String type = json.getString("type");
    if ("stack".equalsIgnoreCase(type)) {
      return parseStackTransformation(json);
    } else if ("accumulate".equalsIgnoreCase(type)) {
      return parseAccumulateTransformation(json);
    } else if ("waterfall".equalsIgnoreCase(type)) {
      return parseWaterfallTransformation(json);
    } else if ("normalize".equalsIgnoreCase(type)) {
      return parseNormalizeTransformation(json);
    } else if ("subcube".equalsIgnoreCase(type)) {
      return parseSubcubeTransformation(json);
    } else if ("reverse".equalsIgnoreCase(type)) {
      return parseReverseTransformation(json);
    } else if ("sort".equalsIgnoreCase(type)) {
      return parseSortTransformation(json);
    } else {
      throw new JSONException("Unknown transformation '" + type + "'");
    }
  }

  private static Transformation parseStackTransformation(JSONObject json) {
    return new StackedTransform();
  }

  private static Transformation parseAccumulateTransformation(JSONObject json) {
    return new AccumulateTransform();
  }

  private static Transformation parseWaterfallTransformation(JSONObject json) {
    return new WaterfallTransform();
  }

  private static Transformation parseNormalizeTransformation(JSONObject json) {
    return new NormalizeTransform();
  }

  private static Transformation parseSubcubeTransformation(JSONObject json) throws JSONException {
    final JSONArray dimensionsArray = json.getJSONArray("dimensions");
    final int[] dimensions = new int[dimensionsArray.length()];
    for (int n = 0; n < dimensions.length; n++) {
      dimensions[n] = dimensionsArray.getInt(n);
    }
    return new SubcubeTransform(dimensions);
  }

  private static Transformation parseReverseTransformation(JSONObject json) throws JSONException {
    final int dimension = json.getInt("dimension");
    return new ReverseTransform(dimension);
  }

  private static Transformation parseSortTransformation(JSONObject json) throws JSONException {
    final int dimension = json.getInt("dimension");
    final boolean desc = json.optBoolean("desc", false);
    final int limit = json.optInt("limit", Integer.MAX_VALUE);
    return new SortTransform(dimension, desc, limit);
  }
}
