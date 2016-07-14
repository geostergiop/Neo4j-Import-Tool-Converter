package gr.aueb.cs.infosec.util;

public class Scaling {

  private final double range0, range1, domain0, domain1;

  /**
   * Constructor
   *
   * @param domain0
   * @param domain1
   * @param range0
   * @param range1
   */
  public Scaling(double domain0, double domain1, double range0, double range1) {
    this.range0 = range0;
    this.range1 = range1;
    this.domain0 = domain0;
    this.domain1 = domain1;
  }

  /**
   * Interpolate method
   *
   * @param x
   * @return
   */
  private double interpolate(double x) {
    return range0 * (1 - x) + range1 * x;
  }

  /**
   * Uninterpolate method
   *
   * @param x
   * @return
   */
  private double uninterpolate(double x) {
    double b = (domain1 - domain0) != 0 ? domain1 - domain0 : 1 / domain1;
    return (x - domain0) / b;
  }

  /**
   * Rescale to the new scaling levels
   * 
   * @param x
   * @return
   */
  public double rescale(double x) {
    return interpolate(uninterpolate(x));
  }
}
