package geometry;
public class Point3d {
  public double x, y, z;
  
  public Point3d() {
    x = 0d;
    y = 0d;
    z = 0d;
  }
  
  public Point3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Point3d crossProduct(Point3d q) {
    return new Point3d(
        y * q.z -z * q.y,
        q.x * z - x * q.z,
        x * q.y - y * q.x
        );
  }
  
  public double scalarProduct(Point3d q) {
    return x * q.x + y * q.y + z * q.z;
  }
  

  public static Point3d linearCombination(Point3d[] points, double[] coeff) {
    Point3d result = new Point3d(0, 0, 0);
    for (int i = 0; i < coeff.length; i++) {
      result.x += (points[i].x * coeff[i]);
      result.y += (points[i].y * coeff[i]);
      result.z += (points[i].z * coeff[i]);
    }
    return result;
  }
}
