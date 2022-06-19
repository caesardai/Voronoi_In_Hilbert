public class Point_2 implements Point_
{
    public double x;
    public double y;
    
    public Point_2() {
    }
    
    public Point_2(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point_2(final double[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
    }
    
    public Point_2(final Point_ p) {
        this.x = p.getCartesian(0);
        this.y = p.getCartesian(1);
    }
    
    @Override
    public void barycenter(final Point_[] points) {
        double x_ = 0.0;
        double y_ = 0.0;
        for (int i = 0; i < points.length; ++i) {
            x_ += points[i].getCartesian(0);
            y_ += points[i].getCartesian(1);
        }
        this.x = x_ / points.length;
        this.y = y_ / points.length;
    }
    
    public static Point_ linearCombination(final Point_2[] points, final double[] coefficients) {
        double x_ = 0.0;
        double y_ = 0.0;
        for (int i = 0; i < points.length; ++i) {
            x_ += points[i].getX() * coefficients[i];
            y_ += points[i].getY() * coefficients[i];
        }
        return new Point_2(x_, y_);
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    @Override
    public float[] toFloat() {
        final float[] result = { (float)this.x, (float)this.y };
        return result;
    }
    
    @Override
    public double[] toDouble() {
        final double[] result = { this.x, this.y };
        return result;
    }
    
    public Point_ toCartesian() {
        throw new Error("method not defined: to be completed");
    }
    
    public Point_ toHomogeneous() {
        final double[] hCoordinates = { this.x, this.y, 1.0 };
        return (Point_)new Point_3(hCoordinates);
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    @Override
    public void translateOf(final Vector_ v) {
        this.x += v.getCartesian(0);
        this.y += v.getCartesian(1);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Point_2) {
            final Point_2 p = (Point_2)o;
            return this.x == p.getCartesian(0) && this.y == p.getCartesian(1);
        }
        throw new RuntimeException("Comparing Point_2 with object of type " + o.getClass());
    }
    
    public double distanceFrom(final Point_2 p) {
        final double dX = p.getX() - this.x;
        final double dY = p.getY() - this.y;
        return Math.sqrt(dX * dX + dY * dY);
    }
    
    public double squareDistance(final Point_2 p) {
        final double dX = p.getX() - this.x;
        final double dY = p.getY() - this.y;
        return dX * dX + dY * dY;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
    
    @Override
    public int dimension() {
        return 2;
    }
    
    @Override
    public double getCartesian(final int i) {
        if (i == 0) {
            return this.x;
        }
        return this.y;
    }
    
    @Override
    public void setCartesian(final int i, final double x) {
        if (i == 0) {
            this.x = x;
        }
        else {
            this.y = x;
        }
    }
    
    @Override
    public void setOrigin() {
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public Point_2 sum(final Vector_ v) {
        return new Point_2(this.x + v.getCartesian(0), this.y + v.getCartesian(1));
    }
    
    public Point_2 multiplyByScalar(final double s) {
        return new Point_2(this.x * s, this.y * s);
    }
    
    @Override
    public int compareTo(final Point_ o) {
        final Point_2 p = (Point_2)o;
        if (this.x < p.getX()) {
            return -1;
        }
        if (this.x > p.getX()) {
            return 1;
        }
        if (this.y < p.getY()) {
            return -1;
        }
        if (this.y > p.getY()) {
            return 1;
        }
        return 0;
    }
    
    public Vector_2 minus(final Point_ p) {
        if (p == null) {
            System.out.println("NULL");
        }
        return new Vector_2(this.x - p.getCartesian(0), this.y - p.getCartesian(1));
    }
}