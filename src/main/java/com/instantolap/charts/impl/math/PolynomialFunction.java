package com.instantolap.charts.impl.math;

/**
 * Immutable representation of a real polynomial function with real
 * coefficients.
 * <p>
 * <a href="http://mathworld.wolfram.com/HornersMethod.html">Horner's Method</a>
 * is used to evaluate the function.
 * </p>
 *
 * @version $Id: PolynomialFunction.java,v 1.1 2015/02/02 08:10:44 tom Exp $
 */
public class PolynomialFunction {
  /**
   * The coefficients of the polynomial, ordered by degree -- i.e.,
   * coefficients[0] is the constant term and coefficients[n] is the
   * coefficient of x^n where n is the degree of the polynomial.
   */
  private final double coefficients[];

  /**
   * Construct a polynomial with the given coefficients. The first element of
   * the coefficients array is the constant term. Higher degree coefficients
   * follow in sequence. The degree of the resulting polynomial is the index
   * of the last non-null element of the array, or 0 if all elements are null.
   * <p>
   * The constructor makes a copy of the input array and assigns the copy to
   * the coefficients property.
   * </p>
   *
   * @param c Polynomial coefficients.
   */
  public PolynomialFunction(double c[]) {
    int n = c.length;
    while ((n > 1) && (c[n - 1] == 0)) {
      --n;
    }
    this.coefficients = new double[n];
    System.arraycopy(c, 0, this.coefficients, 0, n);
  }

  /**
   * Uses Horner's Method to evaluate the polynomial with the given
   * coefficients at the argument.
   * <p>
   * The value returned is <br/>
   * <code>coefficients[n] * x^n + ... + coefficients[1] * x  + coefficients[0]</code>
   * </p>
   *
   * @param x Argument for which the function value should be computed.
   *
   * @return the value of the polynomial at the given point.
   */
  public double value(double x) {
    final int n = coefficients.length;
    double result = coefficients[n - 1];
    for (int j = n - 2; j >= 0; j--) {
      result = x * result + coefficients[j];
    }
    return result;
  }
}
