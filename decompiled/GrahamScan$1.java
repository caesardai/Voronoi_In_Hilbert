import java.util.Comparator;

class GrahamScan$1 implements Comparator<Point_2> {
    private final /* synthetic */ Point_2 val$lowest;
    
    @Override
    public int compare(final Point_2 a, final Point_2 b) {
        if (a == b || a.equals((Object)b)) {
            return 0;
        }
        final double thetaA = Math.atan2((long)a.y - this.val$lowest.y, (long)a.x - this.val$lowest.x);
        final double thetaB = Math.atan2((long)b.y - this.val$lowest.y, (long)b.x - this.val$lowest.x);
        if (thetaA < thetaB) {
            return -1;
        }
        if (thetaA > thetaB) {
            return 1;
        }
        final double distanceA = Math.sqrt(((long)this.val$lowest.x - a.x) * ((long)this.val$lowest.x - a.x) + ((long)this.val$lowest.y - a.y) * ((long)this.val$lowest.y - a.y));
        final double distanceB = Math.sqrt(((long)this.val$lowest.x - b.x) * ((long)this.val$lowest.x - b.x) + ((long)this.val$lowest.y - b.y) * ((long)this.val$lowest.y - b.y));
        if (distanceA < distanceB) {
            return -1;
        }
        return 1;
    }
}