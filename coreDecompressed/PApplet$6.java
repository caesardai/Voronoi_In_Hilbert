package processing.core;

import java.awt.Point;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

class PApplet$6 extends ComponentAdapter {
    @Override
    public void componentMoved(final ComponentEvent componentEvent) {
        final Point location = ((Frame)componentEvent.getSource()).getLocation();
        System.err.println("__MOVE__ " + location.x + " " + location.y);
        System.err.flush();
    }
}