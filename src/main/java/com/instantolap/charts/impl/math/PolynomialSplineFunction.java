package com.instantolap.charts.impl.math;

import java.util.Arrays;


public class PolynomialSplineFunction {
  /**
   * Spline segment interval delimiters (knots). Size is n + 1 for n segments.
   */
  private final double knots[];
  /**
   * The polynomial functions that make up the spline. The first element
   * determines the value of the spline over the first subinterval, the second
   * over the second, etc. Spline function values are determined by evaluating
   * these functions at {@code (x - knot[i])} where i is the knot segment to
   * which x belongs.
   */
  private final PolynomialFunction polynomials[];
  /**
   * Number of spline segments. It is equal to the number of polynomials and
   * to the number of partition points - 1.
   */
  private final int n;

  /**
   * Construct a polynomial spline function with the given segment delimiters
   * and interpolating polynomials. The constructor copies both arrays and
   * assigns the copies to the knots and polynomials properties, respectively.
   *
   * @param knots       Spline segment interval delimiters.
   * @param polynomials Polynomial functions that make up the spline.
   */
  public PolynomialSplineFunction(double knots[],
    PolynomialFunction polynomials[])
  {
    this.n = knots.length - 1;
    this.knots = new double[n + 1];
    System.arraycopy(knots, 0, this.knots, 0, n + 1);

    this.polynomials = new PolynomialFunction[n];
    System.arraycopy(polynomials, 0, this.polynomials, 0, n);
  }

  /**
   * Compute the value for the function. See {@link PolynomialSplineFunction}
   * for details on the algorithm for computing the value of the function.
   *
   * @param v Point for which the function value should be computed.
   *
   * @return the value.
   */
  public double value(double v) {
    int i = Arrays.binarySearch(knots, v);
    if (i < 0) {
      i = -i - 2;
    }
    // This will handle the case where v is the last knot value
    // There are only n-1 polynomials, so if v is the last knot
    // then we will use the last polynomial to calculate the value.
    if (i >= polynomials.length) {
      i--;
    }
    return polynomials[i].value(v - knots[i]);
  }
}
