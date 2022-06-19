package processing.core;

import java.awt.Component;
import javax.swing.JFileChooser;
import java.io.File;
import java.awt.FileDialog;

class PApplet$5 implements Runnable {
    final /* synthetic */ String val$prompt;
    
    public void run() {
        if (PApplet.platform == 2) {
            final FileDialog fileDialog = new FileDialog(PApplet.this.parentFrame, this.val$prompt, 0);
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            fileDialog.setVisible(true);
            System.setProperty("apple.awt.fileDialogForDirectories", "false");
            PApplet.this.selectedFile = ((fileDialog.getFile() == null) ? null : new File(fileDialog.getDirectory(), fileDialog.getFile()));
        }
        else {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(this.val$prompt);
            fileChooser.setFileSelectionMode(1);
            final int showOpenDialog = fileChooser.showOpenDialog(PApplet.this.parentFrame);
            System.out.println(showOpenDialog);
            if (showOpenDialog == 1) {
                PApplet.this.selectedFile = null;
            }
            else {
                PApplet.this.selectedFile = fileChooser.getSelectedFile();
            }
        }
    }
}