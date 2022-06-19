package processing.core;

import java.util.HashMap;

public class PShape implements PConstants
{
    protected String name;
    protected HashMap<String, PShape> nameTable;
    public static final int GROUP = 0;
    public static final int PRIMITIVE = 1;
    public static final int PATH = 2;
    public static final int GEOMETRY = 3;
    protected int family;
    protected int primitive;
    protected PMatrix matrix;
    protected PImage image;
    public float width;
    public float height;
    public float depth;
    protected boolean visible;
    protected boolean stroke;
    protected int strokeColor;
    protected float strokeWeight;
    protected int strokeCap;
    protected int strokeJoin;
    protected boolean fill;
    protected int fillColor;
    protected boolean style;
    protected float[] params;
    protected int vertexCount;
    protected float[][] vertices;
    public static final int VERTEX = 0;
    public static final int BEZIER_VERTEX = 1;
    public static final int QUAD_BEZIER_VERTEX = 2;
    public static final int CURVE_VERTEX = 3;
    public static final int BREAK = 4;
    protected int vertexCodeCount;
    protected int[] vertexCodes;
    protected boolean close;
    protected PShape parent;
    protected int childCount;
    protected PShape[] children;
    
    public PShape() {
        this.visible = true;
        this.style = true;
        this.family = 0;
    }
    
    public PShape(final int family) {
        this.visible = true;
        this.style = true;
        this.family = family;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public void disableStyle() {
        this.style = false;
        for (int i = 0; i < this.childCount; ++i) {
            this.children[i].disableStyle();
        }
    }
    
    public void enableStyle() {
        this.style = true;
        for (int i = 0; i < this.childCount; ++i) {
            this.children[i].enableStyle();
        }
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public float getDepth() {
        return this.depth;
    }
    
    public boolean is3D() {
        return false;
    }
    
    protected void pre(final PGraphics pGraphics) {
        if (this.matrix != null) {
            pGraphics.pushMatrix();
            pGraphics.applyMatrix(this.matrix);
        }
        if (this.style) {
            pGraphics.pushStyle();
            this.styles(pGraphics);
        }
    }
    
    protected void styles(final PGraphics pGraphics) {
        if (this.stroke) {
            pGraphics.stroke(this.strokeColor);
            pGraphics.strokeWeight(this.strokeWeight);
            pGraphics.strokeCap(this.strokeCap);
            pGraphics.strokeJoin(this.strokeJoin);
        }
        else {
            pGraphics.noStroke();
        }
        if (this.fill) {
            pGraphics.fill(this.fillColor);
        }
        else {
            pGraphics.noFill();
        }
    }
    
    public void post(final PGraphics pGraphics) {
        if (this.matrix != null) {
            pGraphics.popMatrix();
        }
        if (this.style) {
            pGraphics.popStyle();
        }
    }
    
    public void draw(final PGraphics pGraphics) {
        if (this.visible) {
            this.pre(pGraphics);
            this.drawImpl(pGraphics);
            this.post(pGraphics);
        }
    }
    
    public void drawImpl(final PGraphics pGraphics) {
        if (this.family == 0) {
            this.drawGroup(pGraphics);
        }
        else if (this.family == 1) {
            this.drawPrimitive(pGraphics);
        }
        else if (this.family == 3) {
            this.drawGeometry(pGraphics);
        }
        else if (this.family == 2) {
            this.drawPath(pGraphics);
        }
    }
    
    protected void drawGroup(final PGraphics pGraphics) {
        for (int i = 0; i < this.childCount; ++i) {
            this.children[i].draw(pGraphics);
        }
    }
    
    protected void drawPrimitive(final PGraphics pGraphics) {
        if (this.primitive == 2) {
            pGraphics.point(this.params[0], this.params[1]);
        }
        else if (this.primitive == 4) {
            if (this.params.length == 4) {
                pGraphics.line(this.params[0], this.params[1], this.params[2], this.params[3]);
            }
            else {
                pGraphics.line(this.params[0], this.params[1], this.params[2], this.params[3], this.params[4], this.params[5]);
            }
        }
        else if (this.primitive == 8) {
            pGraphics.triangle(this.params[0], this.params[1], this.params[2], this.params[3], this.params[4], this.params[5]);
        }
        else if (this.primitive == 16) {
            pGraphics.quad(this.params[0], this.params[1], this.params[2], this.params[3], this.params[4], this.params[5], this.params[6], this.params[7]);
        }
        else if (this.primitive == 30) {
            if (this.image != null) {
                pGraphics.imageMode(0);
                pGraphics.image(this.image, this.params[0], this.params[1], this.params[2], this.params[3]);
            }
            else {
                pGraphics.rectMode(0);
                pGraphics.rect(this.params[0], this.params[1], this.params[2], this.params[3]);
            }
        }
        else if (this.primitive == 31) {
            pGraphics.ellipseMode(0);
            pGraphics.ellipse(this.params[0], this.params[1], this.params[2], this.params[3]);
        }
        else if (this.primitive == 32) {
            pGraphics.ellipseMode(0);
            pGraphics.arc(this.params[0], this.params[1], this.params[2], this.params[3], this.params[4], this.params[5]);
        }
        else if (this.primitive == 41) {
            if (this.params.length == 1) {
                pGraphics.box(this.params[0]);
            }
            else {
                pGraphics.box(this.params[0], this.params[1], this.params[2]);
            }
        }
        else if (this.primitive == 40) {
            pGraphics.sphere(this.params[0]);
        }
    }
    
    protected void drawGeometry(final PGraphics pGraphics) {
        pGraphics.beginShape(this.primitive);
        if (this.style) {
            for (int i = 0; i < this.vertexCount; ++i) {
                pGraphics.vertex(this.vertices[i]);
            }
        }
        else {
            for (int j = 0; j < this.vertexCount; ++j) {
                final float[] array = this.vertices[j];
                if (array[2] == 0.0f) {
                    pGraphics.vertex(array[0], array[1]);
                }
                else {
                    pGraphics.vertex(array[0], array[1], array[2]);
                }
            }
        }
        pGraphics.endShape();
    }
    
    protected void drawPath(final PGraphics pGraphics) {
        if (this.vertices == null) {
            return;
        }
        pGraphics.beginShape();
        if (this.vertexCodeCount == 0) {
            if (this.vertices[0].length == 2) {
                for (int i = 0; i < this.vertexCount; ++i) {
                    pGraphics.vertex(this.vertices[i][0], this.vertices[i][1]);
                }
            }
            else {
                for (int j = 0; j < this.vertexCount; ++j) {
                    pGraphics.vertex(this.vertices[j][0], this.vertices[j][1], this.vertices[j][2]);
                }
            }
        }
        else {
            int n = 0;
            if (this.vertices[0].length == 2) {
                for (int k = 0; k < this.vertexCodeCount; ++k) {
                    switch (this.vertexCodes[k]) {
                        case 0: {
                            pGraphics.vertex(this.vertices[n][0], this.vertices[n][1]);
                            ++n;
                            break;
                        }
                        case 2: {
                            pGraphics.quadVertex(this.vertices[n + 0][0], this.vertices[n + 0][1], this.vertices[n + 1][0], this.vertices[n + 1][1]);
                            n += 2;
                            break;
                        }
                        case 1: {
                            pGraphics.bezierVertex(this.vertices[n + 0][0], this.vertices[n + 0][1], this.vertices[n + 1][0], this.vertices[n + 1][1], this.vertices[n + 2][0], this.vertices[n + 2][1]);
                            n += 3;
                            break;
                        }
                        case 3: {
                            pGraphics.curveVertex(this.vertices[n][0], this.vertices[n][1]);
                            ++n;
                        }
                        case 4: {
                            pGraphics.breakShape();
                            break;
                        }
                    }
                }
            }
            else {
                for (int l = 0; l < this.vertexCodeCount; ++l) {
                    switch (this.vertexCodes[l]) {
                        case 0: {
                            pGraphics.vertex(this.vertices[n][0], this.vertices[n][1], this.vertices[n][2]);
                            ++n;
                            break;
                        }
                        case 2: {
                            pGraphics.quadVertex(this.vertices[n + 0][0], this.vertices[n + 0][1], this.vertices[n + 0][2], this.vertices[n + 1][0], this.vertices[n + 1][1], this.vertices[n + 0][2]);
                            n += 2;
                            break;
                        }
                        case 1: {
                            pGraphics.bezierVertex(this.vertices[n + 0][0], this.vertices[n + 0][1], this.vertices[n + 0][2], this.vertices[n + 1][0], this.vertices[n + 1][1], this.vertices[n + 1][2], this.vertices[n + 2][0], this.vertices[n + 2][1], this.vertices[n + 2][2]);
                            n += 3;
                            break;
                        }
                        case 3: {
                            pGraphics.curveVertex(this.vertices[n][0], this.vertices[n][1], this.vertices[n][2]);
                            ++n;
                        }
                        case 4: {
                            pGraphics.breakShape();
                            break;
                        }
                    }
                }
            }
        }
        pGraphics.endShape(this.close ? 2 : 1);
    }
    
    public PShape getParent() {
        return this.parent;
    }
    
    public int getChildCount() {
        return this.childCount;
    }
    
    public PShape[] getChildren() {
        return this.children;
    }
    
    public PShape getChild(final int n) {
        return this.children[n];
    }
    
    public PShape getChild(final String s) {
        if (this.name != null && this.name.equals(s)) {
            return this;
        }
        if (this.nameTable != null) {
            final PShape pShape = this.nameTable.get(s);
            if (pShape != null) {
                return pShape;
            }
        }
        for (int i = 0; i < this.childCount; ++i) {
            final PShape child = this.children[i].getChild(s);
            if (child != null) {
                return child;
            }
        }
        return null;
    }
    
    public PShape findChild(final String s) {
        if (this.parent == null) {
            return this.getChild(s);
        }
        return this.parent.findChild(s);
    }
    
    public void addChild(final PShape pShape) {
        if (this.children == null) {
            this.children = new PShape[1];
        }
        if (this.childCount == this.children.length) {
            this.children = (PShape[])PApplet.expand((Object)this.children);
        }
        this.children[this.childCount++] = pShape;
        pShape.parent = this;
        if (pShape.getName() != null) {
            this.addName(pShape.getName(), pShape);
        }
    }
    
    public void addChild(final PShape pShape, final int n) {
        if (n < this.childCount) {
            if (this.childCount == this.children.length) {
                this.children = (PShape[])PApplet.expand((Object)this.children);
            }
            for (int i = this.childCount - 1; i >= n; --i) {
                this.children[i + 1] = this.children[i];
            }
            ++this.childCount;
            this.children[n] = pShape;
            pShape.parent = this;
            if (pShape.getName() != null) {
                this.addName(pShape.getName(), pShape);
            }
        }
    }
    
    public void removeChild(final int n) {
        if (n < this.childCount) {
            final PShape pShape = this.children[n];
            for (int i = n; i < this.childCount - 1; ++i) {
                this.children[i] = this.children[i + 1];
            }
            --this.childCount;
            if (pShape.getName() != null && this.nameTable != null) {
                this.nameTable.remove(pShape.getName());
            }
        }
    }
    
    public void addName(final String key, final PShape value) {
        if (this.parent != null) {
            this.parent.addName(key, value);
        }
        else {
            if (this.nameTable == null) {
                this.nameTable = new HashMap<String, PShape>();
            }
            this.nameTable.put(key, value);
        }
    }
    
    public int getChildIndex(final PShape pShape) {
        for (int i = 0; i < this.childCount; ++i) {
            if (this.children[i] == pShape) {
                return i;
            }
        }
        return -1;
    }
    
    public int getFamily() {
        return this.family;
    }
    
    public int getPrimitive() {
        return this.primitive;
    }
    
    public float[] getParams() {
        return this.getParams(null);
    }
    
    public float[] getParams(float[] array) {
        if (array == null || array.length != this.params.length) {
            array = new float[this.params.length];
        }
        PApplet.arrayCopy((Object)this.params, (Object)array);
        return array;
    }
    
    public float getParam(final int n) {
        return this.params[n];
    }
    
    public int getVertexCount() {
        return this.vertexCount;
    }
    
    public float[] getVertex(final int i) {
        if (i < 0 || i >= this.vertexCount) {
            throw new IllegalArgumentException("No vertex " + i + " for this shape, " + "only vertices 0 through " + (this.vertexCount - 1) + ".");
        }
        return this.vertices[i];
    }
    
    public float getVertexX(final int n) {
        return this.vertices[n][0];
    }
    
    public float getVertexY(final int n) {
        return this.vertices[n][1];
    }
    
    public float getVertexZ(final int n) {
        return this.vertices[n][2];
    }
    
    public int[] getVertexCodes() {
        if (this.vertexCodes == null) {
            return null;
        }
        if (this.vertexCodes.length != this.vertexCodeCount) {
            this.vertexCodes = PApplet.subset(this.vertexCodes, 0, this.vertexCodeCount);
        }
        return this.vertexCodes;
    }
    
    public int getVertexCodeCount() {
        return this.vertexCodeCount;
    }
    
    public int getVertexCode(final int n) {
        return this.vertexCodes[n];
    }
    
    public boolean isClosed() {
        return this.close;
    }
    
    public boolean contains(final float n, final float n2) {
        if (this.family == 2) {
            boolean b = false;
            int i = 0;
            int n3 = this.vertexCount - 1;
            while (i < this.vertexCount) {
                if (this.vertices[i][1] > n2 != this.vertices[n3][1] > n2 && n < (this.vertices[n3][0] - this.vertices[i][0]) * (n2 - this.vertices[i][1]) / (this.vertices[n3][1] - this.vertices[i][1]) + this.vertices[i][0]) {
                    b = !b;
                }
                n3 = i++;
            }
            return b;
        }
        throw new IllegalArgumentException("The contains() method is only implemented for paths.");
    }
    
    public void translate(final float n, final float n2) {
        this.checkMatrix(2);
        this.matrix.translate(n, n2);
    }
    
    public void translate(final float n, final float n2, final float n3) {
        this.checkMatrix(3);
        this.matrix.translate(n, n2, n3);
    }
    
    public void rotateX(final float n) {
        this.rotate(n, 1.0f, 0.0f, 0.0f);
    }
    
    public void rotateY(final float n) {
        this.rotate(n, 0.0f, 1.0f, 0.0f);
    }
    
    public void rotateZ(final float n) {
        this.rotate(n, 0.0f, 0.0f, 1.0f);
    }
    
    public void rotate(final float n) {
        this.checkMatrix(2);
        this.matrix.rotate(n);
    }
    
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        this.checkMatrix(3);
        this.matrix.rotate(n, n2, n3, n4);
    }
    
    public void scale(final float n) {
        this.checkMatrix(2);
        this.matrix.scale(n);
    }
    
    public void scale(final float n, final float n2) {
        this.checkMatrix(2);
        this.matrix.scale(n, n2);
    }
    
    public void scale(final float n, final float n2, final float n3) {
        this.checkMatrix(3);
        this.matrix.scale(n, n2, n3);
    }
    
    public void resetMatrix() {
        this.checkMatrix(2);
        this.matrix.reset();
    }
    
    public void applyMatrix(final PMatrix pMatrix) {
        if (pMatrix instanceof PMatrix2D) {
            this.applyMatrix((PMatrix2D)pMatrix);
        }
        else if (pMatrix instanceof PMatrix3D) {
            this.applyMatrix(pMatrix);
        }
    }
    
    public void applyMatrix(final PMatrix2D pMatrix2D) {
        this.applyMatrix(pMatrix2D.m00, pMatrix2D.m01, 0.0f, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, 0.0f, pMatrix2D.m12, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.checkMatrix(2);
        this.matrix.apply(n, n2, n3, 0.0f, n4, n5, n6, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public void apply(final PMatrix3D pMatrix3D) {
        this.applyMatrix(pMatrix3D.m00, pMatrix3D.m01, pMatrix3D.m02, pMatrix3D.m03, pMatrix3D.m10, pMatrix3D.m11, pMatrix3D.m12, pMatrix3D.m13, pMatrix3D.m20, pMatrix3D.m21, pMatrix3D.m22, pMatrix3D.m23, pMatrix3D.m30, pMatrix3D.m31, pMatrix3D.m32, pMatrix3D.m33);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        this.checkMatrix(3);
        this.matrix.apply(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
    }
    
    protected void checkMatrix(final int n) {
        if (this.matrix == null) {
            if (n == 2) {
                this.matrix = (PMatrix)new PMatrix2D();
            }
            else {
                this.matrix = (PMatrix)new PMatrix3D();
            }
        }
        else if (n == 3 && this.matrix instanceof PMatrix2D) {
            this.matrix = (PMatrix)new PMatrix3D(this.matrix);
        }
    }
}

 