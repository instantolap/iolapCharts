package com.instantolap.charts.impl.math;


/**
 * Estimates an ordinary least squares regression model with one independent
 * variable.
 * <p>
 * <code> y = intercept + slope * x  </code>
 * </p>
 * <p>
 * Standard errors for <code>intercept</code> and <code>slope</code> are
 * available as well as ANOVA, r-square and Pearson's r statistics.
 * </p>
 * <p>
 * Observations (x,y pairs) can be added to the model one at a time or they can
 * be provided in a 2-dimensional array. The observations are not stored in
 * memory, so there is no limit to the number of observations that can be added
 * to the model.
 * </p>
 * <p>
 * <strong>Usage Notes</strong>:
 * <ul>
 * <li>When there are fewer than two observations in the model, or when there is
 * no variation in the x values (i.e. all x values are the same) all statistics
 * return <code>NaN</code>. At least two observations with different x
 * coordinates are required to estimate a bivariate regression model.</li>
 * <li>Getters for the statistics always compute values based on the current set
 * of observations -- i.e., you can get statistics, then add more data and get
 * updated statistics without using a new instance. There is no "compute" method
 * that updates all statistics. Each of the getters performs the necessary
 * computations to return the requested statistic.</li>
 * <li>The intercept term may be suppressed by passing {@code false} to the
 * {@link #SimpleRegression(boolean)} constructor. When the {@code hasIntercept}
 * property is false, the model is estimated without a constant term and
 * {@link #getIntercept()} returns {@code 0}.</li>
 * </ul>
 * </p>
 *
 * @version $Id: SimpleRegression.java,v 1.1 2015/02/02 08:10:44 tom Exp $
 */
public class SimpleRegression {

  private final boolean hasIntercept;
  private double sumX = 0d;
  private double sumXX = 0d;
  private double sumY = 0d;
  private double sumXY = 0d;
  private long n = 0;
  private double xbar = 0;
  private double ybar = 0;

  // ---------------------Public methods--------------------------------------

  public SimpleRegression() {
    this(true);
  }

  public SimpleRegression(boolean includeIntercept) {
    super();
    hasIntercept = includeIntercept;
  }

  public void addData(final double x, final double y) {
    if (n == 0) {
      xbar = x;
      ybar = y;
    } else {
      if (hasIntercept) {
        final double fact1 = 1.0 + n;
        final double fact2 = n / (1.0 + n);
        final double dx = x - xbar;
        final double dy = y - ybar;
        sumXX += dx * dx * fact2;
        sumXY += dx * dy * fact2;
        xbar += dx / fact1;
        ybar += dy / fact1;
      }
    }
    if (!hasIntercept) {
      sumXX += x * x;
      sumXY += x * y;
    }
    sumX += x;
    sumY += y;
    n++;
  }

  public double predict(final double x) {
    final double b1 = getSlope();
    if (hasIntercept) {
      return getIntercept(b1) + b1 * x;
    }
    return b1 * x;
  }

  public double getSlope() {
    if (n < 2) {
      return Double.NaN; // not enough data
    }
    if (Math.abs(sumXX) < 10 * Double.MIN_VALUE) {
      return Double.NaN; // not enough variation in x
    }
    return sumXY / sumXX;
  }

  private double getIntercept(final double slope) {
    if (hasIntercept) {
      return (sumY - slope * sumX) / n;
    }
    return 0.0;
  }

  public double getIntercept() {
    return hasIntercept ? getIntercept(getSlope()) : 0.0;
  }

  public boolean hasIntercept() {
    return hasIntercept;
  }
}
