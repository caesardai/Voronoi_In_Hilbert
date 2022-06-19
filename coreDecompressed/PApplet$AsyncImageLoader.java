package processing.core;

class AsyncImageLoader extends Thread
{
    String filename;
    String extension;
    PImage vessel;
    
    public AsyncImageLoader(final String filename, final String extension, final PImage vessel) {
        this.filename = filename;
        this.extension = extension;
        this.vessel = vessel;
    }
    
    @Override
    public void run() {
        while (PApplet.this.requestImageCount == PApplet.this.requestImageMax) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex) {}
        }
        final PApplet this$0 = PApplet.this;
        ++this$0.requestImageCount;
        final PImage loadImage = PApplet.this.loadImage(this.filename, this.extension);
        if (loadImage == null) {
            this.vessel.width = -1;
            this.vessel.height = -1;
        }
        else {
            this.vessel.width = loadImage.width;
            this.vessel.height = loadImage.height;
            this.vessel.format = loadImage.format;
            this.vessel.pixels = loadImage.pixels;
        }
        final PApplet this$2 = PApplet.this;
        --this$2.requestImageCount;
    }
}