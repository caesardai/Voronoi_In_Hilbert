enum Mode
{
    DRAW_CONVEX("DRAW_CONVEX", 0), 
    INCONVEXTEST("INCONVEXTEST", 1), 
    UNIT_BALL("UNIT_BALL", 2), 
    VORONOI_DEF("VORONOI_DEF", 3), 
    VORONOI_FIND("VORONOI_FIND", 4);
    
    private Mode(final String name, final int ordinal) {
    }
}