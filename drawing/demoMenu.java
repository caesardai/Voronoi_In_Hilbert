package drawing;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;

class demoMenu implements ActionListener, ItemListener {
	Panel draw;
	Panel convexPanel;
	Button reinit;
	Button plusButton;
	Button minusButton;
	Button printPDF;
	CheckboxGroup drawMode;
	CheckboxGroup ballMode;
	Checkbox toggleRays;
	Checkbox drawConvex;
	Checkbox unitBall;
	Checkbox blueBall;
	Checkbox redBall;
	Checkbox normalBall;
	DrawingApplet applet;
	String[] argument;
	String originalFileName;
	Panel panel;
	MenuBar menuBar;
	Menu menu;
	Menu submenu;
	MenuItem movingSquare;
	MenuItem exitDemo;
	MenuItem innerTangency;
	MenuItem noIntersectionInside;
	MenuItem noIntersection;
	MenuItem intersectionSquare;
	MenuItem movingQuadrangle;
	MenuItem radiusVarying;
	MenuItem radiusVarying2;
	MenuItem movingTriangle;

	public demoMenu(final String[] argument, final Panel panel) {
		this.argument = argument;
		this.applet = new DrawingApplet(argument);
		this.panel = panel;
		this.originalFileName = argument[0];
	}

	public void createButtons() {
		final Panel buttonPanel = new Panel(new BorderLayout());
//		final Panel printPanel = new Panel(new BorderLayout());
//		(this.printPDF = new Button("Print PDF")).setBackground(DrawUtil.WHITE);
//		printPanel.add(this.printPDF);
//		buttonPanel.add(printPanel, "West");
		this.drawMode = new CheckboxGroup();
		this.drawConvex = new Checkbox("Insert Convex", this.drawMode, true);
		this.unitBall = new Checkbox("Insert Sites", this.drawMode, false);
		this.drawConvex.setBackground(DrawUtil.WHITE);
		this.unitBall.setBackground(DrawUtil.WHITE);
		(this.reinit = new Button("Reinitialize")).setBackground(DrawUtil.WHITE);
		(this.convexPanel = new Panel()).add(this.drawConvex);
		this.convexPanel.add(this.unitBall);
		this.convexPanel.add(this.reinit);
		this.convexPanel.setBackground(DrawUtil.WHITE);
		this.convexPanel.setName("Drawing mode");
		buttonPanel.add(this.convexPanel);
//		this.plusButton = new Button("+");
//		this.minusButton = new Button("-");
		this.ballMode = new CheckboxGroup();
		this.blueBall = new Checkbox("Blue", this.ballMode, false);
		this.redBall = new Checkbox("Red", this.ballMode, false);
		this.normalBall = new Checkbox("Black", this.ballMode, true);
		this.blueBall.setBackground(DrawUtil.WHITE);
		this.redBall.setBackground(DrawUtil.WHITE);
		this.normalBall.setBackground(DrawUtil.WHITE);
		(this.toggleRays = new Checkbox("Display Rays")).setBackground(DrawUtil.WHITE);
//		this.plusButton.setBackground(DrawUtil.WHITE);
//		this.minusButton.setBackground(DrawUtil.WHITE);
//		(this.draw = new Panel()).add(this.plusButton);
//		this.draw.add(this.minusButton);
		(this.draw = new Panel()).add(this.blueBall);
		this.draw.add(this.redBall);
		this.draw.add(this.normalBall);
		this.draw.add(this.toggleRays);
		this.draw.validate();
		this.draw.setBackground(DrawUtil.WHITE);
		buttonPanel.add(this.draw, "East");
		// this.panel.add(this.applet, "Center");
		this.panel.add(buttonPanel, "North");
		this.attachButtonsToApplet();
		// this.attachAppletToButtons();
	}

	public void attachButtonsToApplet() {
//		this.applet.printPDF = this.printPDF;
		this.applet.drawConvex = this.drawConvex;
		this.applet.unitBall = this.unitBall;
		this.applet.drawMode = this.drawMode;
		this.applet.reinit = this.reinit;
		this.applet.convexPanel = this.convexPanel;
//		this.applet.plusButton = this.plusButton;
//		this.applet.minusButton = this.minusButton;
		this.applet.ballMode = this.ballMode;
		this.applet.blueBall = this.blueBall;
		this.applet.redBall = this.redBall;
		this.applet.normalBall = this.normalBall;
		this.applet.toggleRays = this.toggleRays;
		this.applet.draw = this.draw;
	}

	public void attachAppletToButtons() {
		// this.printPDF.addActionListener(this.applet);
		this.drawConvex.addItemListener(this.applet);
		this.unitBall.addItemListener(this.applet);
		this.reinit.addActionListener(this.applet);
		// this.plusButton.addActionListener(this.applet);
		// this.minusButton.addActionListener(this.applet);
		this.blueBall.addItemListener(this.applet);
		this.redBall.addItemListener(this.applet);
		this.normalBall.addItemListener(this.applet);
		this.toggleRays.addItemListener(this.applet);
	}

	public void createMenu() {
		this.menuBar = new MenuBar();
		this.menu = new Menu("Demos");
		this.menuBar.add(this.menu);
		this.movingTriangle = new MenuItem("Variation of number of edges - in triangle");
		this.menu.add(this.movingTriangle);
		this.movingTriangle.addActionListener(this);
		this.movingSquare = new MenuItem("Variation of number of edges - in square");
		this.menu.add(this.movingSquare);
		this.movingSquare.addActionListener(this);
		this.movingQuadrangle = new MenuItem("Variation of number of edges - in quadrangle");
		this.menu.add(this.movingQuadrangle);
		this.movingQuadrangle.addActionListener(this);
		this.radiusVarying = new MenuItem("Variation of radius - in center of quadrangle");
		this.menu.add(this.radiusVarying);
		this.radiusVarying.addActionListener(this);
		this.radiusVarying2 = new MenuItem("Variation of radius - in quadrangle");
		this.menu.add(this.radiusVarying2);
		this.radiusVarying2.addActionListener(this);
		this.innerTangency = new MenuItem("Inner tangency in triangle");
		this.menu.add(this.innerTangency);
		this.innerTangency.addActionListener(this);
		this.noIntersectionInside = new MenuItem("No intersection in triangle 1");
		this.menu.add(this.noIntersectionInside);
		this.noIntersectionInside.addActionListener(this);
		this.noIntersection = new MenuItem("No intersection in triangle 2");
		this.menu.add(this.noIntersection);
		this.noIntersection.addActionListener(this);
		this.intersectionSquare = new MenuItem("Outer tangency in square");
		this.menu.add(this.intersectionSquare);
		this.intersectionSquare.addActionListener(this);
		this.exitDemo = new MenuItem("Free edit mode");
		this.menu.add(this.exitDemo);
		this.exitDemo.addActionListener(this);
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		this.argument[0] = "";
		if (arg0.getSource() == this.movingSquare) {
			this.argument[1] = "1";
		} else if (arg0.getSource() == this.exitDemo) {
			this.argument[0] = this.originalFileName;
			this.argument[1] = "0";
		} else if (arg0.getSource() == this.movingTriangle) {
			this.argument[1] = "2";
		} else if (arg0.getSource() == this.movingQuadrangle) {
			this.argument[1] = "3";
		} else if (arg0.getSource() == this.radiusVarying) {
			this.argument[1] = "4";
		} else if (arg0.getSource() == this.radiusVarying2) {
			this.argument[1] = "41";
		} else if (arg0.getSource() == this.innerTangency) {
			this.argument[1] = "5";
		} else if (arg0.getSource() == this.noIntersectionInside) {
			this.argument[1] = "6";
		} else if (arg0.getSource() == this.noIntersection) {
			this.argument[1] = "7";
		} else if (arg0.getSource() == this.intersectionSquare) {
			this.argument[1] = "8";
		}
		this.applet.stop();
		// this.panel.remove(this.applet);
		this.applet = new DrawingApplet(this.argument);
		this.attachButtonsToApplet();
		this.attachAppletToButtons();
		// this.applet.init();
		// this.panel.add(this.applet, "Center");
	}
}
