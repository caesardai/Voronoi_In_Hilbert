package test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.ArrayList;

import drawing.DrawUtil;

import geometry.Util;

import trapmap.Segment;
import trapmap.Trapezoid;
import trapmap.TrapMap;

/* NOTE: YOU ARE NOT INTENDED TO RUN THIS SOURCE CODE FROM THIS FILE. YOU NEED TO COPY THIS INTO DRAWINGAPPLET.JAVA TO MAKE THIS WORK!!!! */

public class DrawingTrapTest {
	private void testOneSiteCaseSetup() {
		java.util.List<Segment> segments = new ArrayList<>();
		Point2D.Double site1 = new Point2D.Double(10, 20);
		Point2D.Double site2 = new Point2D.Double(55, 10);

		// larger box
		Segment s1 = new Segment(50, 50, 150, 550, site1); // left side
		Segment s2 = new Segment(150, 550, 300, 300, site1); // top side
		Segment s3 = new Segment(300, 300, 250, 150, site1); // right side
		Segment s4 = new Segment(250, 150, 50, 50, site1); // bottom side
		segments.addAll(Arrays.asList(s1, s2, s3, s4));

		TrapMap trapMap = new TrapMap(segments);
		
		// get all trapezoids
//		this.allTraps = trapMap.getAllTrapezoids();
	}

	private void testOneSiteCaseDraw() {
		Point2D.Double s1 = null, s2 = null;
		ArrayList<Trapezoid> listTraps = new ArrayList<Trapezoid>();
//		listTraps.addAll(this.allTraps);

		// keep track of every segment
		ArrayList<Segment> segs = new ArrayList<Segment>();
		ArrayList<Color> colors = new ArrayList<Color>();
		for(int index = 0; index < listTraps.size(); index++) {
			Trapezoid t = listTraps.get(index);
			Segment top = t.getUpperBound();
			Segment bottom = t.getLowerBound();
			
			// default color option
			Color c = DrawUtil.BLACK;
			if(t.getSite() != null) {
				// populate sites if they are null
				if(s1 == null)
					s1 = t.getSite();

				else if(s2 == null)
					s2 = t.getSite();

				// if populated, change color
				if(t.getSite().equals(s1))
					c = DrawUtil.PURPLE;

				else if(t.getSite().equals(s2))
					c = DrawUtil.GREEN;
			}

			// determine if the segment already exists in the ArrayList
			int i1 = segs.indexOf(top);
			int i2 = segs.indexOf(bottom);
			
			if(i1 == -1) {
				segs.add(top);
				colors.add(c);
			} else {
				Color oldColor = colors.get(i1);
				if(oldColor.equals(DrawUtil.BLACK) && !c.equals(DrawUtil.BLACK))
					colors.set(i1, c);
			}

			if(i2 == -1) {
				segs.add(bottom);
				colors.add(c);
			} else {
				Color oldColor = colors.get(i2);
				if(oldColor.equals(DrawUtil.BLACK) && !c.equals(DrawUtil.BLACK))
					colors.set(i2, c);
			}
			
			// draw vertical lines
			Point2D.Double tl = Util.toPoint2D( t.getUpperBound().intersect(t.getLeftBound().x) );
			Point2D.Double tr = Util.toPoint2D( t.getUpperBound().intersect(t.getRightBound().x) );
			Point2D.Double bl = Util.toPoint2D( t.getLowerBound().intersect(t.getLeftBound().x) );
			Point2D.Double br = Util.toPoint2D( t.getLowerBound().intersect(t.getRightBound().x) );

//			DrawUtil.changeColor(this, c);
//			DrawUtil.drawSegment(bl, tl, this);
//			DrawUtil.drawSegment(br, tr, this);
		}
		
		for(int index = 0; index < segs.size(); index++) {
			Segment s = segs.get(index);
//			DrawUtil.changeColor(this, colors.get(index));
//			DrawUtil.drawSegment(Util.toPoint2D(s.getLeftPoint()), Util.toPoint2D(s.getRightPoint()), this);
			continue;
		}
	}

}
