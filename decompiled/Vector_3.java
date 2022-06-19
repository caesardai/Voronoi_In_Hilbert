public class Vector_3 extends Vector_2
{
    public double z;
    
    public Vector_3() {
    }
    
    public Vector_3(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector_3(final double[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
    }
    
    public Vector_3(final Point_3 a, final Point_3 b) {
        this.x = b.getX() - a.getX();
        this.y = b.getY() - a.getY();
        this.z = b.getZ() - a.getZ();
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    @Override
    public boolean equals(final Vector_ v) {
        return this.x == v.getCartesian(0) && this.y == v.getCartesian(1) && this.z == v.getCartesian(2);
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
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
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
    public Vector_3 sum(final Vector_ v) {
        return new Vector_3(this.x + v.getCartesian(0), this.y + v.getCartesian(1), this.z + v.getCartesian(2));
    }
    
    @Override
    public Vector_3 difference(final Vector_ v) {
        return new Vector_3(v.getCartesian(0) - this.x, v.getCartesian(1) - this.y, v.getCartesian(2) - this.z);
    }
    
    @Override
    public Vector_3 opposite() {
        return new Vector_3(-this.x, -this.y, -this.z);
    }
    
    @Override
    public double innerProduct(final Vector_ v) {
        return this.x * v.getCartesian(0) + this.y * v.getCartesian(1) + this.z * v.getCartesian(2);
    }
    
    @Override
    public Vector_3 divisionByScalar(final double s) {
        if (s == 0.0) {
            throw new Error("error: division by zero");
        }
        return new Vector_3(this.x / s, this.y / s, this.z / s);
    }
    
    @Override
    public Vector_3 multiplyByScalar(final double s) {
        return new Vector_3(this.x * s, this.y * s, this.z * s);
    }
    
    @Override
    public double squaredLength() {
        return this.innerProduct(this);
    }
}