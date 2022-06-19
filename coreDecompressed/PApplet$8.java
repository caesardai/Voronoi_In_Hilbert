package processing.core;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

class PApplet$8 extends ComponentAdapter {
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
        if (PApplet.this.frame.isResizable()) {
            final Frame frame = (Frame)componentEvent.getComponent();
            if (frame.isVisible()) {
                final Insets insets = frame.getInsets();
                final Dimension size = frame.getSize();
                PApplet.this.setBounds(insets.left, insets.top, size.width - insets.left - insets.right, size.height - insets.top - insets.bottom);
            }
        }
    }
}