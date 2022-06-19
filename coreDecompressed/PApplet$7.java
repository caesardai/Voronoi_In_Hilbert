package processing.core;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

class PApplet$7 extends WindowAdapter {
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        PApplet.this.exit();
    }
}