package processing.core;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

class PApplet$2 extends ComponentAdapter {
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
        final Rectangle bounds = componentEvent.getComponent().getBounds();
        PApplet.this.resizeRequest = true;
        PApplet.this.resizeWidth = bounds.width;
        PApplet.this.resizeHeight = bounds.height;
    }
}