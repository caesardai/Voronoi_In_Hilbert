package processing.core;

public class PMatrix2D implements PMatrix
{
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    
    public PMatrix2D() {
        this.reset();
    }
    
    public PMatrix2D(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.set(n, n2, n3, n4, n5, n6);
    }
    
    public PMatrix2D(final PMatrix pMatrix) {
        this.set(pMatrix);
    }
    
    public void reset() {
        this.set(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }
    
    public PMatrix2D get() {
        final PMatrix2D pMatrix2D = new PMatrix2D();
        pMatrix2D.set(this);
        return pMatrix2D;
    }
    
    public float[] get(float[] array) {
        if (array == null || array.length != 6) {
            array = new float[6];
        }
        array[0] = this.m00;
        array[1] = this.m01;
        array[2] = this.m02;
        array[3] = this.m10;
        array[4] = this.m11;
        array[5] = this.m12;
        return array;
    }
    
    public void set(final PMatrix pMatrix) {
        if (pMatrix instanceof PMatrix2D) {
            final PMatrix2D pMatrix2D = (PMatrix2D)pMatrix;
            this.set(pMatrix2D.m00, pMatrix2D.m01, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, pMatrix2D.m12);
            return;
        }
        throw new IllegalArgumentException("PMatrix2D.set() only accepts PMatrix2D objects.");
    }
    
    public void set(final PMatrix3D pMatrix3D) {
    }
    
    public void set(final float[] array) {
        this.m00 = array[0];
        this.m01 = array[1];
        this.m02 = array[2];
        this.m10 = array[3];
        this.m11 = array[4];
        this.m12 = array[5];
    }
    
    public void set(final float m00, final float m2, final float m3, final float m4, final float m5, final float m6) {
        this.m00 = m00;
        this.m01 = m2;
        this.m02 = m3;
        this.m10 = m4;
        this.m11 = m5;
        this.m12 = m6;
    }
    
    public void set(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
    }
    
    public void translate(final float n, final float n2) {
        this.m02 += n * this.m00 + n2 * this.m01;
        this.m12 += n * this.m10 + n2 * this.m11;
    }
    
    public void translate(final float n, final float n2, final float n3) {
        throw new IllegalArgumentException("Cannot use translate(x, y, z) on a PMatrix2D.");
    }
    
    public void rotate(final float n) {
        final float sin = this.sin(n);
        final float cos = this.cos(n);
        final float m00 = this.m00;
        final float m2 = this.m01;
        this.m00 = cos * m00 + sin * m2;
        this.m01 = -sin * m00 + cos * m2;
        final float m3 = this.m10;
        final float m4 = this.m11;
        this.m10 = cos * m3 + sin * m4;
        this.m11 = -sin * m3 + cos * m4;
    }
    
    public void rotateX(final float n) {
        throw new IllegalArgumentException("Cannot use rotateX() on a PMatrix2D.");
    }
    
    public void rotateY(final float n) {
        throw new IllegalArgumentException("Cannot use rotateY() on a PMatrix2D.");
    }
    
    public void rotateZ(final float n) {
        this.rotate(n);
    }
    
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        throw new IllegalArgumentException("Cannot use this version of rotate() on a PMatrix2D.");
    }
    
    public void scale(final float n) {
        this.scale(n, n);
    }
    
    public void scale(final float n, final float n2) {
        this.m00 *= n;
        this.m01 *= n2;
        this.m10 *= n;
        this.m11 *= n2;
    }
    
    public void scale(final float n, final float n2, final float n3) {
        throw new IllegalArgumentException("Cannot use this version of scale() on a PMatrix2D.");
    }
    
    public void shearX(final float n) {
        this.apply(1.0f, 0.0f, 1.0f, this.tan(n), 0.0f, 0.0f);
    }
    
    public void shearY(final float n) {
        this.apply(1.0f, 0.0f, 1.0f, 0.0f, this.tan(n), 0.0f);
    }
    
    public void apply(final PMatrix pMatrix) {
        if (pMatrix instanceof PMatrix2D) {
            this.apply((PMatrix2D)pMatrix);
        }
        else if (pMatrix instanceof PMatrix3D) {
            this.apply((PMatrix3D)pMatrix);
        }
    }
    
    public void apply(final PMatrix2D pMatrix2D) {
        this.apply(pMatrix2D.m00, pMatrix2D.m01, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, pMatrix2D.m12);
    }
    
    public void apply(final PMatrix3D pMatrix3D) {
        throw new IllegalArgumentException("Cannot use apply(PMatrix3D) on a PMatrix2D.");
    }
    
    public void apply(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float m00 = this.m00;
        final float m2 = this.m01;
        this.m00 = n * m00 + n4 * m2;
        this.m01 = n2 * m00 + n5 * m2;
        this.m02 += n3 * m00 + n6 * m2;
        final float m3 = this.m10;
        final float m4 = this.m11;
        this.m10 = n * m3 + n4 * m4;
        this.m11 = n2 * m3 + n5 * m4;
        this.m12 += n3 * m3 + n6 * m4;
    }
    
    public void apply(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        throw new IllegalArgumentException("Cannot use this version of apply() on a PMatrix2D.");
    }
    
    public void preApply(final PMatrix2D pMatrix2D) {
        this.preApply(pMatrix2D.m00, pMatrix2D.m01, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, pMatrix2D.m12);
    }
    
    public void preApply(final PMatrix3D pMatrix3D) {
        throw new IllegalArgumentException("Cannot use preApply(PMatrix3D) on a PMatrix2D.");
    }
    
    public void preApply(final float n, final float n2, float m02, final float n3, final float n4, float m3) {
        final float m4 = this.m02;
        final float m5 = this.m12;
        m02 += m4 * n + m5 * n2;
        m3 += m4 * n3 + m5 * n4;
        this.m02 = m02;
        this.m12 = m3;
        final float m6 = this.m00;
        final float m7 = this.m10;
        this.m00 = m6 * n + m7 * n2;
        this.m10 = m6 * n3 + m7 * n4;
        final float m8 = this.m01;
        final float m9 = this.m11;
        this.m01 = m8 * n + m9 * n2;
        this.m11 = m8 * n3 + m9 * n4;
    }
    
    public void preApply(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        throw new IllegalArgumentException("Cannot use this version of preApply() on a PMatrix2D.");
    }
    
    public PVector mult(final PVector pVector, PVector pVector2) {
        if (pVector2 == null) {
            pVector2 = new PVector();
        }
        pVector2.x = this.m00 * pVector.x + this.m01 * pVector.y + this.m02;
        pVector2.y = this.m10 * pVector.x + this.m11 * pVector.y + this.m12;
        return pVector2;
    }
    
    public float[] mult(final float[] array, float[] array2) {
        if (array2 == null || array2.length != 2) {
            array2 = new float[2];
        }
        if (array == array2) {
            final float n = this.m00 * array[0] + this.m01 * array[1] + this.m02;
            final float n2 = this.m10 * array[0] + this.m11 * array[1] + this.m12;
            array2[0] = n;
            array2[1] = n2;
        }
        else {
            array2[0] = this.m00 * array[0] + this.m01 * array[1] + this.m02;
            array2[1] = this.m10 * array[0] + this.m11 * array[1] + this.m12;
        }
        return array2;
    }
    
    public float multX(final float n, final float n2) {
        return this.m00 * n + this.m01 * n2 + this.m02;
    }
    
    public float multY(final float n, final float n2) {
        return this.m10 * n + this.m11 * n2 + this.m12;
    }
    
    public void transpose() {
    }
    
    public boolean invert() {
        final float determinant = this.determinant();
        if (Math.abs(determinant) <= Float.MIN_VALUE) {
            return false;
        }
        final float m00 = this.m00;
        final float m2 = this.m01;
        final float m3 = this.m02;
        final float m4 = this.m10;
        final float m5 = this.m11;
        final float m6 = this.m12;
        this.m00 = m5 / determinant;
        this.m10 = -m4 / determinant;
        this.m01 = -m2 / determinant;
        this.m11 = m00 / determinant;
        this.m02 = (m2 * m6 - m5 * m3) / determinant;
        this.m12 = (m4 * m3 - m00 * m6) / determinant;
        return true;
    }
    
    public float determinant() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public void print() {
        int n = (int)this.abs(this.max(PApplet.max(this.abs(this.m00), this.abs(this.m01), this.abs(this.m02)), PApplet.max(this.abs(this.m10), this.abs(this.m11), this.abs(this.m12))));
        int n2 = 1;
        if (Float.isNaN((float)n) || Float.isInfinite((float)n)) {
            n2 = 5;
        }
        else {
            while ((n /= 10) != 0) {
                ++n2;
            }
        }
        System.out.println(PApplet.nfs(this.m00, n2, 4) + " " + PApplet.nfs(this.m01, n2, 4) + " " + PApplet.nfs(this.m02, n2, 4));
        System.out.println(PApplet.nfs(this.m10, n2, 4) + " " + PApplet.nfs(this.m11, n2, 4) + " " + PApplet.nfs(this.m12, n2, 4));
        System.out.println();
    }
    
    protected boolean isIdentity() {
        return this.m00 == 1.0f && this.m01 == 0.0f && this.m02 == 0.0f && this.m10 == 0.0f && this.m11 == 1.0f && this.m12 == 0.0f;
    }
    
    protected boolean isWarped() {
        return this.m00 != 1.0f || (this.m01 != 0.0f && this.m10 != 0.0f) || this.m11 != 1.0f;
    }
    
    private final float max(final float n, final float n2) {
        return (n > n2) ? n : n2;
    }
    
    private final float abs(final float n) {
        return (n < 0.0f) ? (-n) : n;
    }
    
    private final float sin(final float n) {
        return (float)Math.sin(n);
    }
    
    private final float cos(final float n) {
        return (float)Math.cos(n);
    }
    
    private final float tan(final float n) {
        return (float)Math.tan(n);
    }
}
 