package processing.core;

import java.awt.Dimension;

class PApplet$1 implements Runnable {
    final /* synthetic */ int val$iwidth;
    final /* synthetic */ int val$iheight;
    
    public void run() {
        PApplet.this.setPreferredSize(new Dimension(this.val$iwidth, this.val$iheight));
        PApplet.this.setSize(this.val$iwidth, this.val$iheight);
    }
}