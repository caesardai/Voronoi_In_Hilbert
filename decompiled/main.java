public class main
{
    static String PATH;
    
    public static void main(final String[] args) {
        main.PATH = "convexes/square.in";
        final String[] argument = { main.PATH };
        System.out.println(argument[0]);
        DrawingApplet.main(argument);
    }
}