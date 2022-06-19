package processing.core;

import java.io.File;
import java.awt.FileDialog;

class PApplet$4 implements Runnable {
    final /* synthetic */ String val$prompt;
    final /* synthetic */ int val$mode;
    
    public void run() {
        final FileDialog fileDialog = new FileDialog(PApplet.this.parentFrame, this.val$prompt, this.val$mode);
        fileDialog.setVisible(true);
        final String directory = fileDialog.getDirectory();
        final String file = fileDialog.getFile();
        PApplet.this.selectedFile = ((file == null) ? null : new File(directory, file));
    }
}