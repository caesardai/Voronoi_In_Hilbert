// 
// Decompiled by Procyon v0.5.36
// 

package processing.core;

import java.awt.Insets;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.SystemColor;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.awt.Container;
import java.awt.Font;
import java.io.Reader;
import processing.xml.XMLElement;
import java.util.zip.GZIPInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Cursor;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentListener;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.io.File;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.applet.Applet;

public class PApplet extends Applet implements PConstants, Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener
{
    public static final String javaVersionName;
    public static final float javaVersion;
    public static int platform;
    public static boolean useQuartz;
    public static final int MENU_SHORTCUT;
    public PGraphics g;
    public Frame frame;
    public int screenWidth;
    public int screenHeight;
    @Deprecated
    public Dimension screen;
    public PGraphics recorder;
    public String[] args;
    public String sketchPath;
    static final boolean THREAD_DEBUG = false;
    public static final int DEFAULT_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 100;
    public static final int MIN_WINDOW_WIDTH = 128;
    public static final int MIN_WINDOW_HEIGHT = 128;
    public boolean defaultSize;
    volatile boolean resizeRequest;
    volatile int resizeWidth;
    volatile int resizeHeight;
    public int[] pixels;
    public int width;
    public int height;
    public int mouseX;
    public int mouseY;
    public int pmouseX;
    public int pmouseY;
    protected int dmouseX;
    protected int dmouseY;
    protected int emouseX;
    protected int emouseY;
    public boolean firstMouse;
    public int mouseButton;
    public boolean mousePressed;
    public MouseEvent mouseEvent;
    public char key;
    public int keyCode;
    public boolean keyPressed;
    public KeyEvent keyEvent;
    public boolean focused;
    public boolean online;
    long millisOffset;
    public float frameRate;
    protected long frameRateLastNanos;
    protected float frameRateTarget;
    protected long frameRatePeriod;
    protected boolean looping;
    protected boolean redraw;
    public int frameCount;
    public volatile boolean finished;
    public volatile boolean paused;
    protected boolean exitCalled;
    Thread thread;
    protected PApplet.RegisteredMethods sizeMethods;
    protected PApplet.RegisteredMethods preMethods;
    protected PApplet.RegisteredMethods drawMethods;
    protected PApplet.RegisteredMethods postMethods;
    protected PApplet.RegisteredMethods mouseEventMethods;
    protected PApplet.RegisteredMethods keyEventMethods;
    protected PApplet.RegisteredMethods disposeMethods;
    public static final String ARGS_EDITOR_LOCATION = "--editor-location";
    public static final String ARGS_EXTERNAL = "--external";
    public static final String ARGS_LOCATION = "--location";
    public static final String ARGS_DISPLAY = "--display";
    public static final String ARGS_BGCOLOR = "--bgcolor";
    public static final String ARGS_PRESENT = "--present";
    public static final String ARGS_EXCLUSIVE = "--exclusive";
    public static final String ARGS_STOP_COLOR = "--stop-color";
    public static final String ARGS_HIDE_STOP = "--hide-stop";
    public static final String ARGS_SKETCH_FOLDER = "--sketch-path";
    public static final String EXTERNAL_STOP = "__STOP__";
    public static final String EXTERNAL_MOVE = "__MOVE__";
    boolean external;
    static final String ERROR_MIN_MAX = "Cannot use min() or max() on an empty array.";
    MouseEvent[] mouseEventQueue;
    int mouseEventCount;
    KeyEvent[] keyEventQueue;
    int keyEventCount;
    static String openLauncher;
    int cursorType;
    boolean cursorVisible;
    PImage invisibleCursor;
    Random internalRandom;
    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 16;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 256;
    static final int PERLIN_SIZE = 4095;
    int perlin_octaves;
    float perlin_amp_falloff;
    int perlin_TWOPI;
    int perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;
    Random perlinRandom;
    protected String[] loadImageFormats;
    public int requestImageMax;
    volatile int requestImageCount;
    protected String[] loadShapeFormats;
    public File selectedFile;
    protected Frame parentFrame;
    protected static HashMap<String, Pattern> matchPatterns;
    private static NumberFormat int_nf;
    private static int int_nf_digits;
    private static boolean int_nf_commas;
    private static NumberFormat float_nf;
    private static int float_nf_left;
    private static int float_nf_right;
    private static boolean float_nf_commas;
    public static final byte[] ICON_IMAGE;
    
    public PApplet() {
        this.screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.focused = false;
        this.online = false;
        this.millisOffset = System.currentTimeMillis();
        this.frameRate = 10.0f;
        this.frameRateLastNanos = 0L;
        this.frameRateTarget = 60.0f;
        this.frameRatePeriod = 16666666L;
        this.external = false;
        this.mouseEventQueue = new MouseEvent[10];
        this.keyEventQueue = new KeyEvent[10];
        this.cursorType = 0;
        this.cursorVisible = true;
        this.perlin_octaves = 4;
        this.perlin_amp_falloff = 0.5f;
        this.requestImageMax = 4;
    }
    
    @Override
    public void init() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.width;
        this.screenHeight = screenSize.height;
        this.setFocusTraversalKeysEnabled(false);
        this.finished = false;
        this.looping = true;
        this.redraw = true;
        this.firstMouse = true;
        this.sizeMethods = new PApplet.RegisteredMethods(this);
        this.preMethods = new PApplet.RegisteredMethods(this);
        this.drawMethods = new PApplet.RegisteredMethods(this);
        this.postMethods = new PApplet.RegisteredMethods(this);
        this.mouseEventMethods = new PApplet.RegisteredMethods(this);
        this.keyEventMethods = new PApplet.RegisteredMethods(this);
        this.disposeMethods = new PApplet.RegisteredMethods(this);
        try {
            this.getAppletContext();
            this.online = true;
        }
        catch (NullPointerException ex) {
            this.online = false;
        }
        try {
            if (this.sketchPath == null) {
                this.sketchPath = System.getProperty("user.dir");
            }
        }
        catch (Exception ex2) {}
        final Dimension size = this.getSize();
        if (size.width != 0 && size.height != 0) {
            this.g = this.makeGraphics(size.width, size.height, this.sketchRenderer(), null, true);
        }
        else {
            this.defaultSize = true;
            final int sketchWidth = this.sketchWidth();
            final int sketchHeight = this.sketchHeight();
            this.g = this.makeGraphics(sketchWidth, sketchHeight, this.sketchRenderer(), null, true);
            this.setSize(sketchWidth, sketchHeight);
            this.setPreferredSize(new Dimension(sketchWidth, sketchHeight));
        }
        this.width = this.g.width;
        this.height = this.g.height;
        this.addListeners();
        this.start();
    }
    
    public int sketchWidth() {
        return 100;
    }
    
    public int sketchHeight() {
        return 100;
    }
    
    public String sketchRenderer() {
        return "processing.core.PGraphicsJava2D";
    }
    
    @Override
    public void start() {
        this.finished = false;
        this.paused = false;
        if (this.thread == null) {
            (this.thread = new Thread(this, "Animation Thread")).start();
        }
    }
    
    @Override
    public void stop() {
        this.paused = true;
    }
    
    @Override
    public void destroy() {
        this.exit();
    }
    
    public void registerSize(final Object o) {
        this.registerWithArgs(this.sizeMethods, "size", o, new Class[] { Integer.TYPE, Integer.TYPE });
    }
    
    public void registerPre(final Object o) {
        this.registerNoArgs(this.preMethods, "pre", o);
    }
    
    public void registerDraw(final Object o) {
        this.registerNoArgs(this.drawMethods, "draw", o);
    }
    
    public void registerPost(final Object o) {
        this.registerNoArgs(this.postMethods, "post", o);
    }
    
    public void registerMouseEvent(final Object o) {
        this.registerWithArgs(this.mouseEventMethods, "mouseEvent", o, new Class[] { MouseEvent.class });
    }
    
    public void registerKeyEvent(final Object o) {
        this.registerWithArgs(this.keyEventMethods, "keyEvent", o, new Class[] { KeyEvent.class });
    }
    
    public void registerDispose(final Object o) {
        this.registerNoArgs(this.disposeMethods, "dispose", o);
    }
    
    protected void registerNoArgs(final PApplet.RegisteredMethods registeredMethods, final String str, final Object obj) {
        final Class<?> class1 = obj.getClass();
        try {
            registeredMethods.add(obj, class1.getMethod(str, (Class<?>[])new Class[0]));
        }
        catch (NoSuchMethodException ex2) {
            this.die("There is no public " + str + "() method in the class " + obj.getClass().getName());
        }
        catch (Exception ex) {
            this.die("Could not register " + str + " + () for " + obj, ex);
        }
    }
    
    protected void registerWithArgs(final PApplet.RegisteredMethods registeredMethods, final String str, final Object obj, final Class<?>[] parameterTypes) {
        final Class<?> class1 = obj.getClass();
        try {
            registeredMethods.add(obj, class1.getMethod(str, parameterTypes));
        }
        catch (NoSuchMethodException ex2) {
            this.die("There is no public " + str + "() method in the class " + obj.getClass().getName());
        }
        catch (Exception ex) {
            this.die("Could not register " + str + " + () for " + obj, ex);
        }
    }
    
    public void unregisterSize(final Object o) {
        this.unregisterWithArgs(this.sizeMethods, "size", o, new Class[] { Integer.TYPE, Integer.TYPE });
    }
    
    public void unregisterPre(final Object o) {
        this.unregisterNoArgs(this.preMethods, "pre", o);
    }
    
    public void unregisterDraw(final Object o) {
        this.unregisterNoArgs(this.drawMethods, "draw", o);
    }
    
    public void unregisterPost(final Object o) {
        this.unregisterNoArgs(this.postMethods, "post", o);
    }
    
    public void unregisterMouseEvent(final Object o) {
        this.unregisterWithArgs(this.mouseEventMethods, "mouseEvent", o, new Class[] { MouseEvent.class });
    }
    
    public void unregisterKeyEvent(final Object o) {
        this.unregisterWithArgs(this.keyEventMethods, "keyEvent", o, new Class[] { KeyEvent.class });
    }
    
    public void unregisterDispose(final Object o) {
        this.unregisterNoArgs(this.disposeMethods, "dispose", o);
    }
    
    protected void unregisterNoArgs(final PApplet.RegisteredMethods registeredMethods, final String s, final Object obj) {
        final Class<?> class1 = obj.getClass();
        try {
            registeredMethods.remove(obj, class1.getMethod(s, (Class<?>[])new Class[0]));
        }
        catch (Exception ex) {
            this.die("Could not unregister " + s + "() for " + obj, ex);
        }
    }
    
    protected void unregisterWithArgs(final PApplet.RegisteredMethods registeredMethods, final String s, final Object obj, final Class<?>[] parameterTypes) {
        final Class<?> class1 = obj.getClass();
        try {
            registeredMethods.remove(obj, class1.getMethod(s, parameterTypes));
        }
        catch (Exception ex) {
            this.die("Could not unregister " + s + "() for " + obj, ex);
        }
    }
    
    public void setup() {
    }
    
    public void draw() {
        this.finished = true;
    }
    
    protected void resizeRenderer(final int width, final int height) {
        if (this.width != width || this.height != height) {
            this.g.setSize(width, height);
            this.width = width;
            this.height = height;
        }
    }
    
    public void size(final int n, final int n2) {
        this.size(n, n2, "processing.core.PGraphicsJava2D", null);
    }
    
    public void size(final int n, final int n2, final String s) {
        this.size(n, n2, s, null);
    }
    
    public void size(final int width, final int height, final String anObject, String savePath) {
        SwingUtilities.invokeLater((Runnable)new PApplet.PApplet$1(this, width, height));
        if (savePath != null) {
            savePath = this.savePath(savePath);
        }
        if (this.g.getClass().getName().equals(anObject)) {
            this.resizeRenderer(width, height);
            return;
        }
        this.g = this.makeGraphics(width, height, anObject, savePath, true);
        this.width = width;
        this.height = height;
        this.defaultSize = false;
        throw new PApplet.RendererChangeException();
    }
    
    public PGraphics createGraphics(final int n, final int n2, final String s) {
        return this.makeGraphics(n, n2, s, null, false);
    }
    
    public PGraphics createGraphics(final int n, final int n2, final String s, String savePath) {
        if (savePath != null) {
            savePath = this.savePath(savePath);
        }
        final PGraphics graphics = this.makeGraphics(n, n2, s, savePath, false);
        graphics.parent = this;
        return graphics;
    }
    
    protected PGraphics makeGraphics(final int n, final int n2, final String str, final String path, final boolean primary) {
        if (str.equals("processing.opengl.PGraphicsOpenGL") && PApplet.platform == 1) {
            final String property = System.getProperty("java.version");
            if (property != null && property.equals("1.5.0_10")) {
                System.err.println("OpenGL support is broken with Java 1.5.0_10");
                System.err.println("See http://dev.processing.org/bugs/show_bug.cgi?id=513 for more info.");
                throw new RuntimeException("Please update your Java installation (see bug #513)");
            }
        }
        final String s = "Before using OpenGL, first select Import Library > opengl from the Sketch menu.";
        try {
            final PGraphics pGraphics = (PGraphics)Thread.currentThread().getContextClassLoader().loadClass(str).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            pGraphics.setParent(this);
            pGraphics.setPrimary(primary);
            if (path != null) {
                pGraphics.setPath(path);
            }
            pGraphics.setSize(n, n2);
            return pGraphics;
        }
        catch (InvocationTargetException ex) {
            final String message = ex.getTargetException().getMessage();
            if (message != null && message.indexOf("no jogl in java.library.path") != -1) {
                throw new RuntimeException(s + " (The native library is missing.)");
            }
            ex.getTargetException().printStackTrace();
            final Throwable targetException = ex.getTargetException();
            if (PApplet.platform == 2) {
                targetException.printStackTrace(System.out);
            }
            throw new RuntimeException(targetException.getMessage());
        }
        catch (ClassNotFoundException ex2) {
            if (ex2.getMessage().indexOf("processing.opengl.PGraphicsGL") != -1) {
                throw new RuntimeException(s + " (The library .jar file is missing.)");
            }
            throw new RuntimeException("You need to use \"Import Library\" to add " + str + " to your sketch.");
        }
        catch (Exception ex3) {
            if (ex3 instanceof IllegalArgumentException || ex3 instanceof NoSuchMethodException || ex3 instanceof IllegalAccessException) {
                ex3.printStackTrace();
                throw new RuntimeException(str + " needs to be updated " + "for the current release of Processing.");
            }
            if (PApplet.platform == 2) {
                ex3.printStackTrace(System.out);
            }
            throw new RuntimeException(ex3.getMessage());
        }
    }
    
    public PImage createImage(final int n, final int n2, final int n3) {
        return this.createImage(n, n2, n3, null);
    }
    
    public PImage createImage(final int n, final int n2, final int n3, final Object o) {
        final PImage pImage = new PImage(n, n2, n3);
        if (o != null) {
            pImage.setParams(this.g, o);
        }
        pImage.parent = this;
        return pImage;
    }
    
    public void update(final Graphics graphics) {
        this.paint(graphics);
    }
    
    public void paint(final Graphics graphics) {
        if (this.frameCount == 0) {
            return;
        }
        if (this.g != null && this.g.image != null) {
            synchronized (this.g.image) {
                graphics.drawImage(this.g.image, 0, 0, null);
            }
        }
    }
    
    public void run() {
        long n = System.nanoTime();
        long n2 = 0L;
        int n3 = 0;
        while (Thread.currentThread() == this.thread && !this.finished) {
            while (this.paused) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ex) {}
            }
            if (this.resizeRequest) {
                this.resizeRenderer(this.resizeWidth, this.resizeHeight);
                this.resizeRequest = false;
            }
            this.handleDraw();
            if (this.frameCount == 1) {
                this.requestFocusInWindow();
            }
            final long nanoTime = System.nanoTime();
            final long n4 = this.frameRatePeriod - (nanoTime - n) - n2;
            if (n4 > 0L) {
                try {
                    Thread.sleep(n4 / 1000000L, (int)(n4 % 1000000L));
                    n3 = 0;
                }
                catch (InterruptedException ex2) {}
                n2 = System.nanoTime() - nanoTime - n4;
            }
            else {
                n2 = 0L;
                if (n3 > 15) {
                    Thread.yield();
                    n3 = 0;
                }
            }
            n = System.nanoTime();
        }
        this.dispose();
        if (this.exitCalled) {
            this.exit2();
        }
    }
    
    public void handleDraw() {
        if (this.g != null && (this.looping || this.redraw)) {
            if (!this.g.canDraw()) {
                return;
            }
            this.g.beginDraw();
            if (this.recorder != null) {
                this.recorder.beginDraw();
            }
            final long nanoTime = System.nanoTime();
            if (this.frameCount == 0) {
                try {
                    this.setup();
                }
                catch (PApplet.RendererChangeException ex) {
                    return;
                }
                this.defaultSize = false;
            }
            else {
                this.frameRate = this.frameRate * 0.9f + (float)(1000000.0 / ((nanoTime - this.frameRateLastNanos) / 1000000.0)) / 1000.0f * 0.1f;
                this.preMethods.handle();
                this.pmouseX = this.dmouseX;
                this.pmouseY = this.dmouseY;
                this.draw();
                this.dmouseX = this.mouseX;
                this.dmouseY = this.mouseY;
                this.dequeueMouseEvents();
                this.dequeueKeyEvents();
                this.drawMethods.handle();
                this.redraw = false;
            }
            this.g.endDraw();
            if (this.recorder != null) {
                this.recorder.endDraw();
            }
            this.frameRateLastNanos = nanoTime;
            ++this.frameCount;
            this.repaint();
            this.getToolkit().sync();
            this.postMethods.handle();
        }
    }
    
    public synchronized void redraw() {
        if (!this.looping) {
            this.redraw = true;
        }
    }
    
    public synchronized void loop() {
        if (!this.looping) {
            this.looping = true;
        }
    }
    
    public synchronized void noLoop() {
        if (this.looping) {
            this.looping = false;
        }
    }
    
    public void addListeners() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.addFocusListener(this);
        this.addComponentListener((ComponentListener)new PApplet.PApplet$2(this));
    }
    
    protected void enqueueMouseEvent(final MouseEvent mouseEvent) {
        synchronized (this.mouseEventQueue) {
            if (this.mouseEventCount == this.mouseEventQueue.length) {
                final MouseEvent[] mouseEventQueue = new MouseEvent[this.mouseEventCount << 1];
                System.arraycopy(this.mouseEventQueue, 0, mouseEventQueue, 0, this.mouseEventCount);
                this.mouseEventQueue = mouseEventQueue;
            }
            this.mouseEventQueue[this.mouseEventCount++] = mouseEvent;
        }
    }
    
    protected void dequeueMouseEvents() {
        synchronized (this.mouseEventQueue) {
            for (int i = 0; i < this.mouseEventCount; ++i) {
                this.handleMouseEvent(this.mouseEvent = this.mouseEventQueue[i]);
            }
            this.mouseEventCount = 0;
        }
    }
    
    protected void handleMouseEvent(final MouseEvent mouseEvent) {
        final int id = mouseEvent.getID();
        if (id == 506 || id == 503) {
            this.pmouseX = this.emouseX;
            this.pmouseY = this.emouseY;
            this.mouseX = mouseEvent.getX();
            this.mouseY = mouseEvent.getY();
        }
        this.mouseEvent = mouseEvent;
        final int modifiers = mouseEvent.getModifiers();
        if ((modifiers & 0x10) != 0x0) {
            this.mouseButton = 37;
        }
        else if ((modifiers & 0x8) != 0x0) {
            this.mouseButton = 3;
        }
        else if ((modifiers & 0x4) != 0x0) {
            this.mouseButton = 39;
        }
        if (PApplet.platform == 2 && this.mouseEvent.isPopupTrigger()) {
            this.mouseButton = 39;
        }
        this.mouseEventMethods.handle(new Object[] { mouseEvent });
        if (this.firstMouse) {
            this.pmouseX = this.mouseX;
            this.pmouseY = this.mouseY;
            this.dmouseX = this.mouseX;
            this.dmouseY = this.mouseY;
            this.firstMouse = false;
        }
        switch (id) {
            case 501: {
                this.mousePressed = true;
                this.mousePressed();
                break;
            }
            case 502: {
                this.mousePressed = false;
                this.mouseReleased();
                break;
            }
            case 500: {
                this.mouseClicked();
                break;
            }
            case 506: {
                this.mouseDragged();
                break;
            }
            case 503: {
                this.mouseMoved();
                break;
            }
        }
        if (id == 506 || id == 503) {
            this.emouseX = this.mouseX;
            this.emouseY = this.mouseY;
        }
    }
    
    protected void checkMouseEvent(final MouseEvent mouseEvent) {
        if (this.looping) {
            this.enqueueMouseEvent(mouseEvent);
        }
        else {
            this.handleMouseEvent(mouseEvent);
        }
    }
    
    public void mousePressed(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseReleased(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseClicked(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseEntered(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseExited(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseDragged(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mouseMoved(final MouseEvent mouseEvent) {
        this.checkMouseEvent(mouseEvent);
    }
    
    public void mousePressed() {
    }
    
    public void mouseReleased() {
    }
    
    public void mouseClicked() {
    }
    
    public void mouseDragged() {
    }
    
    public void mouseMoved() {
    }
    
    protected void enqueueKeyEvent(final KeyEvent keyEvent) {
        synchronized (this.keyEventQueue) {
            if (this.keyEventCount == this.keyEventQueue.length) {
                final KeyEvent[] keyEventQueue = new KeyEvent[this.keyEventCount << 1];
                System.arraycopy(this.keyEventQueue, 0, keyEventQueue, 0, this.keyEventCount);
                this.keyEventQueue = keyEventQueue;
            }
            this.keyEventQueue[this.keyEventCount++] = keyEvent;
        }
    }
    
    protected void dequeueKeyEvents() {
        synchronized (this.keyEventQueue) {
            for (int i = 0; i < this.keyEventCount; ++i) {
                this.handleKeyEvent(this.keyEvent = this.keyEventQueue[i]);
            }
            this.keyEventCount = 0;
        }
    }
    
    protected void handleKeyEvent(final KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
        this.key = keyEvent.getKeyChar();
        this.keyCode = keyEvent.getKeyCode();
        this.keyEventMethods.handle(new Object[] { keyEvent });
        switch (keyEvent.getID()) {
            case 401: {
                this.keyPressed = true;
                this.keyPressed();
                break;
            }
            case 402: {
                this.keyPressed = false;
                this.keyReleased();
                break;
            }
            case 400: {
                this.keyTyped();
                break;
            }
        }
        if (keyEvent.getID() == 401) {
            if (this.key == '\u001b') {
                this.exit();
            }
            if (this.external && keyEvent.getModifiers() == PApplet.MENU_SHORTCUT && keyEvent.getKeyCode() == 87) {
                this.exit();
            }
        }
    }
    
    protected void checkKeyEvent(final KeyEvent keyEvent) {
        if (this.looping) {
            this.enqueueKeyEvent(keyEvent);
        }
        else {
            this.handleKeyEvent(keyEvent);
        }
    }
    
    public void keyPressed(final KeyEvent keyEvent) {
        this.checkKeyEvent(keyEvent);
    }
    
    public void keyReleased(final KeyEvent keyEvent) {
        this.checkKeyEvent(keyEvent);
    }
    
    public void keyTyped(final KeyEvent keyEvent) {
        this.checkKeyEvent(keyEvent);
    }
    
    public void keyPressed() {
    }
    
    public void keyReleased() {
    }
    
    public void keyTyped() {
    }
    
    public void focusGained() {
    }
    
    public void focusGained(final FocusEvent focusEvent) {
        this.focused = true;
        this.focusGained();
    }
    
    public void focusLost() {
    }
    
    public void focusLost(final FocusEvent focusEvent) {
        this.focused = false;
        this.focusLost();
    }
    
    public int millis() {
        return (int)(System.currentTimeMillis() - this.millisOffset);
    }
    
    public static int second() {
        return Calendar.getInstance().get(13);
    }
    
    public static int minute() {
        return Calendar.getInstance().get(12);
    }
    
    public static int hour() {
        return Calendar.getInstance().get(11);
    }
    
    public static int day() {
        return Calendar.getInstance().get(5);
    }
    
    public static int month() {
        return Calendar.getInstance().get(2) + 1;
    }
    
    public static int year() {
        return Calendar.getInstance().get(1);
    }
    
    public void delay(final int n) {
        if (this.frameCount != 0 && n > 0) {
            try {
                Thread.sleep(n);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public void frameRate(final float frameRateTarget) {
        this.frameRateTarget = frameRateTarget;
        this.frameRatePeriod = (long)(1.0E9 / this.frameRateTarget);
    }
    
    public String param(final String name) {
        if (this.online) {
            return this.getParameter(name);
        }
        System.err.println("param() only works inside a web browser");
        return null;
    }
    
    public void status(final String s) {
        if (this.online) {
            this.showStatus(s);
        }
        else {
            System.out.println(s);
        }
    }
    
    public void link(final String s) {
        this.link(s, null);
    }
    
    public void link(String replaceAll, final String s) {
        if (this.online) {
            try {
                if (s == null) {
                    this.getAppletContext().showDocument(new URL(replaceAll));
                }
                else {
                    this.getAppletContext().showDocument(new URL(replaceAll), s);
                }
                return;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Could not open " + replaceAll);
            }
        }
        try {
            if (PApplet.platform == 1) {
                replaceAll = replaceAll.replaceAll("&", "^&");
                Runtime.getRuntime().exec("cmd /c start " + replaceAll);
            }
            else if (PApplet.platform == 2) {
                try {
                    Class.forName("com.apple.eio.FileManager").getMethod("openURL", String.class).invoke(null, replaceAll);
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
            else {
                open(replaceAll);
            }
        }
        catch (IOException ex3) {
            ex3.printStackTrace();
            throw new RuntimeException("Could not open " + replaceAll);
        }
    }
    
    public static void open(final String s) {
        open(new String[] { s });
    }
    
    public static Process open(final String[] array) {
        String[] array2 = null;
        if (PApplet.platform == 1) {
            array2 = new String[] { "cmd", "/c" };
        }
        else if (PApplet.platform == 2) {
            array2 = new String[] { "open" };
        }
        else if (PApplet.platform == 3) {
            if (PApplet.openLauncher == null) {
                try {
                    Runtime.getRuntime().exec(new String[] { "gnome-open" }).waitFor();
                    PApplet.openLauncher = "gnome-open";
                }
                catch (Exception ex) {}
            }
            if (PApplet.openLauncher == null) {
                try {
                    Runtime.getRuntime().exec(new String[] { "kde-open" }).waitFor();
                    PApplet.openLauncher = "kde-open";
                }
                catch (Exception ex2) {}
            }
            if (PApplet.openLauncher == null) {
                System.err.println("Could not find gnome-open or kde-open, the open() command may not work.");
            }
            if (PApplet.openLauncher != null) {
                array2 = new String[] { PApplet.openLauncher };
            }
        }
        if (array2 == null) {
            return exec(array);
        }
        if (array2[0].equals(array[0])) {
            return exec(array);
        }
        return exec(concat(array2, array));
    }
    
    public static Process exec(final String[] cmdarray) {
        try {
            return Runtime.getRuntime().exec(cmdarray);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not open " + join(cmdarray, ' '));
        }
    }
    
    public void die(final String message) {
        this.dispose();
        throw new RuntimeException(message);
    }
    
    public void die(final String s, final Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        this.die(s);
    }
    
    public void exit() {
        if (this.thread == null) {
            this.exit2();
        }
        else if (this.looping) {
            this.finished = true;
            this.exitCalled = true;
        }
        else if (!this.looping) {
            this.dispose();
            this.exit2();
        }
    }
    
    void exit2() {
        try {
            System.exit(0);
        }
        catch (SecurityException ex) {}
    }
    
    public void dispose() {
        this.finished = true;
        if (this.thread == null) {
            return;
        }
        this.thread = null;
        if (this.g != null) {
            this.g.dispose();
        }
        this.disposeMethods.handle();
    }
    
    public void method(final String s) {
        try {
            this.getClass().getMethod(s, (Class<?>[])new Class[0]).invoke(this, new Object[0]);
        }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        catch (IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
        catch (InvocationTargetException ex3) {
            ex3.getTargetException().printStackTrace();
        }
        catch (NoSuchMethodException ex5) {
            System.err.println("There is no public " + s + "() method " + "in the class " + this.getClass().getName());
        }
        catch (Exception ex4) {
            ex4.printStackTrace();
        }
    }
    
    public void thread(final String s) {
        ((Thread)new PApplet.PApplet$3(this, s)).start();
    }
    
    public void save(final String s) {
        this.g.save(this.savePath(s));
    }
    
    public void saveFrame() {
        try {
            this.g.save(this.savePath("screen-" + nf(this.frameCount, 4) + ".tif"));
        }
        catch (SecurityException ex) {
            System.err.println("Can't use saveFrame() when running in a browser, unless using a signed applet.");
        }
    }
    
    public void saveFrame(final String s) {
        try {
            this.g.save(this.savePath(this.insertFrame(s)));
        }
        catch (SecurityException ex) {
            System.err.println("Can't use saveFrame() when running in a browser, unless using a signed applet.");
        }
    }
    
    protected String insertFrame(final String s) {
        final int index = s.indexOf(35);
        final int lastIndex = s.lastIndexOf(35);
        if (index != -1 && lastIndex - index > 0) {
            return s.substring(0, index) + nf(this.frameCount, lastIndex - index + 1) + s.substring(lastIndex + 1);
        }
        return s;
    }
    
    public void cursor(final int n) {
        this.setCursor(Cursor.getPredefinedCursor(n));
        this.cursorVisible = true;
        this.cursorType = n;
    }
    
    public void cursor(final PImage pImage) {
        this.cursor(pImage, pImage.width / 2, pImage.height / 2);
    }
    
    public void cursor(final PImage pImage, final int x, final int y) {
        this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(this.createImage(new MemoryImageSource(pImage.width, pImage.height, pImage.pixels, 0, pImage.width)), new Point(x, y), "Custom Cursor"));
        this.cursorVisible = true;
    }
    
    public void cursor() {
        if (!this.cursorVisible) {
            this.cursorVisible = true;
            this.setCursor(Cursor.getPredefinedCursor(this.cursorType));
        }
    }
    
    public void noCursor() {
        if (!this.cursorVisible) {
            return;
        }
        if (this.invisibleCursor == null) {
            this.invisibleCursor = new PImage(16, 16, 2);
        }
        this.cursor(this.invisibleCursor, 8, 8);
        this.cursorVisible = false;
    }
    
    public static void print(final byte i) {
        System.out.print(i);
        System.out.flush();
    }
    
    public static void print(final boolean b) {
        System.out.print(b);
        System.out.flush();
    }
    
    public static void print(final char c) {
        System.out.print(c);
        System.out.flush();
    }
    
    public static void print(final int i) {
        System.out.print(i);
        System.out.flush();
    }
    
    public static void print(final float f) {
        System.out.print(f);
        System.out.flush();
    }
    
    public static void print(final String s) {
        System.out.print(s);
        System.out.flush();
    }
    
    public static void print(final Object o) {
        if (o == null) {
            System.out.print("null");
        }
        else {
            System.out.println(o.toString());
        }
    }
    
    public static void println() {
        System.out.println();
    }
    
    public static void println(final byte b) {
        print(b);
        System.out.println();
    }
    
    public static void println(final boolean b) {
        print(b);
        System.out.println();
    }
    
    public static void println(final char c) {
        print(c);
        System.out.println();
    }
    
    public static void println(final int n) {
        print(n);
        System.out.println();
    }
    
    public static void println(final float n) {
        print(n);
        System.out.println();
    }
    
    public static void println(final String s) {
        print(s);
        System.out.println();
    }
    
    public static void println(final Object x) {
        if (x == null) {
            System.out.println("null");
        }
        else {
            final String name = x.getClass().getName();
            if (name.charAt(0) == '[') {
                switch (name.charAt(1)) {
                    case '[': {
                        System.out.println(x);
                        break;
                    }
                    case 'L': {
                        final Object[] array = (Object[])x;
                        for (int i = 0; i < array.length; ++i) {
                            if (array[i] instanceof String) {
                                System.out.println("[" + i + "] \"" + array[i] + "\"");
                            }
                            else {
                                System.out.println("[" + i + "] " + array[i]);
                            }
                        }
                        break;
                    }
                    case 'Z': {
                        final boolean[] array2 = (boolean[])x;
                        for (int j = 0; j < array2.length; ++j) {
                            System.out.println("[" + j + "] " + array2[j]);
                        }
                        break;
                    }
                    case 'B': {
                        final byte[] array3 = (byte[])x;
                        for (int k = 0; k < array3.length; ++k) {
                            System.out.println("[" + k + "] " + array3[k]);
                        }
                        break;
                    }
                    case 'C': {
                        final char[] array4 = (char[])x;
                        for (int l = 0; l < array4.length; ++l) {
                            System.out.println("[" + l + "] '" + array4[l] + "'");
                        }
                        break;
                    }
                    case 'I': {
                        final int[] array5 = (int[])x;
                        for (int m = 0; m < array5.length; ++m) {
                            System.out.println("[" + m + "] " + array5[m]);
                        }
                        break;
                    }
                    case 'F': {
                        final float[] array6 = (float[])x;
                        for (int i2 = 0; i2 < array6.length; ++i2) {
                            System.out.println("[" + i2 + "] " + array6[i2]);
                        }
                        break;
                    }
                    case 'D': {
                        final double[] array7 = (double[])x;
                        for (int i3 = 0; i3 < array7.length; ++i3) {
                            System.out.println("[" + i3 + "] " + array7[i3]);
                        }
                        break;
                    }
                    default: {
                        System.out.println(x);
                        break;
                    }
                }
            }
            else {
                System.out.println(x);
            }
        }
    }
    
    public static final float abs(final float n) {
        return (n < 0.0f) ? (-n) : n;
    }
    
    public static final int abs(final int n) {
        return (n < 0) ? (-n) : n;
    }
    
    public static final float sq(final float n) {
        return n * n;
    }
    
    public static final float sqrt(final float n) {
        return (float)Math.sqrt(n);
    }
    
    public static final float log(final float n) {
        return (float)Math.log(n);
    }
    
    public static final float exp(final float n) {
        return (float)Math.exp(n);
    }
    
    public static final float pow(final float n, final float n2) {
        return (float)Math.pow(n, n2);
    }
    
    public static final int max(final int n, final int n2) {
        return (n > n2) ? n : n2;
    }
    
    public static final float max(final float n, final float n2) {
        return (n > n2) ? n : n2;
    }
    
    public static final int max(final int n, final int n2, final int n3) {
        return (n > n2) ? ((n > n3) ? n : n3) : ((n2 > n3) ? n2 : n3);
    }
    
    public static final float max(final float n, final float n2, final float n3) {
        return (n > n2) ? ((n > n3) ? n : n3) : ((n2 > n3) ? n2 : n3);
    }
    
    public static final int max(final int[] array) {
        if (array.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        int n = array[0];
        for (int i = 1; i < array.length; ++i) {
            if (array[i] > n) {
                n = array[i];
            }
        }
        return n;
    }
    
    public static final float max(final float[] array) {
        if (array.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        float n = array[0];
        for (int i = 1; i < array.length; ++i) {
            if (array[i] > n) {
                n = array[i];
            }
        }
        return n;
    }
    
    public static final int min(final int n, final int n2) {
        return (n < n2) ? n : n2;
    }
    
    public static final float min(final float n, final float n2) {
        return (n < n2) ? n : n2;
    }
    
    public static final int min(final int n, final int n2, final int n3) {
        return (n < n2) ? ((n < n3) ? n : n3) : ((n2 < n3) ? n2 : n3);
    }
    
    public static final float min(final float n, final float n2, final float n3) {
        return (n < n2) ? ((n < n3) ? n : n3) : ((n2 < n3) ? n2 : n3);
    }
    
    public static final int min(final int[] array) {
        if (array.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        int n = array[0];
        for (int i = 1; i < array.length; ++i) {
            if (array[i] < n) {
                n = array[i];
            }
        }
        return n;
    }
    
    public static final float min(final float[] array) {
        if (array.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        float n = array[0];
        for (int i = 1; i < array.length; ++i) {
            if (array[i] < n) {
                n = array[i];
            }
        }
        return n;
    }
    
    public static final int constrain(final int n, final int n2, final int n3) {
        return (n < n2) ? n2 : ((n > n3) ? n3 : n);
    }
    
    public static final float constrain(final float n, final float n2, final float n3) {
        return (n < n2) ? n2 : ((n > n3) ? n3 : n);
    }
    
    public static final float sin(final float n) {
        return (float)Math.sin(n);
    }
    
    public static final float cos(final float n) {
        return (float)Math.cos(n);
    }
    
    public static final float tan(final float n) {
        return (float)Math.tan(n);
    }
    
    public static final float asin(final float n) {
        return (float)Math.asin(n);
    }
    
    public static final float acos(final float n) {
        return (float)Math.acos(n);
    }
    
    public static final float atan(final float n) {
        return (float)Math.atan(n);
    }
    
    public static final float atan2(final float n, final float n2) {
        return (float)Math.atan2(n, n2);
    }
    
    public static final float degrees(final float n) {
        return n * 57.295776f;
    }
    
    public static final float radians(final float n) {
        return n * 0.017453292f;
    }
    
    public static final int ceil(final float n) {
        return (int)Math.ceil(n);
    }
    
    public static final int floor(final float n) {
        return (int)Math.floor(n);
    }
    
    public static final int round(final float a) {
        return Math.round(a);
    }
    
    public static final float mag(final float n, final float n2) {
        return (float)Math.sqrt(n * n + n2 * n2);
    }
    
    public static final float mag(final float n, final float n2, final float n3) {
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public static final float dist(final float n, final float n2, final float n3, final float n4) {
        return sqrt(sq(n3 - n) + sq(n4 - n2));
    }
    
    public static final float dist(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return sqrt(sq(n4 - n) + sq(n5 - n2) + sq(n6 - n3));
    }
    
    public static final float lerp(final float n, final float n2, final float n3) {
        return n + (n2 - n) * n3;
    }
    
    public static final float norm(final float n, final float n2, final float n3) {
        return (n - n2) / (n3 - n2);
    }
    
    public static final float map(final float n, final float n2, final float n3, final float n4, final float n5) {
        return n4 + (n5 - n4) * ((n - n2) / (n3 - n2));
    }
    
    public final float random(final float n) {
        if (n == 0.0f) {
            return 0.0f;
        }
        if (this.internalRandom == null) {
            this.internalRandom = new Random();
        }
        float n2;
        do {
            n2 = this.internalRandom.nextFloat() * n;
        } while (n2 == n);
        return n2;
    }
    
    public final float random(final float n, final float n2) {
        if (n >= n2) {
            return n;
        }
        return this.random(n2 - n) + n;
    }
    
    public final void randomSeed(final long seed) {
        if (this.internalRandom == null) {
            this.internalRandom = new Random();
        }
        this.internalRandom.setSeed(seed);
    }
    
    public float noise(final float n) {
        return this.noise(n, 0.0f, 0.0f);
    }
    
    public float noise(final float n, final float n2) {
        return this.noise(n, n2, 0.0f);
    }
    
    public float noise(float n, float n2, float n3) {
        if (this.perlin == null) {
            if (this.perlinRandom == null) {
                this.perlinRandom = new Random();
            }
            this.perlin = new float[4096];
            for (int i = 0; i < 4096; ++i) {
                this.perlin[i] = this.perlinRandom.nextFloat();
            }
            this.perlin_cosTable = PGraphics.cosLUT;
            final int n4 = 720;
            this.perlin_PI = n4;
            this.perlin_TWOPI = n4;
            this.perlin_PI >>= 1;
        }
        if (n < 0.0f) {
            n = -n;
        }
        if (n2 < 0.0f) {
            n2 = -n2;
        }
        if (n3 < 0.0f) {
            n3 = -n3;
        }
        int n5 = (int)n;
        int n6 = (int)n2;
        int n7 = (int)n3;
        float n8 = n - n5;
        float n9 = n2 - n6;
        float n10 = n3 - n7;
        float n11 = 0.0f;
        float n12 = 0.5f;
        for (int j = 0; j < this.perlin_octaves; ++j) {
            int n13 = n5 + (n6 << 4) + (n7 << 8);
            final float noise_fsc = this.noise_fsc(n8);
            final float noise_fsc2 = this.noise_fsc(n9);
            final float n14 = this.perlin[n13 & 0xFFF];
            final float n15 = n14 + noise_fsc * (this.perlin[n13 + 1 & 0xFFF] - n14);
            final float n16 = this.perlin[n13 + 16 & 0xFFF];
            final float n17 = n15 + noise_fsc2 * (n16 + noise_fsc * (this.perlin[n13 + 16 + 1 & 0xFFF] - n16) - n15);
            n13 += 256;
            final float n18 = this.perlin[n13 & 0xFFF];
            final float n19 = n18 + noise_fsc * (this.perlin[n13 + 1 & 0xFFF] - n18);
            final float n20 = this.perlin[n13 + 16 & 0xFFF];
            n11 += (n17 + this.noise_fsc(n10) * (n19 + noise_fsc2 * (n20 + noise_fsc * (this.perlin[n13 + 16 + 1 & 0xFFF] - n20) - n19) - n17)) * n12;
            n12 *= this.perlin_amp_falloff;
            n5 <<= 1;
            n8 *= 2.0f;
            n6 <<= 1;
            n9 *= 2.0f;
            n7 <<= 1;
            n10 *= 2.0f;
            if (n8 >= 1.0f) {
                ++n5;
                --n8;
            }
            if (n9 >= 1.0f) {
                ++n6;
                --n9;
            }
            if (n10 >= 1.0f) {
                ++n7;
                --n10;
            }
        }
        return n11;
    }
    
    private float noise_fsc(final float n) {
        return 0.5f * (1.0f - this.perlin_cosTable[(int)(n * this.perlin_PI) % this.perlin_TWOPI]);
    }
    
    public void noiseDetail(final int perlin_octaves) {
        if (perlin_octaves > 0) {
            this.perlin_octaves = perlin_octaves;
        }
    }
    
    public void noiseDetail(final int perlin_octaves, final float perlin_amp_falloff) {
        if (perlin_octaves > 0) {
            this.perlin_octaves = perlin_octaves;
        }
        if (perlin_amp_falloff > 0.0f) {
            this.perlin_amp_falloff = perlin_amp_falloff;
        }
    }
    
    public void noiseSeed(final long seed) {
        if (this.perlinRandom == null) {
            this.perlinRandom = new Random();
        }
        this.perlinRandom.setSeed(seed);
        this.perlin = null;
    }
    
    public PImage loadImage(final String s) {
        return this.loadImage(s, null, null);
    }
    
    public PImage loadImage(final String s, final String s2) {
        return this.loadImage(s, s2, null);
    }
    
    public PImage loadImage(final String s, final Object o) {
        return this.loadImage(s, null, o);
    }
    
    public PImage loadImage(final String s, String s2, final Object o) {
        if (s2 == null) {
            final String lowerCase = s.toLowerCase();
            final int lastIndex = s.lastIndexOf(46);
            if (lastIndex == -1) {
                s2 = "unknown";
            }
            s2 = lowerCase.substring(lastIndex + 1);
            final int index = s2.indexOf(63);
            if (index != -1) {
                s2 = s2.substring(0, index);
            }
        }
        s2 = s2.toLowerCase();
        if (s2.equals("tga")) {
            try {
                final PImage loadImageTGA = this.loadImageTGA(s);
                if (o != null) {
                    loadImageTGA.setParams(this.g, o);
                }
                return loadImageTGA;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        if (s2.equals("tif") || s2.equals("tiff")) {
            final byte[] loadBytes = this.loadBytes(s);
            final PImage pImage = (loadBytes == null) ? null : PImage.loadTIFF(loadBytes);
            if (o != null) {
                pImage.setParams(this.g, o);
            }
            return pImage;
        }
        try {
            if (s2.equals("jpg") || s2.equals("jpeg") || s2.equals("gif") || s2.equals("png") || s2.equals("unknown")) {
                final byte[] loadBytes2 = this.loadBytes(s);
                if (loadBytes2 == null) {
                    return null;
                }
                final PImage loadImageMT = this.loadImageMT(Toolkit.getDefaultToolkit().createImage(loadBytes2));
                if (loadImageMT.width == -1) {
                    System.err.println("The file " + s + " contains bad image data, or may not be an image.");
                }
                if (s2.equals("gif") || s2.equals("png")) {
                    loadImageMT.checkAlpha();
                }
                if (o != null) {
                    loadImageMT.setParams(this.g, o);
                }
                return loadImageMT;
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        if (this.loadImageFormats == null) {
            this.loadImageFormats = ImageIO.getReaderFormatNames();
        }
        if (this.loadImageFormats != null) {
            for (int i = 0; i < this.loadImageFormats.length; ++i) {
                if (s2.equals(this.loadImageFormats[i])) {
                    final PImage loadImageIO = this.loadImageIO(s);
                    if (o != null) {
                        loadImageIO.setParams(this.g, o);
                    }
                    return loadImageIO;
                }
            }
        }
        System.err.println("Could not find a method to load " + s);
        return null;
    }
    
    public PImage requestImage(final String s) {
        return this.requestImage(s, null, null);
    }
    
    public PImage requestImage(final String s, final String s2) {
        return this.requestImage(s, s2, null);
    }
    
    public PImage requestImage(final String s, final String s2, final Object o) {
        final PImage image = this.createImage(0, 0, 2, o);
        new PApplet.AsyncImageLoader(this, s, s2, image).start();
        return image;
    }
    
    protected PImage loadImageMT(final Image image) {
        final MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForAll();
        }
        catch (InterruptedException ex) {}
        final PImage pImage = new PImage(image);
        pImage.parent = this;
        return pImage;
    }
    
    protected PImage loadImageIO(final String str) {
        final InputStream input = this.createInput(str);
        if (input == null) {
            System.err.println("The image " + str + " could not be found.");
            return null;
        }
        try {
            final BufferedImage read = ImageIO.read(input);
            final PImage pImage = new PImage(read.getWidth(), read.getHeight());
            pImage.parent = this;
            read.getRGB(0, 0, pImage.width, pImage.height, pImage.pixels, 0, pImage.width);
            pImage.checkAlpha();
            return pImage;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    protected PImage loadImageTGA(final String str) throws IOException {
        final InputStream input = this.createInput(str);
        if (input == null) {
            return null;
        }
        final byte[] b = new byte[18];
        int i = 0;
        do {
            final int read = input.read(b, i, b.length - i);
            if (read == -1) {
                return null;
            }
            i += read;
        } while (i < 18);
        int n = 0;
        if ((b[2] == 3 || b[2] == 11) && b[16] == 8 && (b[17] == 8 || b[17] == 40)) {
            n = 4;
        }
        else if ((b[2] == 2 || b[2] == 10) && b[16] == 24 && (b[17] == 32 || b[17] == 0)) {
            n = 1;
        }
        else if ((b[2] == 2 || b[2] == 10) && b[16] == 32 && (b[17] == 8 || b[17] == 40)) {
            n = 2;
        }
        if (n == 0) {
            System.err.println("Unknown .tga file format for " + str);
            return null;
        }
        final int n2 = ((b[13] & 0xFF) << 8) + (b[12] & 0xFF);
        final int n3 = ((b[15] & 0xFF) << 8) + (b[14] & 0xFF);
        final PImage image = this.createImage(n2, n3, n);
        final boolean b2 = (b[17] & 0x20) != 0x0;
        if (b[2] == 2 || b[2] == 3) {
            if (b2) {
                int n4 = (n3 - 1) * n2;
                switch (n) {
                    case 4: {
                        for (int j = n3 - 1; j >= 0; --j) {
                            for (int k = 0; k < n2; ++k) {
                                image.pixels[n4 + k] = input.read();
                            }
                            n4 -= n2;
                        }
                        break;
                    }
                    case 1: {
                        for (int l = n3 - 1; l >= 0; --l) {
                            for (int n5 = 0; n5 < n2; ++n5) {
                                image.pixels[n4 + n5] = (input.read() | input.read() << 8 | input.read() << 16 | 0xFF000000);
                            }
                            n4 -= n2;
                        }
                        break;
                    }
                    case 2: {
                        for (int n6 = n3 - 1; n6 >= 0; --n6) {
                            for (int n7 = 0; n7 < n2; ++n7) {
                                image.pixels[n4 + n7] = (input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24);
                            }
                            n4 -= n2;
                        }
                        break;
                    }
                }
            }
            else {
                final int n8 = n2 * n3;
                switch (n) {
                    case 4: {
                        for (int n9 = 0; n9 < n8; ++n9) {
                            image.pixels[n9] = input.read();
                        }
                        break;
                    }
                    case 1: {
                        for (int n10 = 0; n10 < n8; ++n10) {
                            image.pixels[n10] = (input.read() | input.read() << 8 | input.read() << 16 | 0xFF000000);
                        }
                        break;
                    }
                    case 2: {
                        for (int n11 = 0; n11 < n8; ++n11) {
                            image.pixels[n11] = (input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24);
                        }
                        break;
                    }
                }
            }
        }
        else {
            int n12 = 0;
            final int[] pixels = image.pixels;
            while (n12 < pixels.length) {
                int read2 = input.read();
                if ((read2 & 0x80) != 0x0) {
                    read2 -= 127;
                    int read3 = 0;
                    switch (n) {
                        case 4: {
                            read3 = input.read();
                            break;
                        }
                        case 1: {
                            read3 = (0xFF000000 | input.read() | input.read() << 8 | input.read() << 16);
                            break;
                        }
                        case 2: {
                            read3 = (input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24);
                            break;
                        }
                    }
                    for (int n13 = 0; n13 < read2; ++n13) {
                        pixels[n12++] = read3;
                        if (n12 == pixels.length) {
                            break;
                        }
                    }
                }
                else {
                    ++read2;
                    switch (n) {
                        case 4: {
                            for (int n14 = 0; n14 < read2; ++n14) {
                                pixels[n12++] = input.read();
                            }
                            continue;
                        }
                        case 1: {
                            for (int n15 = 0; n15 < read2; ++n15) {
                                pixels[n12++] = (0xFF000000 | input.read() | input.read() << 8 | input.read() << 16);
                            }
                            continue;
                        }
                        case 2: {
                            for (int n16 = 0; n16 < read2; ++n16) {
                                pixels[n12++] = (input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24);
                            }
                            continue;
                        }
                    }
                }
            }
            if (!b2) {
                final int[] array = new int[n2];
                for (int n17 = 0; n17 < n3 / 2; ++n17) {
                    final int n18 = n3 - 1 - n17;
                    System.arraycopy(pixels, n17 * n2, array, 0, n2);
                    System.arraycopy(pixels, n18 * n2, pixels, n17 * n2, n2);
                    System.arraycopy(array, 0, pixels, n18 * n2, n2);
                }
            }
        }
        return image;
    }
    
    public PShape loadShape(final String s) {
        return this.loadShape(s, null);
    }
    
    public PShape loadShape(final String s, final Object o) {
        final String lowerCase = s.toLowerCase();
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex == -1) {}
        String s2 = lowerCase.substring(lastIndex + 1);
        final int index = s2.indexOf(63);
        if (index != -1) {
            s2 = s2.substring(0, index);
        }
        if (s2.equals("svg")) {
            return (PShape)new PShapeSVG(this, s);
        }
        if (s2.equals("svgz")) {
            try {
                return (PShape)new PShapeSVG(new XMLElement((Reader)createReader(new GZIPInputStream(this.createInput(s)))));
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        this.loadShapeFormats = this.g.getSupportedShapeFormats();
        if (this.loadShapeFormats != null) {
            for (int i = 0; i < this.loadShapeFormats.length; ++i) {
                if (s2.equals(this.loadShapeFormats[i])) {
                    return this.g.loadShape(s, o);
                }
            }
        }
        return null;
    }
    
    public PShape createShape(final int n, final Object o) {
        return this.g.createShape(n, o);
    }
    
    public PFont loadFont(final String str) {
        try {
            return new PFont(this.createInput(str));
        }
        catch (Exception ex) {
            this.die("Could not load font " + str + ". " + "Make sure that the font has been copied " + "to the data folder of your sketch.", ex);
            return null;
        }
    }
    
    protected PFont createDefaultFont(final float n) {
        return this.createFont("Lucida Sans", n, true, null);
    }
    
    public PFont createFont(final String s, final float n) {
        return this.createFont(s, n, true, null);
    }
    
    public PFont createFont(final String s, final float n, final boolean b) {
        return this.createFont(s, n, b, null);
    }
    
    public PFont createFont(final String s, final float size, final boolean b, final char[] array) {
        final String lowerCase = s.toLowerCase();
        try {
            InputStream input = null;
            Font font;
            if (lowerCase.endsWith(".otf") || lowerCase.endsWith(".ttf")) {
                input = this.createInput(s);
                if (input == null) {
                    System.err.println("The font \"" + s + "\" " + "is missing or inaccessible, make sure " + "the URL is valid or that the file has been " + "added to your sketch and is readable.");
                    return null;
                }
                font = Font.createFont(0, this.createInput(s));
            }
            else {
                font = PFont.findFont(s);
            }
            return new PFont(font.deriveFont(size), b, array, input != null);
        }
        catch (Exception ex) {
            System.err.println("Problem createFont(" + s + ")");
            ex.printStackTrace();
            return null;
        }
    }
    
    protected void checkParentFrame() {
        if (this.parentFrame == null) {
            for (Container container = this.getParent(); container != null; container = container.getParent()) {
                if (container instanceof Frame) {
                    this.parentFrame = (Frame)container;
                    break;
                }
            }
            if (this.parentFrame == null) {
                this.parentFrame = new Frame();
            }
        }
    }
    
    public String selectInput() {
        return this.selectInput("Select a file...");
    }
    
    public String selectInput(final String s) {
        return this.selectFileImpl(s, 0);
    }
    
    public String selectOutput() {
        return this.selectOutput("Save as...");
    }
    
    public String selectOutput(final String s) {
        return this.selectFileImpl(s, 1);
    }
    
    protected String selectFileImpl(final String s, final int n) {
        this.checkParentFrame();
        try {
            SwingUtilities.invokeAndWait((Runnable)new PApplet.PApplet$4(this, s, n));
            return (this.selectedFile == null) ? null : this.selectedFile.getAbsolutePath();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String selectFolder() {
        return this.selectFolder("Select a folder...");
    }
    
    public String selectFolder(final String s) {
        this.checkParentFrame();
        try {
            SwingUtilities.invokeAndWait((Runnable)new PApplet.PApplet$5(this, s));
            return (this.selectedFile == null) ? null : this.selectedFile.getAbsolutePath();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public BufferedReader createReader(final String s) {
        try {
            final InputStream input = this.createInput(s);
            if (input == null) {
                System.err.println(s + " does not exist or could not be read");
                return null;
            }
            return createReader(input);
        }
        catch (Exception ex) {
            if (s == null) {
                System.err.println("Filename passed to reader() was null");
            }
            else {
                System.err.println("Couldn't create a reader for " + s);
            }
            return null;
        }
    }
    
    public static BufferedReader createReader(final File file) {
        try {
            InputStream in = new FileInputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                in = new GZIPInputStream(in);
            }
            return createReader(in);
        }
        catch (Exception ex) {
            if (file == null) {
                throw new RuntimeException("File passed to createReader() was null");
            }
            ex.printStackTrace();
            throw new RuntimeException("Couldn't create a reader for " + file.getAbsolutePath());
        }
    }
    
    public static BufferedReader createReader(final InputStream in) {
        Reader in2 = null;
        try {
            in2 = new InputStreamReader(in, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {}
        return new BufferedReader(in2);
    }
    
    public PrintWriter createWriter(final String s) {
        return createWriter(this.saveFile(s));
    }
    
    public static PrintWriter createWriter(final File file) {
        try {
            createPath(file);
            OutputStream out = new FileOutputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                out = new GZIPOutputStream(out);
            }
            return createWriter(out);
        }
        catch (Exception ex) {
            if (file == null) {
                throw new RuntimeException("File passed to createWriter() was null");
            }
            ex.printStackTrace();
            throw new RuntimeException("Couldn't create a writer for " + file.getAbsolutePath());
        }
    }
    
    public static PrintWriter createWriter(final OutputStream out) {
        try {
            return new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out, 8192), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    @Deprecated
    public InputStream openStream(final String s) {
        return this.createInput(s);
    }
    
    public InputStream createInput(final String s) {
        final InputStream inputRaw = this.createInputRaw(s);
        if (inputRaw != null && s.toLowerCase().endsWith(".gz")) {
            try {
                return new GZIPInputStream(inputRaw);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return inputRaw;
    }
    
    public InputStream createInputRaw(final String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 0) {
            return null;
        }
        if (name.indexOf(":") != -1) {
            try {
                return new URL(name).openStream();
            }
            catch (MalformedURLException ex3) {}
            catch (FileNotFoundException ex4) {}
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        try {
            File file = new File(this.dataPath(name));
            if (!file.exists()) {
                file = new File(this.sketchPath, name);
            }
            if (file.isDirectory()) {
                return null;
            }
            if (file.exists()) {
                try {
                    final String name2 = new File(file.getCanonicalPath()).getName();
                    if (!name2.equals(new File(name).getName())) {
                        throw new RuntimeException("This file is named " + name2 + " not " + name + ". Rename the file " + "or change your code.");
                    }
                }
                catch (IOException ex5) {}
            }
            final FileInputStream fileInputStream = new FileInputStream(file);
            if (fileInputStream != null) {
                return fileInputStream;
            }
        }
        catch (IOException ex6) {}
        catch (SecurityException ex7) {}
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final InputStream resourceAsStream = classLoader.getResourceAsStream("data/" + name);
        if (resourceAsStream != null && !resourceAsStream.getClass().getName().equals("sun.plugin.cache.EmptyInputStream")) {
            return resourceAsStream;
        }
        final InputStream resourceAsStream2 = classLoader.getResourceAsStream(name);
        if (resourceAsStream2 != null && !resourceAsStream2.getClass().getName().equals("sun.plugin.cache.EmptyInputStream")) {
            return resourceAsStream2;
        }
        try {
            final URL documentBase = this.getDocumentBase();
            if (documentBase != null) {
                return new URL(documentBase, name).openConnection().getInputStream();
            }
        }
        catch (Exception ex8) {}
        try {
            final URL documentBase2 = this.getDocumentBase();
            if (documentBase2 != null) {
                return new URL(documentBase2, "data/" + name).openConnection().getInputStream();
            }
        }
        catch (Exception ex9) {}
        try {
            try {
                try {
                    final FileInputStream fileInputStream2 = new FileInputStream(this.dataPath(name));
                    if (fileInputStream2 != null) {
                        return fileInputStream2;
                    }
                }
                catch (IOException ex10) {}
                try {
                    final FileInputStream fileInputStream3 = new FileInputStream(this.sketchPath(name));
                    if (fileInputStream3 != null) {
                        return fileInputStream3;
                    }
                }
                catch (Exception ex11) {}
                try {
                    final FileInputStream fileInputStream4 = new FileInputStream(name);
                    if (fileInputStream4 != null) {
                        return fileInputStream4;
                    }
                }
                catch (IOException ex12) {}
            }
            catch (SecurityException ex13) {}
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        return null;
    }
    
    public static InputStream createInput(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File passed to createInput() was null");
        }
        try {
            final FileInputStream in = new FileInputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                return new GZIPInputStream(in);
            }
            return in;
        }
        catch (IOException ex) {
            System.err.println("Could not createInput() for " + file);
            ex.printStackTrace();
            return null;
        }
    }
    
    public byte[] loadBytes(final String str) {
        final InputStream input = this.createInput(str);
        if (input != null) {
            return loadBytes(input);
        }
        System.err.println("The file \"" + str + "\" " + "is missing or inaccessible, make sure " + "the URL is valid or that the file has been " + "added to your sketch and is readable.");
        return null;
    }
    
    public static byte[] loadBytes(final InputStream in) {
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int i = bufferedInputStream.read(); i != -1; i = bufferedInputStream.read()) {
                byteArrayOutputStream.write(i);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static byte[] loadBytes(final File file) {
        return loadBytes(createInput(file));
    }
    
    public static String[] loadStrings(final File file) {
        final InputStream input = createInput(file);
        if (input != null) {
            return loadStrings(input);
        }
        return null;
    }
    
    public String[] loadStrings(final String str) {
        final InputStream input = this.createInput(str);
        if (input != null) {
            return loadStrings(input);
        }
        System.err.println("The file \"" + str + "\" " + "is missing or inaccessible, make sure " + "the URL is valid or that the file has been " + "added to your sketch and is readable.");
        return null;
    }
    
    public static String[] loadStrings(final InputStream in) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String[] array = new String[100];
            int n = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (n == array.length) {
                    final String[] array2 = new String[n << 1];
                    System.arraycopy(array, 0, array2, 0, n);
                    array = array2;
                }
                array[n++] = line;
            }
            bufferedReader.close();
            if (n == array.length) {
                return array;
            }
            final String[] array3 = new String[n];
            System.arraycopy(array, 0, array3, 0, n);
            return array3;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public OutputStream createOutput(final String s) {
        return createOutput(this.saveFile(s));
    }
    
    public static OutputStream createOutput(final File file) {
        try {
            createPath(file);
            final FileOutputStream out = new FileOutputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                return new GZIPOutputStream(out);
            }
            return out;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public boolean saveStream(final String s, final String s2) {
        return this.saveStream(this.saveFile(s), s2);
    }
    
    public boolean saveStream(final File file, final String s) {
        return saveStream(file, this.createInputRaw(s));
    }
    
    public boolean saveStream(final String s, final InputStream inputStream) {
        return saveStream(this.saveFile(s), inputStream);
    }
    
    public static boolean saveStream(final File dest, final InputStream in) {
        File tempFile = null;
        try {
            final File parentFile = dest.getParentFile();
            createPath(dest);
            tempFile = File.createTempFile(dest.getName(), null, parentFile);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in, 16384);
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            final byte[] array = new byte[8192];
            int read;
            while ((read = bufferedInputStream.read(array)) != -1) {
                bufferedOutputStream.write(array, 0, read);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            if (!tempFile.renameTo(dest)) {
                System.err.println("Could not rename temporary file " + tempFile.getAbsolutePath());
                return false;
            }
            return true;
        }
        catch (IOException ex) {
            if (tempFile != null) {
                tempFile.delete();
            }
            ex.printStackTrace();
            return false;
        }
    }
    
    public void saveBytes(final String s, final byte[] array) {
        saveBytes(this.saveFile(s), array);
    }
    
    public static void saveBytes(final File file, final byte[] array) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(file.getName(), null, file.getParentFile());
            final OutputStream output = createOutput(tempFile);
            saveBytes(output, array);
            output.close();
            if (!tempFile.renameTo(file)) {
                System.err.println("Could not rename temporary file " + tempFile.getAbsolutePath());
            }
        }
        catch (IOException ex) {
            System.err.println("error saving bytes to " + file);
            if (tempFile != null) {
                tempFile.delete();
            }
            ex.printStackTrace();
        }
    }
    
    public static void saveBytes(final OutputStream outputStream, final byte[] b) {
        try {
            outputStream.write(b);
            outputStream.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveStrings(final String s, final String[] array) {
        saveStrings(this.saveFile(s), array);
    }
    
    public static void saveStrings(final File file, final String[] array) {
        saveStrings(createOutput(file), array);
    }
    
    public static void saveStrings(final OutputStream outputStream, final String[] array) {
        final PrintWriter writer = createWriter(outputStream);
        for (int i = 0; i < array.length; ++i) {
            writer.println(array[i]);
        }
        writer.flush();
        writer.close();
    }
    
    public String sketchPath(final String s) {
        if (this.sketchPath == null) {
            return s;
        }
        try {
            if (new File(s).isAbsolute()) {
                return s;
            }
        }
        catch (Exception ex) {}
        return this.sketchPath + File.separator + s;
    }
    
    public File sketchFile(final String s) {
        return new File(this.sketchPath(s));
    }
    
    public String savePath(final String s) {
        if (s == null) {
            return null;
        }
        final String sketchPath = this.sketchPath(s);
        createPath(sketchPath);
        return sketchPath;
    }
    
    public File saveFile(final String s) {
        return new File(this.savePath(s));
    }
    
    public String dataPath(final String s) {
        if (new File(s).isAbsolute()) {
            return s;
        }
        return this.sketchPath + File.separator + "data" + File.separator + s;
    }
    
    public File dataFile(final String s) {
        return new File(this.dataPath(s));
    }
    
    public static void createPath(final String pathname) {
        createPath(new File(pathname));
    }
    
    public static void createPath(final File file) {
        try {
            final String parent = file.getParent();
            if (parent != null) {
                final File file2 = new File(parent);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
            }
        }
        catch (SecurityException ex) {
            System.err.println("You don't have permissions to create " + file.getAbsolutePath());
        }
    }
    
    public static byte[] sort(final byte[] array) {
        return sort(array, array.length);
    }
    
    public static byte[] sort(final byte[] array, final int toIndex) {
        final byte[] a = new byte[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a, 0, toIndex);
        return a;
    }
    
    public static char[] sort(final char[] array) {
        return sort(array, array.length);
    }
    
    public static char[] sort(final char[] array, final int toIndex) {
        final char[] a = new char[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a, 0, toIndex);
        return a;
    }
    
    public static int[] sort(final int[] array) {
        return sort(array, array.length);
    }
    
    public static int[] sort(final int[] array, final int toIndex) {
        final int[] a = new int[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a, 0, toIndex);
        return a;
    }
    
    public static float[] sort(final float[] array) {
        return sort(array, array.length);
    }
    
    public static float[] sort(final float[] array, final int toIndex) {
        final float[] a = new float[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a, 0, toIndex);
        return a;
    }
    
    public static String[] sort(final String[] array) {
        return sort(array, array.length);
    }
    
    public static String[] sort(final String[] array, final int toIndex) {
        final String[] a = new String[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a, 0, toIndex);
        return a;
    }
    
    public static void arrayCopy(final Object o, final int n, final Object o2, final int n2, final int n3) {
        System.arraycopy(o, n, o2, n2, n3);
    }
    
    public static void arrayCopy(final Object o, final Object o2, final int n) {
        System.arraycopy(o, 0, o2, 0, n);
    }
    
    public static void arrayCopy(final Object o, final Object o2) {
        System.arraycopy(o, 0, o2, 0, Array.getLength(o));
    }
    
    @Deprecated
    public static void arraycopy(final Object o, final int n, final Object o2, final int n2, final int n3) {
        System.arraycopy(o, n, o2, n2, n3);
    }
    
    @Deprecated
    public static void arraycopy(final Object o, final Object o2, final int n) {
        System.arraycopy(o, 0, o2, 0, n);
    }
    
    @Deprecated
    public static void arraycopy(final Object o, final Object o2) {
        System.arraycopy(o, 0, o2, 0, Array.getLength(o));
    }
    
    public static boolean[] expand(final boolean[] array) {
        return expand(array, array.length << 1);
    }
    
    public static boolean[] expand(final boolean[] array, final int a) {
        final boolean[] array2 = new boolean[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static byte[] expand(final byte[] array) {
        return expand(array, array.length << 1);
    }
    
    public static byte[] expand(final byte[] array, final int a) {
        final byte[] array2 = new byte[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static char[] expand(final char[] array) {
        return expand(array, array.length << 1);
    }
    
    public static char[] expand(final char[] array, final int a) {
        final char[] array2 = new char[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static int[] expand(final int[] array) {
        return expand(array, array.length << 1);
    }
    
    public static int[] expand(final int[] array, final int a) {
        final int[] array2 = new int[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static float[] expand(final float[] array) {
        return expand(array, array.length << 1);
    }
    
    public static float[] expand(final float[] array, final int a) {
        final float[] array2 = new float[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static String[] expand(final String[] array) {
        return expand(array, array.length << 1);
    }
    
    public static String[] expand(final String[] array, final int a) {
        final String[] array2 = new String[a];
        System.arraycopy(array, 0, array2, 0, Math.min(a, array.length));
        return array2;
    }
    
    public static Object expand(final Object o) {
        return expand(o, Array.getLength(o) << 1);
    }
    
    public static Object expand(final Object o, final int n) {
        final Object instance = Array.newInstance(o.getClass().getComponentType(), n);
        System.arraycopy(o, 0, instance, 0, Math.min(Array.getLength(o), n));
        return instance;
    }
    
    public static byte[] append(byte[] expand, final byte b) {
        expand = expand(expand, expand.length + 1);
        expand[expand.length - 1] = b;
        return expand;
    }
    
    public static char[] append(char[] expand, final char c) {
        expand = expand(expand, expand.length + 1);
        expand[expand.length - 1] = c;
        return expand;
    }
    
    public static int[] append(int[] expand, final int n) {
        expand = expand(expand, expand.length + 1);
        expand[expand.length - 1] = n;
        return expand;
    }
    
    public static float[] append(float[] expand, final float n) {
        expand = expand(expand, expand.length + 1);
        expand[expand.length - 1] = n;
        return expand;
    }
    
    public static String[] append(String[] expand, final String s) {
        expand = expand(expand, expand.length + 1);
        expand[expand.length - 1] = s;
        return expand;
    }
    
    public static Object append(Object expand, final Object o) {
        final int length = Array.getLength(expand);
        expand = expand(expand, length + 1);
        Array.set(expand, length, o);
        return expand;
    }
    
    public static boolean[] shorten(final boolean[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static byte[] shorten(final byte[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static char[] shorten(final char[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static int[] shorten(final int[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static float[] shorten(final float[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static String[] shorten(final String[] array) {
        return subset(array, 0, array.length - 1);
    }
    
    public static Object shorten(final Object o) {
        return subset(o, 0, Array.getLength(o) - 1);
    }
    
    public static final boolean[] splice(final boolean[] array, final boolean b, final int n) {
        final boolean[] array2 = new boolean[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n);
        array2[n] = b;
        System.arraycopy(array, n, array2, n + 1, array.length - n);
        return array2;
    }
    
    public static final boolean[] splice(final boolean[] array, final boolean[] array2, final int n) {
        final boolean[] array3 = new boolean[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final byte[] splice(final byte[] array, final byte b, final int n) {
        final byte[] array2 = new byte[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n);
        array2[n] = b;
        System.arraycopy(array, n, array2, n + 1, array.length - n);
        return array2;
    }
    
    public static final byte[] splice(final byte[] array, final byte[] array2, final int n) {
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final char[] splice(final char[] array, final char c, final int n) {
        final char[] array2 = new char[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n);
        array2[n] = c;
        System.arraycopy(array, n, array2, n + 1, array.length - n);
        return array2;
    }
    
    public static final char[] splice(final char[] array, final char[] array2, final int n) {
        final char[] array3 = new char[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final int[] splice(final int[] array, final int n, final int n2) {
        final int[] array2 = new int[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n2);
        array2[n2] = n;
        System.arraycopy(array, n2, array2, n2 + 1, array.length - n2);
        return array2;
    }
    
    public static final int[] splice(final int[] array, final int[] array2, final int n) {
        final int[] array3 = new int[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final float[] splice(final float[] array, final float n, final int n2) {
        final float[] array2 = new float[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n2);
        array2[n2] = n;
        System.arraycopy(array, n2, array2, n2 + 1, array.length - n2);
        return array2;
    }
    
    public static final float[] splice(final float[] array, final float[] array2, final int n) {
        final float[] array3 = new float[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final String[] splice(final String[] array, final String s, final int n) {
        final String[] array2 = new String[array.length + 1];
        System.arraycopy(array, 0, array2, 0, n);
        array2[n] = s;
        System.arraycopy(array, n, array2, n + 1, array.length - n);
        return array2;
    }
    
    public static final String[] splice(final String[] array, final String[] array2, final int n) {
        final String[] array3 = new String[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array2, 0, array3, n, array2.length);
        System.arraycopy(array, n, array3, n + array2.length, array.length - n);
        return array3;
    }
    
    public static final Object splice(final Object o, final Object o2, final int n) {
        final int length = Array.getLength(o);
        Object[] array;
        if (o2.getClass().getName().charAt(0) == '[') {
            final int length2 = Array.getLength(o2);
            array = new Object[length + length2];
            System.arraycopy(o, 0, array, 0, n);
            System.arraycopy(o2, 0, array, n, length2);
            System.arraycopy(o, n, array, n + length2, length - n);
        }
        else {
            array = new Object[length + 1];
            System.arraycopy(o, 0, array, 0, n);
            Array.set(array, n, o2);
            System.arraycopy(o, n, array, n + 1, length - n);
        }
        return array;
    }
    
    public static boolean[] subset(final boolean[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static boolean[] subset(final boolean[] array, final int n, final int n2) {
        final boolean[] array2 = new boolean[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static byte[] subset(final byte[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static byte[] subset(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static char[] subset(final char[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static char[] subset(final char[] array, final int n, final int n2) {
        final char[] array2 = new char[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static int[] subset(final int[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static int[] subset(final int[] array, final int n, final int n2) {
        final int[] array2 = new int[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static float[] subset(final float[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static float[] subset(final float[] array, final int n, final int n2) {
        final float[] array2 = new float[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static String[] subset(final String[] array, final int n) {
        return subset(array, n, array.length - n);
    }
    
    public static String[] subset(final String[] array, final int n, final int n2) {
        final String[] array2 = new String[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static Object subset(final Object o, final int n) {
        return subset(o, n, Array.getLength(o) - n);
    }
    
    public static Object subset(final Object o, final int n, final int length) {
        final Object instance = Array.newInstance(o.getClass().getComponentType(), length);
        System.arraycopy(o, n, instance, 0, length);
        return instance;
    }
    
    public static boolean[] concat(final boolean[] array, final boolean[] array2) {
        final boolean[] array3 = new boolean[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static byte[] concat(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static char[] concat(final char[] array, final char[] array2) {
        final char[] array3 = new char[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static int[] concat(final int[] array, final int[] array2) {
        final int[] array3 = new int[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static float[] concat(final float[] array, final float[] array2) {
        final float[] array3 = new float[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static String[] concat(final String[] array, final String[] array2) {
        final String[] array3 = new String[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static Object concat(final Object o, final Object o2) {
        final Class<?> componentType = o.getClass().getComponentType();
        final int length = Array.getLength(o);
        final int length2 = Array.getLength(o2);
        final Object instance = Array.newInstance(componentType, length + length2);
        System.arraycopy(o, 0, instance, 0, length);
        System.arraycopy(o2, 0, instance, length, length2);
        return instance;
    }
    
    public static boolean[] reverse(final boolean[] array) {
        final boolean[] array2 = new boolean[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static byte[] reverse(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static char[] reverse(final char[] array) {
        final char[] array2 = new char[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static int[] reverse(final int[] array) {
        final int[] array2 = new int[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static float[] reverse(final float[] array) {
        final float[] array2 = new float[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static String[] reverse(final String[] array) {
        final String[] array2 = new String[array.length];
        final int n = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[n - i];
        }
        return array2;
    }
    
    public static Object reverse(final Object o) {
        final Class<?> componentType = o.getClass().getComponentType();
        final int length = Array.getLength(o);
        final Object instance = Array.newInstance(componentType, length);
        for (int i = 0; i < length; ++i) {
            Array.set(instance, i, Array.get(o, length - 1 - i));
        }
        return instance;
    }
    
    public static String trim(final String s) {
        return s.replace(' ', ' ').trim();
    }
    
    public static String[] trim(final String[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                array2[i] = array[i].replace(' ', ' ').trim();
            }
        }
        return array2;
    }
    
    public static String join(final String[] array, final char c) {
        return join(array, String.valueOf(c));
    }
    
    public static String join(final String[] array, final String str) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                sb.append(str);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    public static String[] splitTokens(final String s) {
        return splitTokens(s, " \t\n\r\f ");
    }
    
    public static String[] splitTokens(final String str, final String delim) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, delim);
        final String[] array = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = stringTokenizer.nextToken();
        }
        return array;
    }
    
    public static String[] split(final String original, final char c) {
        if (original == null) {
            return null;
        }
        final char[] charArray = original.toCharArray();
        int n = 0;
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] == c) {
                ++n;
            }
        }
        if (n == 0) {
            return new String[] { new String(original) };
        }
        final String[] array = new String[n + 1];
        int n2 = 0;
        int n3 = 0;
        for (int j = 0; j < charArray.length; ++j) {
            if (charArray[j] == c) {
                array[n2++] = new String(charArray, n3, j - n3);
                n3 = j + 1;
            }
        }
        array[n2] = new String(charArray, n3, charArray.length - n3);
        return array;
    }
    
    public static String[] split(final String s, final String str) {
        final ArrayList<String> list = new ArrayList<String>();
        int beginIndex;
        int index;
        for (beginIndex = 0; (index = s.indexOf(str, beginIndex)) != -1; beginIndex = index + str.length()) {
            list.add(s.substring(beginIndex, index));
        }
        list.add(s.substring(beginIndex));
        final String[] a = new String[list.size()];
        list.toArray(a);
        return a;
    }
    
    static Pattern matchPattern(final String key) {
        Pattern compile = null;
        if (PApplet.matchPatterns == null) {
            PApplet.matchPatterns = new HashMap<String, Pattern>();
        }
        else {
            compile = PApplet.matchPatterns.get(key);
        }
        if (compile == null) {
            if (PApplet.matchPatterns.size() == 10) {
                PApplet.matchPatterns.clear();
            }
            compile = Pattern.compile(key, 40);
            PApplet.matchPatterns.put(key, compile);
        }
        return compile;
    }
    
    public static String[] match(final String input, final String s) {
        final Matcher matcher = matchPattern(s).matcher(input);
        if (matcher.find()) {
            final int n = matcher.groupCount() + 1;
            final String[] array = new String[n];
            for (int i = 0; i < n; ++i) {
                array[i] = matcher.group(i);
            }
            return array;
        }
        return null;
    }
    
    public static String[][] matchAll(final String input, final String s) {
        final Matcher matcher = matchPattern(s).matcher(input);
        final ArrayList<String[]> list = new ArrayList<String[]>();
        final int n = matcher.groupCount() + 1;
        while (matcher.find()) {
            final String[] e = new String[n];
            for (int i = 0; i < n; ++i) {
                e[i] = matcher.group(i);
            }
            list.add(e);
        }
        if (list.isEmpty()) {
            return null;
        }
        final String[][] array = new String[list.size()][n];
        for (int j = 0; j < array.length; ++j) {
            array[j] = list.get(j);
        }
        return array;
    }
    
    public static final boolean parseBoolean(final int n) {
        return n != 0;
    }
    
    public static final boolean parseBoolean(final String s) {
        return new Boolean(s);
    }
    
    public static final boolean[] parseBoolean(final int[] array) {
        final boolean[] array2 = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (array[i] != 0);
        }
        return array2;
    }
    
    public static final boolean[] parseBoolean(final String[] array) {
        final boolean[] array2 = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new Boolean(array[i]);
        }
        return array2;
    }
    
    public static final byte parseByte(final boolean b) {
        return (byte)(b ? 1 : 0);
    }
    
    public static final byte parseByte(final char c) {
        return (byte)c;
    }
    
    public static final byte parseByte(final int n) {
        return (byte)n;
    }
    
    public static final byte parseByte(final float n) {
        return (byte)n;
    }
    
    public static final byte[] parseByte(final boolean[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)(array[i] ? 1 : 0);
        }
        return array2;
    }
    
    public static final byte[] parseByte(final char[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)array[i];
        }
        return array2;
    }
    
    public static final byte[] parseByte(final int[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)array[i];
        }
        return array2;
    }
    
    public static final byte[] parseByte(final float[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)array[i];
        }
        return array2;
    }
    
    public static final char parseChar(final byte b) {
        return (char)(b & 0xFF);
    }
    
    public static final char parseChar(final int n) {
        return (char)n;
    }
    
    public static final char[] parseChar(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        return array2;
    }
    
    public static final char[] parseChar(final int[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (char)array[i];
        }
        return array2;
    }
    
    public static final int parseInt(final boolean b) {
        return b ? 1 : 0;
    }
    
    public static final int parseInt(final byte b) {
        return b & 0xFF;
    }
    
    public static final int parseInt(final char c) {
        return c;
    }
    
    public static final int parseInt(final float n) {
        return (int)n;
    }
    
    public static final int parseInt(final String s) {
        return parseInt(s, 0);
    }
    
    public static final int parseInt(final String s, final int n) {
        try {
            final int index = s.indexOf(46);
            if (index == -1) {
                return Integer.parseInt(s);
            }
            return Integer.parseInt(s.substring(0, index));
        }
        catch (NumberFormatException ex) {
            return n;
        }
    }
    
    public static final int[] parseInt(final boolean[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (array[i] ? 1 : 0);
        }
        return array2;
    }
    
    public static final int[] parseInt(final byte[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (array[i] & 0xFF);
        }
        return array2;
    }
    
    public static final int[] parseInt(final char[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    public static int[] parseInt(final float[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (int)array[i];
        }
        return array2;
    }
    
    public static int[] parseInt(final String[] array) {
        return parseInt(array, 0);
    }
    
    public static int[] parseInt(final String[] array, final int n) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            try {
                array2[i] = Integer.parseInt(array[i]);
            }
            catch (NumberFormatException ex) {
                array2[i] = n;
            }
        }
        return array2;
    }
    
    public static final float parseFloat(final int n) {
        return (float)n;
    }
    
    public static final float parseFloat(final String s) {
        return parseFloat(s, Float.NaN);
    }
    
    public static final float parseFloat(final String s, final float n) {
        try {
            return new Float(s);
        }
        catch (NumberFormatException ex) {
            return n;
        }
    }
    
    public static final float[] parseByte(final byte[] array) {
        final float[] array2 = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    public static final float[] parseFloat(final int[] array) {
        final float[] array2 = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (float)array[i];
        }
        return array2;
    }
    
    public static final float[] parseFloat(final String[] array) {
        return parseFloat(array, Float.NaN);
    }
    
    public static final float[] parseFloat(final String[] array, final float n) {
        final float[] array2 = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            try {
                array2[i] = new Float(array[i]);
            }
            catch (NumberFormatException ex) {
                array2[i] = n;
            }
        }
        return array2;
    }
    
    public static final String str(final boolean b) {
        return String.valueOf(b);
    }
    
    public static final String str(final byte i) {
        return String.valueOf(i);
    }
    
    public static final String str(final char c) {
        return String.valueOf(c);
    }
    
    public static final String str(final int i) {
        return String.valueOf(i);
    }
    
    public static final String str(final float f) {
        return String.valueOf(f);
    }
    
    public static final String[] str(final boolean[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = String.valueOf(array[i]);
        }
        return array2;
    }
    
    public static final String[] str(final byte[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = String.valueOf(array[i]);
        }
        return array2;
    }
    
    public static final String[] str(final char[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = String.valueOf(array[i]);
        }
        return array2;
    }
    
    public static final String[] str(final int[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = String.valueOf(array[i]);
        }
        return array2;
    }
    
    public static final String[] str(final float[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = String.valueOf(array[i]);
        }
        return array2;
    }
    
    public static String[] nf(final int[] array, final int n) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nf(array[i], n);
        }
        return array2;
    }
    
    public static String nf(final int n, final int n2) {
        if (PApplet.int_nf != null && PApplet.int_nf_digits == n2 && !PApplet.int_nf_commas) {
            return PApplet.int_nf.format(n);
        }
        (PApplet.int_nf = NumberFormat.getInstance()).setGroupingUsed(false);
        PApplet.int_nf_commas = false;
        PApplet.int_nf.setMinimumIntegerDigits(n2);
        PApplet.int_nf_digits = n2;
        return PApplet.int_nf.format(n);
    }
    
    public static String[] nfc(final int[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfc(array[i]);
        }
        return array2;
    }
    
    public static String nfc(final int n) {
        if (PApplet.int_nf != null && PApplet.int_nf_digits == 0 && PApplet.int_nf_commas) {
            return PApplet.int_nf.format(n);
        }
        (PApplet.int_nf = NumberFormat.getInstance()).setGroupingUsed(true);
        PApplet.int_nf_commas = true;
        PApplet.int_nf.setMinimumIntegerDigits(0);
        PApplet.int_nf_digits = 0;
        return PApplet.int_nf.format(n);
    }
    
    public static String nfs(final int n, final int n2) {
        return (n < 0) ? nf(n, n2) : (' ' + nf(n, n2));
    }
    
    public static String[] nfs(final int[] array, final int n) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfs(array[i], n);
        }
        return array2;
    }
    
    public static String nfp(final int n, final int n2) {
        return (n < 0) ? nf(n, n2) : ('+' + nf(n, n2));
    }
    
    public static String[] nfp(final int[] array, final int n) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfp(array[i], n);
        }
        return array2;
    }
    
    public static String[] nf(final float[] array, final int n, final int n2) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nf(array[i], n, n2);
        }
        return array2;
    }
    
    public static String nf(final float n, final int n2, final int float_nf_right) {
        if (PApplet.float_nf != null && PApplet.float_nf_left == n2 && PApplet.float_nf_right == float_nf_right && !PApplet.float_nf_commas) {
            return PApplet.float_nf.format(n);
        }
        (PApplet.float_nf = NumberFormat.getInstance()).setGroupingUsed(false);
        PApplet.float_nf_commas = false;
        if (n2 != 0) {
            PApplet.float_nf.setMinimumIntegerDigits(n2);
        }
        if (float_nf_right != 0) {
            PApplet.float_nf.setMinimumFractionDigits(float_nf_right);
            PApplet.float_nf.setMaximumFractionDigits(float_nf_right);
        }
        PApplet.float_nf_left = n2;
        PApplet.float_nf_right = float_nf_right;
        return PApplet.float_nf.format(n);
    }
    
    public static String[] nfc(final float[] array, final int n) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfc(array[i], n);
        }
        return array2;
    }
    
    public static String nfc(final float n, final int float_nf_right) {
        if (PApplet.float_nf != null && PApplet.float_nf_left == 0 && PApplet.float_nf_right == float_nf_right && PApplet.float_nf_commas) {
            return PApplet.float_nf.format(n);
        }
        (PApplet.float_nf = NumberFormat.getInstance()).setGroupingUsed(true);
        PApplet.float_nf_commas = true;
        if (float_nf_right != 0) {
            PApplet.float_nf.setMinimumFractionDigits(float_nf_right);
            PApplet.float_nf.setMaximumFractionDigits(float_nf_right);
        }
        PApplet.float_nf_left = 0;
        PApplet.float_nf_right = float_nf_right;
        return PApplet.float_nf.format(n);
    }
    
    public static String[] nfs(final float[] array, final int n, final int n2) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfs(array[i], n, n2);
        }
        return array2;
    }
    
    public static String nfs(final float n, final int n2, final int n3) {
        return (n < 0.0f) ? nf(n, n2, n3) : (' ' + nf(n, n2, n3));
    }
    
    public static String[] nfp(final float[] array, final int n, final int n2) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = nfp(array[i], n, n2);
        }
        return array2;
    }
    
    public static String nfp(final float n, final int n2, final int n3) {
        return (n < 0.0f) ? nf(n, n2, n3) : ('+' + nf(n, n2, n3));
    }
    
    public static final String hex(final byte b) {
        return hex(b, 2);
    }
    
    public static final String hex(final char c) {
        return hex(c, 4);
    }
    
    public static final String hex(final int n) {
        return hex(n, 8);
    }
    
    public static final String hex(final int i, final int n) {
        final String upperCase = Integer.toHexString(i).toUpperCase();
        final int length = upperCase.length();
        if (length > n) {
            return upperCase.substring(length - n);
        }
        if (length < n) {
            return "00000000".substring(8 - (n - length)) + upperCase;
        }
        return upperCase;
    }
    
    public static final int unhex(final String s) {
        return (int)Long.parseLong(s, 16);
    }
    
    public static final String binary(final byte b) {
        return binary(b, 8);
    }
    
    public static final String binary(final char c) {
        return binary(c, 16);
    }
    
    public static final String binary(final int i) {
        return Integer.toBinaryString(i);
    }
    
    public static final String binary(final int i, final int n) {
        final String binaryString = Integer.toBinaryString(i);
        final int length = binaryString.length();
        if (length > n) {
            return binaryString.substring(length - n);
        }
        if (length < n) {
            return "00000000000000000000000000000000".substring(32 - (n - length)) + binaryString;
        }
        return binaryString;
    }
    
    public static final int unbinary(final String s) {
        return Integer.parseInt(s, 2);
    }
    
    public final int color(int n) {
        if (this.g == null) {
            if (n > 255) {
                n = 255;
            }
            else if (n < 0) {
                n = 0;
            }
            return 0xFF000000 | n << 16 | n << 8 | n;
        }
        return this.g.color(n);
    }
    
    public final int color(final float n) {
        if (this.g == null) {
            int n2 = (int)n;
            if (n2 > 255) {
                n2 = 255;
            }
            else if (n2 < 0) {
                n2 = 0;
            }
            return 0xFF000000 | n2 << 16 | n2 << 8 | n2;
        }
        return this.g.color(n);
    }
    
    public final int color(final int n, int n2) {
        if (this.g != null) {
            return this.g.color(n, n2);
        }
        if (n2 > 255) {
            n2 = 255;
        }
        else if (n2 < 0) {
            n2 = 0;
        }
        if (n > 255) {
            return n2 << 24 | (n & 0xFFFFFF);
        }
        return n2 << 24 | n << 16 | n << 8 | n;
    }
    
    public final int color(final float n, final float n2) {
        if (this.g == null) {
            int n3 = (int)n;
            final int n4 = (int)n2;
            if (n3 > 255) {
                n3 = 255;
            }
            else if (n3 < 0) {
                n3 = 0;
            }
            if (n4 <= 255) {
                if (n4 < 0) {}
            }
            return 0xFF000000 | n3 << 16 | n3 << 8 | n3;
        }
        return this.g.color(n, n2);
    }
    
    public final int color(int n, int n2, int n3) {
        if (this.g == null) {
            if (n > 255) {
                n = 255;
            }
            else if (n < 0) {
                n = 0;
            }
            if (n2 > 255) {
                n2 = 255;
            }
            else if (n2 < 0) {
                n2 = 0;
            }
            if (n3 > 255) {
                n3 = 255;
            }
            else if (n3 < 0) {
                n3 = 0;
            }
            return 0xFF000000 | n << 16 | n2 << 8 | n3;
        }
        return this.g.color(n, n2, n3);
    }
    
    public final int color(float n, float n2, float n3) {
        if (this.g == null) {
            if (n > 255.0f) {
                n = 255.0f;
            }
            else if (n < 0.0f) {
                n = 0.0f;
            }
            if (n2 > 255.0f) {
                n2 = 255.0f;
            }
            else if (n2 < 0.0f) {
                n2 = 0.0f;
            }
            if (n3 > 255.0f) {
                n3 = 255.0f;
            }
            else if (n3 < 0.0f) {
                n3 = 0.0f;
            }
            return 0xFF000000 | (int)n << 16 | (int)n2 << 8 | (int)n3;
        }
        return this.g.color(n, n2, n3);
    }
    
    public final int color(int n, int n2, int n3, int n4) {
        if (this.g == null) {
            if (n4 > 255) {
                n4 = 255;
            }
            else if (n4 < 0) {
                n4 = 0;
            }
            if (n > 255) {
                n = 255;
            }
            else if (n < 0) {
                n = 0;
            }
            if (n2 > 255) {
                n2 = 255;
            }
            else if (n2 < 0) {
                n2 = 0;
            }
            if (n3 > 255) {
                n3 = 255;
            }
            else if (n3 < 0) {
                n3 = 0;
            }
            return n4 << 24 | n << 16 | n2 << 8 | n3;
        }
        return this.g.color(n, n2, n3, n4);
    }
    
    public final int color(float n, float n2, float n3, float n4) {
        if (this.g == null) {
            if (n4 > 255.0f) {
                n4 = 255.0f;
            }
            else if (n4 < 0.0f) {
                n4 = 0.0f;
            }
            if (n > 255.0f) {
                n = 255.0f;
            }
            else if (n < 0.0f) {
                n = 0.0f;
            }
            if (n2 > 255.0f) {
                n2 = 255.0f;
            }
            else if (n2 < 0.0f) {
                n2 = 0.0f;
            }
            if (n3 > 255.0f) {
                n3 = 255.0f;
            }
            else if (n3 < 0.0f) {
                n3 = 0.0f;
            }
            return (int)n4 << 24 | (int)n << 16 | (int)n2 << 8 | (int)n3;
        }
        return this.g.color(n, n2, n3, n4);
    }
    
    public void setupExternalMessages() {
        this.frame.addComponentListener((ComponentListener)new PApplet.PApplet$6(this));
        this.frame.addWindowListener((WindowListener)new PApplet.PApplet$7(this));
    }
    
    public void setupFrameResizeListener() {
        this.frame.addComponentListener((ComponentListener)new PApplet.PApplet$8(this));
    }
    
    public static void runSketch(final String[] array, final PApplet pApplet) {
        if (PApplet.platform == 2) {
            System.setProperty("apple.awt.graphics.UseQuartz", String.valueOf(PApplet.useQuartz));
        }
        if (array.length < 1) {
            System.err.println("Usage: PApplet <appletname>");
            System.err.println("For additional options, see the Javadoc for PApplet");
            System.exit(1);
        }
        boolean external = false;
        int[] int1 = null;
        int[] int2 = null;
        String s = null;
        boolean b = false;
        boolean b2 = false;
        Color color = Color.BLACK;
        Color gray = Color.GRAY;
        GraphicsDevice defaultScreenDevice = null;
        boolean b3 = false;
        String property = null;
        try {
            property = System.getProperty("user.dir");
        }
        catch (Exception ex) {}
        for (int i = 0; i < array.length; ++i) {
            final int index = array[i].indexOf(61);
            if (index != -1) {
                final String substring = array[i].substring(0, index);
                String s2 = array[i].substring(index + 1);
                if (substring.equals("--editor-location")) {
                    external = true;
                    int2 = parseInt(split(s2, ','));
                }
                else if (substring.equals("--display")) {
                    final int n = Integer.parseInt(s2) - 1;
                    final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                    if (n >= 0 && n < screenDevices.length) {
                        defaultScreenDevice = screenDevices[n];
                    }
                    else {
                        System.err.println("Display " + s2 + " does not exist, " + "using the default display instead.");
                    }
                }
                else if (substring.equals("--bgcolor")) {
                    if (s2.charAt(0) == '#') {
                        s2 = s2.substring(1);
                    }
                    color = new Color(Integer.parseInt(s2, 16));
                }
                else if (substring.equals("--stop-color")) {
                    if (s2.charAt(0) == '#') {
                        s2 = s2.substring(1);
                    }
                    gray = new Color(Integer.parseInt(s2, 16));
                }
                else if (substring.equals("--sketch-path")) {
                    property = s2;
                }
                else if (substring.equals("--location")) {
                    int1 = parseInt(split(s2, ','));
                }
            }
            else if (array[i].equals("--present")) {
                b = true;
            }
            else if (array[i].equals("--exclusive")) {
                b2 = true;
            }
            else if (array[i].equals("--hide-stop")) {
                b3 = true;
            }
            else {
                if (!array[i].equals("--external")) {
                    s = array[i];
                    break;
                }
                external = true;
            }
        }
        if (defaultScreenDevice == null) {
            defaultScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
        final Frame frame = new Frame(defaultScreenDevice.getDefaultConfiguration());
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(PApplet.ICON_IMAGE));
        frame.setTitle(s);
        PApplet comp;
        if (pApplet != null) {
            comp = pApplet;
        }
        else {
            try {
                comp = (PApplet)Thread.currentThread().getContextClassLoader().loadClass(s).newInstance();
            }
            catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        }
        comp.frame = frame;
        comp.sketchPath = property;
        comp.args = subset(array, 1);
        comp.external = external;
        Rectangle bounds = null;
        if (b) {
            frame.setUndecorated(true);
            frame.setBackground(color);
            if (b2) {
                defaultScreenDevice.setFullScreenWindow(frame);
                bounds = frame.getBounds();
            }
            else {
                final DisplayMode displayMode = defaultScreenDevice.getDisplayMode();
                bounds = new Rectangle(0, 0, displayMode.getWidth(), displayMode.getHeight());
                frame.setBounds(bounds);
                frame.setVisible(true);
            }
        }
        frame.setLayout(null);
        frame.add(comp);
        if (b) {
            frame.invalidate();
        }
        else {
            frame.pack();
        }
        frame.setResizable(false);
        comp.init();
        while (comp.defaultSize && !comp.finished) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex2) {}
        }
        if (b) {
            frame.setBounds(bounds);
            comp.setBounds((bounds.width - comp.width) / 2, (bounds.height - comp.height) / 2, comp.width, comp.height);
            if (!b3) {
                final Label comp2 = new Label("stop");
                comp2.setForeground(gray);
                comp2.addMouseListener((MouseListener)new PApplet.PApplet$9());
                frame.add(comp2);
                final Dimension size = new Dimension(100, comp2.getPreferredSize().height);
                comp2.setSize(size);
                comp2.setLocation(20, bounds.height - size.height - 20);
            }
            if (external) {
                comp.setupExternalMessages();
            }
        }
        else {
            final Insets insets = frame.getInsets();
            final int width = Math.max(comp.width, 128) + insets.left + insets.right;
            final int height = Math.max(comp.height, 128) + insets.top + insets.bottom;
            frame.setSize(width, height);
            if (int1 != null) {
                frame.setLocation(int1[0], int1[1]);
            }
            else if (external) {
                final int n2 = int2[0] - 20;
                final int y = int2[1];
                if (n2 - width > 10) {
                    frame.setLocation(n2 - width, y);
                }
                else {
                    int x = int2[0] + 66;
                    int y2 = int2[1] + 66;
                    if (x + width > comp.screenWidth - 33 || y2 + height > comp.screenHeight - 33) {
                        x = (comp.screenWidth - width) / 2;
                        y2 = (comp.screenHeight - height) / 2;
                    }
                    frame.setLocation(x, y2);
                }
            }
            else {
                frame.setLocation((comp.screenWidth - comp.width) / 2, (comp.screenHeight - comp.height) / 2);
            }
            final Point location = frame.getLocation();
            if (location.y < 0) {
                frame.setLocation(location.x, 30);
            }
            if (color == Color.black) {
                color = SystemColor.control;
            }
            frame.setBackground(color);
            comp.setBounds((width - comp.width) / 2, insets.top + (height - insets.top - insets.bottom - comp.height) / 2, comp.width, comp.height);
            if (external) {
                comp.setupExternalMessages();
            }
            else {
                frame.addWindowListener((WindowListener)new PApplet.PApplet$10());
            }
            comp.setupFrameResizeListener();
            if (comp.displayable()) {
                frame.setVisible(true);
            }
        }
    }
    
    public static void main(final String[] array) {
        runSketch(array, null);
    }
    
    protected void runSketch(final String[] array) {
        final String[] array2 = new String[array.length + 1];
        System.arraycopy(array, 0, array2, 0, array.length);
        array2[array.length] = this.getClass().getSimpleName().replaceAll("__[^_]+__\\$", "").replaceAll("\\$\\d+", "");
        runSketch(array2, this);
    }
    
    protected void runSketch() {
        this.runSketch(new String[0]);
    }
    
    public PGraphics beginRecord(final String s, String insertFrame) {
        insertFrame = this.insertFrame(insertFrame);
        final PGraphics graphics = this.createGraphics(this.width, this.height, s, insertFrame);
        this.beginRecord(graphics);
        return graphics;
    }
    
    public void beginRecord(final PGraphics recorder) {
        (this.recorder = recorder).beginDraw();
    }
    
    public void endRecord() {
        if (this.recorder != null) {
            this.recorder.endDraw();
            this.recorder.dispose();
            this.recorder = null;
        }
        if (this.g.isRecording()) {
            this.g.endRecord();
        }
    }
    
    public PGraphics beginRaw(final String s, String insertFrame) {
        insertFrame = this.insertFrame(insertFrame);
        final PGraphics graphics = this.createGraphics(this.width, this.height, s, insertFrame);
        this.g.beginRaw(graphics);
        return graphics;
    }
    
    public void beginRaw(final PGraphics pGraphics) {
        this.g.beginRaw(pGraphics);
    }
    
    public void endRaw() {
        this.g.endRaw();
    }
    
    public PShape beginRecord() {
        return this.g.beginRecord();
    }
    
    public void loadPixels() {
        this.g.loadPixels();
        this.pixels = this.g.pixels;
    }
    
    public void updatePixels() {
        this.g.updatePixels();
    }
    
    public void updatePixels(final int n, final int n2, final int n3, final int n4) {
        this.g.updatePixels(n, n2, n3, n4);
    }
    
    public void flush() {
        if (this.recorder != null) {
            this.recorder.flush();
        }
        this.g.flush();
    }
    
    public void hint(final int n) {
        if (this.recorder != null) {
            this.recorder.hint(n);
        }
        this.g.hint(n);
    }
    
    public boolean hintEnabled(final int n) {
        return this.g.hintEnabled(n);
    }
    
    public void beginShape() {
        if (this.recorder != null) {
            this.recorder.beginShape();
        }
        this.g.beginShape();
    }
    
    public void beginShape(final int n) {
        if (this.recorder != null) {
            this.recorder.beginShape(n);
        }
        this.g.beginShape(n);
    }
    
    public void edge(final boolean b) {
        if (this.recorder != null) {
            this.recorder.edge(b);
        }
        this.g.edge(b);
    }
    
    public void normal(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.normal(n, n2, n3);
        }
        this.g.normal(n, n2, n3);
    }
    
    public void textureMode(final int n) {
        if (this.recorder != null) {
            this.recorder.textureMode(n);
        }
        this.g.textureMode(n);
    }
    
    public void texture(final PImage pImage) {
        if (this.recorder != null) {
            this.recorder.texture(pImage);
        }
        this.g.texture(pImage);
    }
    
    public void noTexture() {
        if (this.recorder != null) {
            this.recorder.noTexture();
        }
        this.g.noTexture();
    }
    
    public void vertex(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.vertex(n, n2);
        }
        this.g.vertex(n, n2);
    }
    
    public void vertex(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.vertex(n, n2, n3);
        }
        this.g.vertex(n, n2, n3);
    }
    
    public void vertexFields(final float[] array) {
        if (this.recorder != null) {
            this.recorder.vertexFields(array);
        }
        this.g.vertexFields(array);
    }
    
    public void vertex(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.vertex(n, n2, n3, n4);
        }
        this.g.vertex(n, n2, n3, n4);
    }
    
    public void vertex(final float n, final float n2, final float n3, final float n4, final float n5) {
        if (this.recorder != null) {
            this.recorder.vertex(n, n2, n3, n4, n5);
        }
        this.g.vertex(n, n2, n3, n4, n5);
    }
    
    public void breakShape() {
        if (this.recorder != null) {
            this.recorder.breakShape();
        }
        this.g.breakShape();
    }
    
    public void endShape() {
        if (this.recorder != null) {
            this.recorder.endShape();
        }
        this.g.endShape();
    }
    
    public void endShape(final int n) {
        if (this.recorder != null) {
            this.recorder.endShape(n);
        }
        this.g.endShape(n);
    }
    
    public void bezierVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.bezierVertex(n, n2, n3, n4, n5, n6);
        }
        this.g.bezierVertex(n, n2, n3, n4, n5, n6);
    }
    
    public void bezierVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        if (this.recorder != null) {
            this.recorder.bezierVertex(n, n2, n3, n4, n5, n6, n7, n8, n9);
        }
        this.g.bezierVertex(n, n2, n3, n4, n5, n6, n7, n8, n9);
    }
    
    public void quadVertex(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.quadVertex(n, n2, n3, n4);
        }
        this.g.quadVertex(n, n2, n3, n4);
    }
    
    public void quadVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.quadVertex(n, n2, n3, n4, n5, n6);
        }
        this.g.quadVertex(n, n2, n3, n4, n5, n6);
    }
    
    public void curveVertex(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.curveVertex(n, n2);
        }
        this.g.curveVertex(n, n2);
    }
    
    public void curveVertex(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.curveVertex(n, n2, n3);
        }
        this.g.curveVertex(n, n2, n3);
    }
    
    public void point(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.point(n, n2);
        }
        this.g.point(n, n2);
    }
    
    public void point(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.point(n, n2, n3);
        }
        this.g.point(n, n2, n3);
    }
    
    public void line(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.line(n, n2, n3, n4);
        }
        this.g.line(n, n2, n3, n4);
    }
    
    public void line(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.line(n, n2, n3, n4, n5, n6);
        }
        this.g.line(n, n2, n3, n4, n5, n6);
    }
    
    public void triangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.triangle(n, n2, n3, n4, n5, n6);
        }
        this.g.triangle(n, n2, n3, n4, n5, n6);
    }
    
    public void quad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        if (this.recorder != null) {
            this.recorder.quad(n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.quad(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void rectMode(final int n) {
        if (this.recorder != null) {
            this.recorder.rectMode(n);
        }
        this.g.rectMode(n);
    }
    
    public void rect(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.rect(n, n2, n3, n4);
        }
        this.g.rect(n, n2, n3, n4);
    }
    
    public void rect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.rect(n, n2, n3, n4, n5, n6);
        }
        this.g.rect(n, n2, n3, n4, n5, n6);
    }
    
    public void rect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        if (this.recorder != null) {
            this.recorder.rect(n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.rect(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void ellipseMode(final int n) {
        if (this.recorder != null) {
            this.recorder.ellipseMode(n);
        }
        this.g.ellipseMode(n);
    }
    
    public void ellipse(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.ellipse(n, n2, n3, n4);
        }
        this.g.ellipse(n, n2, n3, n4);
    }
    
    public void arc(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.arc(n, n2, n3, n4, n5, n6);
        }
        this.g.arc(n, n2, n3, n4, n5, n6);
    }
    
    public void box(final float n) {
        if (this.recorder != null) {
            this.recorder.box(n);
        }
        this.g.box(n);
    }
    
    public void box(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.box(n, n2, n3);
        }
        this.g.box(n, n2, n3);
    }
    
    public void sphereDetail(final int n) {
        if (this.recorder != null) {
            this.recorder.sphereDetail(n);
        }
        this.g.sphereDetail(n);
    }
    
    public void sphereDetail(final int n, final int n2) {
        if (this.recorder != null) {
            this.recorder.sphereDetail(n, n2);
        }
        this.g.sphereDetail(n, n2);
    }
    
    public void sphere(final float n) {
        if (this.recorder != null) {
            this.recorder.sphere(n);
        }
        this.g.sphere(n);
    }
    
    public float bezierPoint(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.g.bezierPoint(n, n2, n3, n4, n5);
    }
    
    public float bezierTangent(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.g.bezierTangent(n, n2, n3, n4, n5);
    }
    
    public void bezierDetail(final int n) {
        if (this.recorder != null) {
            this.recorder.bezierDetail(n);
        }
        this.g.bezierDetail(n);
    }
    
    public void bezier(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        if (this.recorder != null) {
            this.recorder.bezier(n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.bezier(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void bezier(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (this.recorder != null) {
            this.recorder.bezier(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        }
        this.g.bezier(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    public float curvePoint(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.g.curvePoint(n, n2, n3, n4, n5);
    }
    
    public float curveTangent(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.g.curveTangent(n, n2, n3, n4, n5);
    }
    
    public void curveDetail(final int n) {
        if (this.recorder != null) {
            this.recorder.curveDetail(n);
        }
        this.g.curveDetail(n);
    }
    
    public void curveTightness(final float n) {
        if (this.recorder != null) {
            this.recorder.curveTightness(n);
        }
        this.g.curveTightness(n);
    }
    
    public void curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        if (this.recorder != null) {
            this.recorder.curve(n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.curve(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (this.recorder != null) {
            this.recorder.curve(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        }
        this.g.curve(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    public void smooth() {
        if (this.recorder != null) {
            this.recorder.smooth();
        }
        this.g.smooth();
    }
    
    public void noSmooth() {
        if (this.recorder != null) {
            this.recorder.noSmooth();
        }
        this.g.noSmooth();
    }
    
    public void imageMode(final int n) {
        if (this.recorder != null) {
            this.recorder.imageMode(n);
        }
        this.g.imageMode(n);
    }
    
    public void image(final PImage pImage, final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.image(pImage, n, n2);
        }
        this.g.image(pImage, n, n2);
    }
    
    public void image(final PImage pImage, final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.image(pImage, n, n2, n3, n4);
        }
        this.g.image(pImage, n, n2, n3, n4);
    }
    
    public void image(final PImage pImage, final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8) {
        if (this.recorder != null) {
            this.recorder.image(pImage, n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.image(pImage, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void shapeMode(final int n) {
        if (this.recorder != null) {
            this.recorder.shapeMode(n);
        }
        this.g.shapeMode(n);
    }
    
    public void shape(final PShape pShape) {
        if (this.recorder != null) {
            this.recorder.shape(pShape);
        }
        this.g.shape(pShape);
    }
    
    public void shape(final PShape pShape, final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.shape(pShape, n, n2);
        }
        this.g.shape(pShape, n, n2);
    }
    
    public void shape(final PShape pShape, final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.shape(pShape, n, n2, n3, n4);
        }
        this.g.shape(pShape, n, n2, n3, n4);
    }
    
    public void textAlign(final int n) {
        if (this.recorder != null) {
            this.recorder.textAlign(n);
        }
        this.g.textAlign(n);
    }
    
    public void textAlign(final int n, final int n2) {
        if (this.recorder != null) {
            this.recorder.textAlign(n, n2);
        }
        this.g.textAlign(n, n2);
    }
    
    public float textAscent() {
        return this.g.textAscent();
    }
    
    public float textDescent() {
        return this.g.textDescent();
    }
    
    public void textFont(final PFont pFont) {
        if (this.recorder != null) {
            this.recorder.textFont(pFont);
        }
        this.g.textFont(pFont);
    }
    
    public void textFont(final PFont pFont, final float n) {
        if (this.recorder != null) {
            this.recorder.textFont(pFont, n);
        }
        this.g.textFont(pFont, n);
    }
    
    public void textLeading(final float n) {
        if (this.recorder != null) {
            this.recorder.textLeading(n);
        }
        this.g.textLeading(n);
    }
    
    public void textMode(final int n) {
        if (this.recorder != null) {
            this.recorder.textMode(n);
        }
        this.g.textMode(n);
    }
    
    public void textSize(final float n) {
        if (this.recorder != null) {
            this.recorder.textSize(n);
        }
        this.g.textSize(n);
    }
    
    public float textWidth(final char c) {
        return this.g.textWidth(c);
    }
    
    public float textWidth(final String s) {
        return this.g.textWidth(s);
    }
    
    public float textWidth(final char[] array, final int n, final int n2) {
        return this.g.textWidth(array, n, n2);
    }
    
    public void text(final char c) {
        if (this.recorder != null) {
            this.recorder.text(c);
        }
        this.g.text(c);
    }
    
    public void text(final char c, final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.text(c, n, n2);
        }
        this.g.text(c, n, n2);
    }
    
    public void text(final char c, final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.text(c, n, n2, n3);
        }
        this.g.text(c, n, n2, n3);
    }
    
    public void text(final String s) {
        if (this.recorder != null) {
            this.recorder.text(s);
        }
        this.g.text(s);
    }
    
    public void text(final String s, final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.text(s, n, n2);
        }
        this.g.text(s, n, n2);
    }
    
    public void text(final char[] array, final int n, final int n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.text(array, n, n2, n3, n4);
        }
        this.g.text(array, n, n2, n3, n4);
    }
    
    public void text(final String s, final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.text(s, n, n2, n3);
        }
        this.g.text(s, n, n2, n3);
    }
    
    public void text(final char[] array, final int n, final int n2, final float n3, final float n4, final float n5) {
        if (this.recorder != null) {
            this.recorder.text(array, n, n2, n3, n4, n5);
        }
        this.g.text(array, n, n2, n3, n4, n5);
    }
    
    public void text(final String s, final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.text(s, n, n2, n3, n4);
        }
        this.g.text(s, n, n2, n3, n4);
    }
    
    public void text(final String s, final float n, final float n2, final float n3, final float n4, final float n5) {
        if (this.recorder != null) {
            this.recorder.text(s, n, n2, n3, n4, n5);
        }
        this.g.text(s, n, n2, n3, n4, n5);
    }
    
    public void text(final int n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.text(n, n2, n3);
        }
        this.g.text(n, n2, n3);
    }
    
    public void text(final int n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.text(n, n2, n3, n4);
        }
        this.g.text(n, n2, n3, n4);
    }
    
    public void text(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.text(n, n2, n3);
        }
        this.g.text(n, n2, n3);
    }
    
    public void text(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.text(n, n2, n3, n4);
        }
        this.g.text(n, n2, n3, n4);
    }
    
    public void pushMatrix() {
        if (this.recorder != null) {
            this.recorder.pushMatrix();
        }
        this.g.pushMatrix();
    }
    
    public void popMatrix() {
        if (this.recorder != null) {
            this.recorder.popMatrix();
        }
        this.g.popMatrix();
    }
    
    public void translate(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.translate(n, n2);
        }
        this.g.translate(n, n2);
    }
    
    public void translate(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.translate(n, n2, n3);
        }
        this.g.translate(n, n2, n3);
    }
    
    public void rotate(final float n) {
        if (this.recorder != null) {
            this.recorder.rotate(n);
        }
        this.g.rotate(n);
    }
    
    public void rotateX(final float n) {
        if (this.recorder != null) {
            this.recorder.rotateX(n);
        }
        this.g.rotateX(n);
    }
    
    public void rotateY(final float n) {
        if (this.recorder != null) {
            this.recorder.rotateY(n);
        }
        this.g.rotateY(n);
    }
    
    public void rotateZ(final float n) {
        if (this.recorder != null) {
            this.recorder.rotateZ(n);
        }
        this.g.rotateZ(n);
    }
    
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.rotate(n, n2, n3, n4);
        }
        this.g.rotate(n, n2, n3, n4);
    }
    
    public void scale(final float n) {
        if (this.recorder != null) {
            this.recorder.scale(n);
        }
        this.g.scale(n);
    }
    
    public void scale(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.scale(n, n2);
        }
        this.g.scale(n, n2);
    }
    
    public void scale(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.scale(n, n2, n3);
        }
        this.g.scale(n, n2, n3);
    }
    
    public void shearX(final float n) {
        if (this.recorder != null) {
            this.recorder.shearX(n);
        }
        this.g.shearX(n);
    }
    
    public void shearY(final float n) {
        if (this.recorder != null) {
            this.recorder.shearY(n);
        }
        this.g.shearY(n);
    }
    
    public void resetMatrix() {
        if (this.recorder != null) {
            this.recorder.resetMatrix();
        }
        this.g.resetMatrix();
    }
    
    public void applyMatrix(final PMatrix pMatrix) {
        if (this.recorder != null) {
            this.recorder.applyMatrix(pMatrix);
        }
        this.g.applyMatrix(pMatrix);
    }
    
    public void applyMatrix(final PMatrix2D pMatrix2D) {
        if (this.recorder != null) {
            this.recorder.applyMatrix(pMatrix2D);
        }
        this.g.applyMatrix(pMatrix2D);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.applyMatrix(n, n2, n3, n4, n5, n6);
        }
        this.g.applyMatrix(n, n2, n3, n4, n5, n6);
    }
    
    public void applyMatrix(final PMatrix3D pMatrix3D) {
        if (this.recorder != null) {
            this.recorder.applyMatrix(pMatrix3D);
        }
        this.g.applyMatrix(pMatrix3D);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        if (this.recorder != null) {
            this.recorder.applyMatrix(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
        }
        this.g.applyMatrix(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
    }
    
    public PMatrix getMatrix() {
        return this.g.getMatrix();
    }
    
    public PMatrix2D getMatrix(final PMatrix2D pMatrix2D) {
        return this.g.getMatrix(pMatrix2D);
    }
    
    public PMatrix3D getMatrix(final PMatrix3D pMatrix3D) {
        return this.g.getMatrix(pMatrix3D);
    }
    
    public void setMatrix(final PMatrix pMatrix) {
        if (this.recorder != null) {
            this.recorder.setMatrix(pMatrix);
        }
        this.g.setMatrix(pMatrix);
    }
    
    public void setMatrix(final PMatrix2D pMatrix2D) {
        if (this.recorder != null) {
            this.recorder.setMatrix(pMatrix2D);
        }
        this.g.setMatrix(pMatrix2D);
    }
    
    public void setMatrix(final PMatrix3D pMatrix3D) {
        if (this.recorder != null) {
            this.recorder.setMatrix(pMatrix3D);
        }
        this.g.setMatrix(pMatrix3D);
    }
    
    public void printMatrix() {
        if (this.recorder != null) {
            this.recorder.printMatrix();
        }
        this.g.printMatrix();
    }
    
    public void beginCamera() {
        if (this.recorder != null) {
            this.recorder.beginCamera();
        }
        this.g.beginCamera();
    }
    
    public void endCamera() {
        if (this.recorder != null) {
            this.recorder.endCamera();
        }
        this.g.endCamera();
    }
    
    public void camera() {
        if (this.recorder != null) {
            this.recorder.camera();
        }
        this.g.camera();
    }
    
    public void camera(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        if (this.recorder != null) {
            this.recorder.camera(n, n2, n3, n4, n5, n6, n7, n8, n9);
        }
        this.g.camera(n, n2, n3, n4, n5, n6, n7, n8, n9);
    }
    
    public void printCamera() {
        if (this.recorder != null) {
            this.recorder.printCamera();
        }
        this.g.printCamera();
    }
    
    public void ortho() {
        if (this.recorder != null) {
            this.recorder.ortho();
        }
        this.g.ortho();
    }
    
    public void ortho(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.ortho(n, n2, n3, n4);
        }
        this.g.ortho(n, n2, n3, n4);
    }
    
    public void ortho(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.ortho(n, n2, n3, n4, n5, n6);
        }
        this.g.ortho(n, n2, n3, n4, n5, n6);
    }
    
    public void perspective() {
        if (this.recorder != null) {
            this.recorder.perspective();
        }
        this.g.perspective();
    }
    
    public void perspective(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.perspective(n, n2, n3, n4);
        }
        this.g.perspective(n, n2, n3, n4);
    }
    
    public void frustum(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.frustum(n, n2, n3, n4, n5, n6);
        }
        this.g.frustum(n, n2, n3, n4, n5, n6);
    }
    
    public void printProjection() {
        if (this.recorder != null) {
            this.recorder.printProjection();
        }
        this.g.printProjection();
    }
    
    public float screenX(final float n, final float n2) {
        return this.g.screenX(n, n2);
    }
    
    public float screenY(final float n, final float n2) {
        return this.g.screenY(n, n2);
    }
    
    public float screenX(final float n, final float n2, final float n3) {
        return this.g.screenX(n, n2, n3);
    }
    
    public float screenY(final float n, final float n2, final float n3) {
        return this.g.screenY(n, n2, n3);
    }
    
    public float screenZ(final float n, final float n2, final float n3) {
        return this.g.screenZ(n, n2, n3);
    }
    
    public float modelX(final float n, final float n2, final float n3) {
        return this.g.modelX(n, n2, n3);
    }
    
    public float modelY(final float n, final float n2, final float n3) {
        return this.g.modelY(n, n2, n3);
    }
    
    public float modelZ(final float n, final float n2, final float n3) {
        return this.g.modelZ(n, n2, n3);
    }
    
    public void pushStyle() {
        if (this.recorder != null) {
            this.recorder.pushStyle();
        }
        this.g.pushStyle();
    }
    
    public void popStyle() {
        if (this.recorder != null) {
            this.recorder.popStyle();
        }
        this.g.popStyle();
    }
    
    public void style(final PStyle pStyle) {
        if (this.recorder != null) {
            this.recorder.style(pStyle);
        }
        this.g.style(pStyle);
    }
    
    public void strokeWeight(final float n) {
        if (this.recorder != null) {
            this.recorder.strokeWeight(n);
        }
        this.g.strokeWeight(n);
    }
    
    public void strokeJoin(final int n) {
        if (this.recorder != null) {
            this.recorder.strokeJoin(n);
        }
        this.g.strokeJoin(n);
    }
    
    public void strokeCap(final int n) {
        if (this.recorder != null) {
            this.recorder.strokeCap(n);
        }
        this.g.strokeCap(n);
    }
    
    public void noStroke() {
        if (this.recorder != null) {
            this.recorder.noStroke();
        }
        this.g.noStroke();
    }
    
    public void stroke(final int n) {
        if (this.recorder != null) {
            this.recorder.stroke(n);
        }
        this.g.stroke(n);
    }
    
    public void stroke(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.stroke(n, n2);
        }
        this.g.stroke(n, n2);
    }
    
    public void stroke(final float n) {
        if (this.recorder != null) {
            this.recorder.stroke(n);
        }
        this.g.stroke(n);
    }
    
    public void stroke(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.stroke(n, n2);
        }
        this.g.stroke(n, n2);
    }
    
    public void stroke(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.stroke(n, n2, n3);
        }
        this.g.stroke(n, n2, n3);
    }
    
    public void stroke(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.stroke(n, n2, n3, n4);
        }
        this.g.stroke(n, n2, n3, n4);
    }
    
    public void noTint() {
        if (this.recorder != null) {
            this.recorder.noTint();
        }
        this.g.noTint();
    }
    
    public void tint(final int n) {
        if (this.recorder != null) {
            this.recorder.tint(n);
        }
        this.g.tint(n);
    }
    
    public void tint(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.tint(n, n2);
        }
        this.g.tint(n, n2);
    }
    
    public void tint(final float n) {
        if (this.recorder != null) {
            this.recorder.tint(n);
        }
        this.g.tint(n);
    }
    
    public void tint(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.tint(n, n2);
        }
        this.g.tint(n, n2);
    }
    
    public void tint(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.tint(n, n2, n3);
        }
        this.g.tint(n, n2, n3);
    }
    
    public void tint(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.tint(n, n2, n3, n4);
        }
        this.g.tint(n, n2, n3, n4);
    }
    
    public void noFill() {
        if (this.recorder != null) {
            this.recorder.noFill();
        }
        this.g.noFill();
    }
    
    public void fill(final int n) {
        if (this.recorder != null) {
            this.recorder.fill(n);
        }
        this.g.fill(n);
    }
    
    public void fill(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.fill(n, n2);
        }
        this.g.fill(n, n2);
    }
    
    public void fill(final float n) {
        if (this.recorder != null) {
            this.recorder.fill(n);
        }
        this.g.fill(n);
    }
    
    public void fill(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.fill(n, n2);
        }
        this.g.fill(n, n2);
    }
    
    public void fill(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.fill(n, n2, n3);
        }
        this.g.fill(n, n2, n3);
    }
    
    public void fill(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.fill(n, n2, n3, n4);
        }
        this.g.fill(n, n2, n3, n4);
    }
    
    public void ambient(final int n) {
        if (this.recorder != null) {
            this.recorder.ambient(n);
        }
        this.g.ambient(n);
    }
    
    public void ambient(final float n) {
        if (this.recorder != null) {
            this.recorder.ambient(n);
        }
        this.g.ambient(n);
    }
    
    public void ambient(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.ambient(n, n2, n3);
        }
        this.g.ambient(n, n2, n3);
    }
    
    public void specular(final int n) {
        if (this.recorder != null) {
            this.recorder.specular(n);
        }
        this.g.specular(n);
    }
    
    public void specular(final float n) {
        if (this.recorder != null) {
            this.recorder.specular(n);
        }
        this.g.specular(n);
    }
    
    public void specular(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.specular(n, n2, n3);
        }
        this.g.specular(n, n2, n3);
    }
    
    public void shininess(final float n) {
        if (this.recorder != null) {
            this.recorder.shininess(n);
        }
        this.g.shininess(n);
    }
    
    public void emissive(final int n) {
        if (this.recorder != null) {
            this.recorder.emissive(n);
        }
        this.g.emissive(n);
    }
    
    public void emissive(final float n) {
        if (this.recorder != null) {
            this.recorder.emissive(n);
        }
        this.g.emissive(n);
    }
    
    public void emissive(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.emissive(n, n2, n3);
        }
        this.g.emissive(n, n2, n3);
    }
    
    public void lights() {
        if (this.recorder != null) {
            this.recorder.lights();
        }
        this.g.lights();
    }
    
    public void noLights() {
        if (this.recorder != null) {
            this.recorder.noLights();
        }
        this.g.noLights();
    }
    
    public void ambientLight(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.ambientLight(n, n2, n3);
        }
        this.g.ambientLight(n, n2, n3);
    }
    
    public void ambientLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.ambientLight(n, n2, n3, n4, n5, n6);
        }
        this.g.ambientLight(n, n2, n3, n4, n5, n6);
    }
    
    public void directionalLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.directionalLight(n, n2, n3, n4, n5, n6);
        }
        this.g.directionalLight(n, n2, n3, n4, n5, n6);
    }
    
    public void pointLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.recorder != null) {
            this.recorder.pointLight(n, n2, n3, n4, n5, n6);
        }
        this.g.pointLight(n, n2, n3, n4, n5, n6);
    }
    
    public void spotLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11) {
        if (this.recorder != null) {
            this.recorder.spotLight(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);
        }
        this.g.spotLight(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);
    }
    
    public void lightFalloff(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.lightFalloff(n, n2, n3);
        }
        this.g.lightFalloff(n, n2, n3);
    }
    
    public void lightSpecular(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.lightSpecular(n, n2, n3);
        }
        this.g.lightSpecular(n, n2, n3);
    }
    
    public void background(final int n) {
        if (this.recorder != null) {
            this.recorder.background(n);
        }
        this.g.background(n);
    }
    
    public void background(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.background(n, n2);
        }
        this.g.background(n, n2);
    }
    
    public void background(final float n) {
        if (this.recorder != null) {
            this.recorder.background(n);
        }
        this.g.background(n);
    }
    
    public void background(final float n, final float n2) {
        if (this.recorder != null) {
            this.recorder.background(n, n2);
        }
        this.g.background(n, n2);
    }
    
    public void background(final float n, final float n2, final float n3) {
        if (this.recorder != null) {
            this.recorder.background(n, n2, n3);
        }
        this.g.background(n, n2, n3);
    }
    
    public void background(final float n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.background(n, n2, n3, n4);
        }
        this.g.background(n, n2, n3, n4);
    }
    
    public void background(final PImage pImage) {
        if (this.recorder != null) {
            this.recorder.background(pImage);
        }
        this.g.background(pImage);
    }
    
    public void colorMode(final int n) {
        if (this.recorder != null) {
            this.recorder.colorMode(n);
        }
        this.g.colorMode(n);
    }
    
    public void colorMode(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.colorMode(n, n2);
        }
        this.g.colorMode(n, n2);
    }
    
    public void colorMode(final int n, final float n2, final float n3, final float n4) {
        if (this.recorder != null) {
            this.recorder.colorMode(n, n2, n3, n4);
        }
        this.g.colorMode(n, n2, n3, n4);
    }
    
    public void colorMode(final int n, final float n2, final float n3, final float n4, final float n5) {
        if (this.recorder != null) {
            this.recorder.colorMode(n, n2, n3, n4, n5);
        }
        this.g.colorMode(n, n2, n3, n4, n5);
    }
    
    public final float alpha(final int n) {
        return this.g.alpha(n);
    }
    
    public final float red(final int n) {
        return this.g.red(n);
    }
    
    public final float green(final int n) {
        return this.g.green(n);
    }
    
    public final float blue(final int n) {
        return this.g.blue(n);
    }
    
    public final float hue(final int n) {
        return this.g.hue(n);
    }
    
    public final float saturation(final int n) {
        return this.g.saturation(n);
    }
    
    public final float brightness(final int n) {
        return this.g.brightness(n);
    }
    
    public int lerpColor(final int n, final int n2, final float n3) {
        return this.g.lerpColor(n, n2, n3);
    }
    
    public static int lerpColor(final int n, final int n2, final float n3, final int n4) {
        return PGraphics.lerpColor(n, n2, n3, n4);
    }
    
    public static void showDepthWarning(final String s) {
        PGraphics.showDepthWarning(s);
    }
    
    public static void showDepthWarningXYZ(final String s) {
        PGraphics.showDepthWarningXYZ(s);
    }
    
    public static void showMethodWarning(final String s) {
        PGraphics.showMethodWarning(s);
    }
    
    public static void showVariationWarning(final String s) {
        PGraphics.showVariationWarning(s);
    }
    
    public static void showMissingWarning(final String s) {
        PGraphics.showMissingWarning(s);
    }
    
    public boolean displayable() {
        return this.g.displayable();
    }
    
    public void screenBlend(final int n) {
        if (this.recorder != null) {
            this.recorder.screenBlend(n);
        }
        this.g.screenBlend(n);
    }
    
    public void textureBlend(final int n) {
        if (this.recorder != null) {
            this.recorder.textureBlend(n);
        }
        this.g.textureBlend(n);
    }
    
    public boolean isRecording() {
        return this.g.isRecording();
    }
    
    public void mergeShapes(final boolean b) {
        if (this.recorder != null) {
            this.recorder.mergeShapes(b);
        }
        this.g.mergeShapes(b);
    }
    
    public void shapeName(final String s) {
        if (this.recorder != null) {
            this.recorder.shapeName(s);
        }
        this.g.shapeName(s);
    }
    
    public void autoNormal(final boolean b) {
        if (this.recorder != null) {
            this.recorder.autoNormal(b);
        }
        this.g.autoNormal(b);
    }
    
    public void matrixMode(final int n) {
        if (this.recorder != null) {
            this.recorder.matrixMode(n);
        }
        this.g.matrixMode(n);
    }
    
    public void beginText() {
        if (this.recorder != null) {
            this.recorder.beginText();
        }
        this.g.beginText();
    }
    
    public void endText() {
        if (this.recorder != null) {
            this.recorder.endText();
        }
        this.g.endText();
    }
    
    public void texture(final PImage... array) {
        if (this.recorder != null) {
            this.recorder.texture(array);
        }
        this.g.texture(array);
    }
    
    public void vertex(final float... array) {
        if (this.recorder != null) {
            this.recorder.vertex(array);
        }
        this.g.vertex(array);
    }
    
    public void delete() {
        if (this.recorder != null) {
            this.recorder.delete();
        }
        this.g.delete();
    }
    
    public void setCache(final PGraphics pGraphics, final Object o) {
        if (this.recorder != null) {
            this.recorder.setCache(pGraphics, o);
        }
        this.g.setCache(pGraphics, o);
    }
    
    public Object getCache(final PGraphics pGraphics) {
        return this.g.getCache(pGraphics);
    }
    
    public void removeCache(final PGraphics pGraphics) {
        if (this.recorder != null) {
            this.recorder.removeCache(pGraphics);
        }
        this.g.removeCache(pGraphics);
    }
    
    public void setParams(final PGraphics pGraphics, final Object o) {
        if (this.recorder != null) {
            this.recorder.setParams(pGraphics, o);
        }
        this.g.setParams(pGraphics, o);
    }
    
    public Object getParams(final PGraphics pGraphics) {
        return this.g.getParams(pGraphics);
    }
    
    public void removeParams(final PGraphics pGraphics) {
        if (this.recorder != null) {
            this.recorder.removeParams(pGraphics);
        }
        this.g.removeParams(pGraphics);
    }
    
    public int get(final int n, final int n2) {
        return this.g.get(n, n2);
    }
    
    public PImage get(final int n, final int n2, final int n3, final int n4) {
        return this.g.get(n, n2, n3, n4);
    }
    
    public PImage get() {
        return this.g.get();
    }
    
    public void set(final int n, final int n2, final int n3) {
        if (this.recorder != null) {
            this.recorder.set(n, n2, n3);
        }
        this.g.set(n, n2, n3);
    }
    
    public void set(final int n, final int n2, final PImage pImage) {
        if (this.recorder != null) {
            this.recorder.set(n, n2, pImage);
        }
        this.g.set(n, n2, pImage);
    }
    
    public void mask(final int[] array) {
        if (this.recorder != null) {
            this.recorder.mask(array);
        }
        this.g.mask(array);
    }
    
    public void mask(final PImage pImage) {
        if (this.recorder != null) {
            this.recorder.mask(pImage);
        }
        this.g.mask(pImage);
    }
    
    public void filter(final int n) {
        if (this.recorder != null) {
            this.recorder.filter(n);
        }
        this.g.filter(n);
    }
    
    public void filter(final int n, final float n2) {
        if (this.recorder != null) {
            this.recorder.filter(n, n2);
        }
        this.g.filter(n, n2);
    }
    
    public void copy(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (this.recorder != null) {
            this.recorder.copy(n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.copy(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void copy(final PImage pImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (this.recorder != null) {
            this.recorder.copy(pImage, n, n2, n3, n4, n5, n6, n7, n8);
        }
        this.g.copy(pImage, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public static int blendColor(final int n, final int n2, final int n3) {
        return PGraphics.blendColor(n, n2, n3);
    }
    
    public void blend(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9) {
        if (this.recorder != null) {
            this.recorder.blend(n, n2, n3, n4, n5, n6, n7, n8, n9);
        }
        this.g.blend(n, n2, n3, n4, n5, n6, n7, n8, n9);
    }
    
    public void blend(final PImage pImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9) {
        if (this.recorder != null) {
            this.recorder.blend(pImage, n, n2, n3, n4, n5, n6, n7, n8, n9);
        }
        this.g.blend(pImage, n, n2, n3, n4, n5, n6, n7, n8, n9);
    }
    
    static {
        javaVersionName = System.getProperty("java.version");
        javaVersion = new Float(PApplet.javaVersionName.substring(0, 3));
        final String property = System.getProperty("os.name");
        if (property.indexOf("Mac") != -1) {
            PApplet.platform = 2;
        }
        else if (property.indexOf("Windows") != -1) {
            PApplet.platform = 1;
        }
        else if (property.equals("Linux")) {
            PApplet.platform = 3;
        }
        else {
            PApplet.platform = 0;
        }
        PApplet.useQuartz = true;
        MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        ICON_IMAGE = new byte[] { 71, 73, 70, 56, 57, 97, 16, 0, 16, 0, -77, 0, 0, 0, 0, 0, -1, -1, -1, 12, 12, 13, -15, -15, -14, 45, 57, 74, 54, 80, 111, 47, 71, 97, 62, 88, 117, 1, 14, 27, 7, 41, 73, 15, 52, 85, 2, 31, 55, 4, 54, 94, 18, 69, 109, 37, 87, 126, -1, -1, -1, 33, -7, 4, 1, 0, 0, 15, 0, 44, 0, 0, 0, 0, 16, 0, 16, 0, 0, 4, 122, -16, -107, 114, -86, -67, 83, 30, -42, 26, -17, -100, -45, 56, -57, -108, 48, 40, 122, -90, 104, 67, -91, -51, 32, -53, 77, -78, -100, 47, -86, 12, 76, -110, -20, -74, -101, 97, -93, 27, 40, 20, -65, 65, 48, -111, 99, -20, -112, -117, -123, -47, -105, 24, 114, -112, 74, 69, 84, 25, 93, 88, -75, 9, 46, 2, 49, 88, -116, -67, 7, -19, -83, 60, 38, 3, -34, 2, 66, -95, 27, -98, 13, 4, -17, 55, 33, 109, 11, 11, -2, -128, 121, 123, 62, 91, 120, -128, 127, 122, 115, 102, 2, 119, 0, -116, -113, -119, 6, 102, 121, -108, -126, 5, 18, 6, 4, -102, -101, -100, 114, 15, 17, 0, 59 };
    }
}
