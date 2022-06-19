public interface Point_ extends Comparable<Point_>
{
    double getCartesian(final int p0);
    
    void setCartesian(final int p0, final double p1);
    
    void setOrigin();
    
    float[] toFloat();
    
    double[] toDouble();
    
    void translateOf(final Vector_ p0);
    
    Vector_ minus(final Point_ p0);
    
    void barycenter(final Point_[] p0);
    
    int dimension();
    
    String toString();
}