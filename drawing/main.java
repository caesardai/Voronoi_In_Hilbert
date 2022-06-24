package drawing;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class main {
	static String PATH;

	public static void main(final String[] args) {
		final String PATH = "convexes/triangle.in"; // input path
		String[] argument;
		if (args.length == 2) {
			argument = new String[] { args[0], args[1], "" };
		} else if (args.length == 1) {
			argument = new String[] { args[0], "0", "" };
		} else {
			argument = new String[] { "", "0", "" };
		}

		final JFrame frame = new JFrame("Voronoi in the Hilbert Metrics");
		frame.setSize(850, 650);
		frame.setDefaultCloseOperation(3); // operation for the close button

		final Panel panel = new Panel(new BorderLayout()); // apply default layout
		final demoMenu menu = new demoMenu(argument, panel);
		
		menu.createMenu();
		menu.createButtons();
		frame.add(panel);
		frame.setMenuBar(menu.menuBar);
		// menu.applet.init();
		frame.pack();
		frame.setVisible(true);
	}
}