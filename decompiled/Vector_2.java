public class Vector_2 implements Vector_
{
    public double x;
    public double y;
    
    public Vector_2() {
    }
    
    public Vector_2(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector_2(final double[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
    }
    
    public Vector_2(final Point_2 a, final Point_2 b) {
        this.x = b.getX() - a.getX();
        this.y = b.getY() - a.getY();
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public void setY(final double y) {
        this.y = y;
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
    
    public boolean equals(final Vector_ v) {
        return this.x == v.getCartesian(0) && this.y == v.getCartesian(1);
    }
    
    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
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
    public Vector_2 sum(final Vector_ v) {
        return new Vector_2(this.x + v.getCartesian(0), this.y + v.getCartesian(1));
    }
    
    @Override
    public Vector_2 difference(final Vector_ v) {
        return new Vector_2(v.getCartesian(0) - this.x, v.getCartesian(1) - this.y);
    }
    
    @Override
    public Vector_2 opposite() {
        return new Vector_2(-this.x, -this.y);
    }
    
    @Override
    public double innerProduct(final Vector_ v) {
        return this.x * v.getCartesian(0) + this.y * v.getCartesian(1);
    }
    
    @Override
    public Vector_2 divisionByScalar(final double s) {
        return new Vector_2(this.x / s, this.y / s);
    }
    
    @Override
    public Vector_2 multiplyByScalar(final double s) {
        return new Vector_2(this.x * s, this.y * s);
    }
    
    @Override
    public double squaredLength() {
        return this.innerProduct(this);
    }
    
    public double crossProduct(final Vector_ v) {
        return this.x * v.getCartesian(1) - this.y * v.getCartesian(0);
    }
}