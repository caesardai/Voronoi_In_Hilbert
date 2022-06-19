public class Point_3 extends Point_2
{
    public double z;
    
    public Point_3() {
    }
    
    public Point_3(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point_3(final double[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
    }
    
    public Point_3(final Point_3 p) {
        this.x = p.getCartesian(0);
        this.y = p.getCartesian(1);
        this.z = p.getCartesian(2);
    }
    
    @Override
    public void barycenter(final Point_[] points) {
        double x_ = 0.0;
        double y_ = 0.0;
        double z_ = 0.0;
        for (int i = 0; i < points.length; ++i) {
            x_ += points[i].getCartesian(0);
            y_ += points[i].getCartesian(1);
            z_ += points[i].getCartesian(2);
        }
        this.x = x_ / points.length;
        this.y = y_ / points.length;
        this.z = z_ / points.length;
    }
    
    public void linearCombination(final Point_[] points, final double[] coefficients) {
        double x_ = 0.0;
        double y_ = 0.0;
        double z_ = 0.0;
        for (int i = 0; i < points.length; ++i) {
            x_ += points[i].getCartesian(0) * coefficients[i];
            y_ += points[i].getCartesian(1) * coefficients[i];
            z_ += points[i].getCartesian(2) * coefficients[i];
        }
        this.x = x_;
        this.y = y_;
        this.z = z_;
    }
    
    public static Point_3 linearCombination(final Point_3[] points, final double[] coefficients) {
        double x_ = 0.0;
        double y_ = 0.0;
        double z_ = 0.0;
        for (int i = 0; i < points.length; ++i) {
            x_ += points[i].getX() * coefficients[i];
            y_ += points[i].getY() * coefficients[i];
            z_ += points[i].getZ() * coefficients[i];
        }
        return new Point_3(x_, y_, z_);
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    @Override
    public float[] toFloat() {
        final float[] result = { (float)this.x, (float)this.y, (float)this.z };
        return result;
    }
    
    @Override
    public double[] toDouble() {
        final double[] result = { this.x, this.y, this.z };
        return result;
    }
    
    @Override
    public Point_ toCartesian() {
        if (this.z == 0.0) {
            throw new Error("Error: division by 0");
        }
        return new Point_2(this.x / this.z, this.y / this.z);
    }
    
    @Override
    public void translateOf(final Vector_ v) {
        this.x += v.getCartesian(0);
        this.y += v.getCartesian(1);
        this.z += v.getCartesian(2);
    }
    
    public void multiply(final double n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }
    
    @Override
    public boolean equals(final Object o) {
        final Point_ p = (Point_)o;
        return this.x == p.getCartesian(0) && this.y == p.getCartesian(1) && this.z == p.getCartesian(2);
    }
    
    public double distanceFrom(final Point_3 p) {
        final double dX = p.getX() - this.x;
        final double dY = p.getY() - this.y;
        final double dZ = p.getZ() - this.z;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }
    
    public double squareDistance(final Point_3 p) {
        final double dX = p.getX() - this.x;
        final double dY = p.getY() - this.y;
        final double dZ = p.getZ() - this.z;
        return dX * dX + dY * dY + dZ * dZ;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }
    
    @Override
    public int dimension() {
        return 3;
    }
    
    @Override
    public double getCartesian(final int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        return this.z;
    }
    
    @Override
    public void setCartesian(final int i, final double x) {
        if (i == 0) {
            this.x = x;
        }
        else if (i == 1) {
            this.y = x;
        }
        else {
            this.z = x;
        }
    }
    
    @Override
    public void setOrigin() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }
    
    @Override
    public Point_3 sum(final Vector_ v) {
        return new Point_3(this.x + v.getCartesian(0), this.y + v.getCartesian(1), this.z + v.getCartesian(2));
    }
    
    @Override
    public Point_3 multiplyByScalar(final double s) {
        return new Point_3(this.x * s, this.y * s, this.z * s);
    }
    
    @Override
    public int compareTo(final Point_ o) {
        final Point_3 p = (Point_3)o;
        if (this.getX() < p.getX()) {
            return -1;
        }
        if (this.getX() > p.getX()) {
            return 1;
        }
        if (this.getY() < p.getY()) {
            return -1;
        }
        if (this.getY() > p.getY()) {
            return 1;
        }
        if (this.getZ() < p.getZ()) {
            return -1;
        }
        if (this.getZ() > p.getZ()) {
            return 1;
        }
        return 0;
    }
    
    public Point_3 crossProduct(final Point_3 q) {
        return new Point_3(this.getY() * q.getZ() - this.getZ() * q.getY(), this.getZ() * q.getX() - this.getX() * q.getZ(), this.getX() * q.getY() - this.getY() * q.getX());
    }
    
    public double scalarProduct(final Point_3 q) {
        return this.getX() * q.getX() + this.getY() * q.getY() + this.getZ() * q.getZ();
    }
}