package processing.core;

public interface PMatrix
{
    void reset();
    
    PMatrix get();
    
    float[] get(final float[] p0);
    
    void set(final PMatrix p0);
    
    void set(final float[] p0);
    
    void set(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    void set(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15);
    
    void translate(final float p0, final float p1);
    
    void translate(final float p0, final float p1, final float p2);
    
    void rotate(final float p0);
    
    void rotateX(final float p0);
    
    void rotateY(final float p0);
    
    void rotateZ(final float p0);
    
    void rotate(final float p0, final float p1, final float p2, final float p3);
    
    void scale(final float p0);
    
    void scale(final float p0, final float p1);
    
    void scale(final float p0, final float p1, final float p2);
    
    void shearX(final float p0);
    
    void shearY(final float p0);
    
    void apply(final PMatrix p0);
    
    void apply(final PMatrix2D p0);
    
    void apply(final PMatrix3D p0);
    
    void apply(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    void apply(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15);
    
    void preApply(final PMatrix2D p0);
    
    void preApply(final PMatrix3D p0);
    
    void preApply(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    void preApply(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15);
    
    PVector mult(final PVector p0, final PVector p1);
    
    float[] mult(final float[] p0, final float[] p1);
    
    void transpose();
    
    boolean invert();
    
    float determinant();
}