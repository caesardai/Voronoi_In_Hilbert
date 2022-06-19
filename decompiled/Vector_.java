public interface Vector_
{
    double getCartesian(final int p0);
    
    void setCartesian(final int p0, final double p1);
    
    float[] toFloat();
    
    double[] toDouble();
    
    Vector_ sum(final Vector_ p0);
    
    Vector_ difference(final Vector_ p0);
    
    Vector_ opposite();
    
    double innerProduct(final Vector_ p0);
    
    Vector_ divisionByScalar(final double p0);
    
    Vector_ multiplyByScalar(final double p0);
    
    double squaredLength();
    
    int dimension();
    
    String toString();
}