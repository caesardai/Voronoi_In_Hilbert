package processing.core;

import java.io.Serializable;

public class PVector implements Serializable
{
    private static final long serialVersionUID = -6717872085945400694L;
    public float x;
    public float y;
    public float z;
    protected transient float[] array;
    
    public PVector() {
    }
    
    public PVector(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public PVector(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.z = 0.0f;
    }
    
    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(final PVector pVector) {
        this.x = pVector.x;
        this.y = pVector.y;
        this.z = pVector.z;
    }
    
    public void set(final float[] array) {
        if (array.length >= 2) {
            this.x = array[0];
            this.y = array[1];
        }
        if (array.length >= 3) {
            this.z = array[2];
        }
    }
    
    public PVector get() {
        return new PVector(this.x, this.y, this.z);
    }
    
    public float[] get(final float[] array) {
        if (array == null) {
            return new float[] { this.x, this.y, this.z };
        }
        if (array.length >= 2) {
            array[0] = this.x;
            array[1] = this.y;
        }
        if (array.length >= 3) {
            array[2] = this.z;
        }
        return array;
    }
    
    public float mag() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public void add(final PVector pVector) {
        this.x += pVector.x;
        this.y += pVector.y;
        this.z += pVector.z;
    }
    
    public void add(final float n, final float n2, final float n3) {
        this.x += n;
        this.y += n2;
        this.z += n3;
    }
    
    public static PVector add(final PVector pVector, final PVector pVector2) {
        return add(pVector, pVector2, null);
    }
    
    public static PVector add(final PVector pVector, final PVector pVector2, PVector pVector3) {
        if (pVector3 == null) {
            pVector3 = new PVector(pVector.x + pVector2.x, pVector.y + pVector2.y, pVector.z + pVector2.z);
        }
        else {
            pVector3.set(pVector.x + pVector2.x, pVector.y + pVector2.y, pVector.z + pVector2.z);
        }
        return pVector3;
    }
    
    public void sub(final PVector pVector) {
        this.x -= pVector.x;
        this.y -= pVector.y;
        this.z -= pVector.z;
    }
    
    public void sub(final float n, final float n2, final float n3) {
        this.x -= n;
        this.y -= n2;
        this.z -= n3;
    }
    
    public static PVector sub(final PVector pVector, final PVector pVector2) {
        return sub(pVector, pVector2, null);
    }
    
    public static PVector sub(final PVector pVector, final PVector pVector2, PVector pVector3) {
        if (pVector3 == null) {
            pVector3 = new PVector(pVector.x - pVector2.x, pVector.y - pVector2.y, pVector.z - pVector2.z);
        }
        else {
            pVector3.set(pVector.x - pVector2.x, pVector.y - pVector2.y, pVector.z - pVector2.z);
        }
        return pVector3;
    }
    
    public void mult(final float n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }
    
    public static PVector mult(final PVector pVector, final float n) {
        return mult(pVector, n, null);
    }
    
    public static PVector mult(final PVector pVector, final float n, PVector pVector2) {
        if (pVector2 == null) {
            pVector2 = new PVector(pVector.x * n, pVector.y * n, pVector.z * n);
        }
        else {
            pVector2.set(pVector.x * n, pVector.y * n, pVector.z * n);
        }
        return pVector2;
    }
    
    public void mult(final PVector pVector) {
        this.x *= pVector.x;
        this.y *= pVector.y;
        this.z *= pVector.z;
    }
    
    public static PVector mult(final PVector pVector, final PVector pVector2) {
        return mult(pVector, pVector2, null);
    }
    
    public static PVector mult(final PVector pVector, final PVector pVector2, PVector pVector3) {
        if (pVector3 == null) {
            pVector3 = new PVector(pVector.x * pVector2.x, pVector.y * pVector2.y, pVector.z * pVector2.z);
        }
        else {
            pVector3.set(pVector.x * pVector2.x, pVector.y * pVector2.y, pVector.z * pVector2.z);
        }
        return pVector3;
    }
    
    public void div(final float n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
    }
    
    public static PVector div(final PVector pVector, final float n) {
        return div(pVector, n, null);
    }
    
    public static PVector div(final PVector pVector, final float n, PVector pVector2) {
        if (pVector2 == null) {
            pVector2 = new PVector(pVector.x / n, pVector.y / n, pVector.z / n);
        }
        else {
            pVector2.set(pVector.x / n, pVector.y / n, pVector.z / n);
        }
        return pVector2;
    }
    
    public void div(final PVector pVector) {
        this.x /= pVector.x;
        this.y /= pVector.y;
        this.z /= pVector.z;
    }
    
    public static PVector div(final PVector pVector, final PVector pVector2) {
        return div(pVector, pVector2, null);
    }
    
    public static PVector div(final PVector pVector, final PVector pVector2, PVector pVector3) {
        if (pVector3 == null) {
            pVector3 = new PVector(pVector.x / pVector2.x, pVector.y / pVector2.y, pVector.z / pVector2.z);
        }
        else {
            pVector3.set(pVector.x / pVector2.x, pVector.y / pVector2.y, pVector.z / pVector2.z);
        }
        return pVector3;
    }
    
    public float dist(final PVector pVector) {
        final float n = this.x - pVector.x;
        final float n2 = this.y - pVector.y;
        final float n3 = this.z - pVector.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public static float dist(final PVector pVector, final PVector pVector2) {
        final float n = pVector.x - pVector2.x;
        final float n2 = pVector.y - pVector2.y;
        final float n3 = pVector.z - pVector2.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public float dot(final PVector pVector) {
        return this.x * pVector.x + this.y * pVector.y + this.z * pVector.z;
    }
    
    public float dot(final float n, final float n2, final float n3) {
        return this.x * n + this.y * n2 + this.z * n3;
    }
    
    public static float dot(final PVector pVector, final PVector pVector2) {
        return pVector.x * pVector2.x + pVector.y * pVector2.y + pVector.z * pVector2.z;
    }
    
    public PVector cross(final PVector pVector) {
        return this.cross(pVector, null);
    }
    
    public PVector cross(final PVector pVector, PVector pVector2) {
        final float n = this.y * pVector.z - pVector.y * this.z;
        final float n2 = this.z * pVector.x - pVector.z * this.x;
        final float n3 = this.x * pVector.y - pVector.x * this.y;
        if (pVector2 == null) {
            pVector2 = new PVector(n, n2, n3);
        }
        else {
            pVector2.set(n, n2, n3);
        }
        return pVector2;
    }
    
    public static PVector cross(final PVector pVector, final PVector pVector2, PVector pVector3) {
        final float n = pVector.y * pVector2.z - pVector2.y * pVector.z;
        final float n2 = pVector.z * pVector2.x - pVector2.z * pVector.x;
        final float n3 = pVector.x * pVector2.y - pVector2.x * pVector.y;
        if (pVector3 == null) {
            pVector3 = new PVector(n, n2, n3);
        }
        else {
            pVector3.set(n, n2, n3);
        }
        return pVector3;
    }
    
    public void normalize() {
        final float mag = this.mag();
        if (mag != 0.0f && mag != 1.0f) {
            this.div(mag);
        }
    }
    
    public PVector normalize(PVector pVector) {
        if (pVector == null) {
            pVector = new PVector();
        }
        final float mag = this.mag();
        if (mag > 0.0f) {
            pVector.set(this.x / mag, this.y / mag, this.z / mag);
        }
        else {
            pVector.set(this.x, this.y, this.z);
        }
        return pVector;
    }
    
    public void limit(final float n) {
        if (this.mag() > n) {
            this.normalize();
            this.mult(n);
        }
    }
    
    public void scaleTo(final float n) {
        this.normalize();
        this.mult(n);
    }
    
    public PVector scaleTo(PVector normalize, final float n) {
        normalize = this.normalize(normalize);
        normalize.mult(n);
        return normalize;
    }
    
    public float heading2D() {
        return -1.0f * (float)Math.atan2(-this.y, this.x);
    }
    
    public void rotate(final float n) {
        final float x = this.x;
        this.x = this.x * PApplet.cos(n) - this.y * PApplet.sin(n);
        this.y = x * PApplet.sin(n) + this.y * PApplet.cos(n);
    }
    
    public static float angleBetween(final PVector pVector, final PVector pVector2) {
        final double a = (pVector.x * pVector2.x + pVector.y * pVector2.y + pVector.z * pVector2.z) / (Math.sqrt(pVector.x * pVector.x + pVector.y * pVector.y + pVector.z * pVector.z) * Math.sqrt(pVector2.x * pVector2.x + pVector2.y * pVector2.y + pVector2.z * pVector2.z));
        if (a <= -1.0) {
            return 3.1415927f;
        }
        if (a >= 1.0) {
            return 0.0f;
        }
        return (float)Math.acos(a);
    }
    
    @Override
    public String toString() {
        return "[ " + this.x + ", " + this.y + ", " + this.z + " ]";
    }
    
    public float[] array() {
        if (this.array == null) {
            this.array = new float[3];
        }
        this.array[0] = this.x;
        this.array[1] = this.y;
        this.array[2] = this.z;
        return this.array;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PVector)) {
            return false;
        }
        final PVector pVector = (PVector)o;
        return this.x == pVector.x && this.y == pVector.y && this.z == pVector.z;
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 * (31 * 1 + Float.floatToIntBits(this.x)) + Float.floatToIntBits(this.y)) + Float.floatToIntBits(this.z);
    }
}