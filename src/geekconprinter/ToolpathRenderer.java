package geekconprinter;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ToolpathRenderer {
	private ArrayList<ArrayList<Point2D>> toolpath;
	private float totalLength;
	private ArrayList<Float> lengths;
	private Point2D marker = new Point2D(0.0, 0.0);
	public void setToolpath(ArrayList<ArrayList<Point2D>> toolpath)
	{
		this.toolpath = toolpath;
		totalLength = 0.0f;
		for (int i = 0; i < toolpath.size(); ++i)
		{
			ArrayList<Point2D> path = toolpath.get(i);
		}
	}
	
	public int getNumberOfLayers()
	{
		return toolpath.size();
	}
	
	public float GetLayerLength(int layer)
	{
		float l = 0.0f;
		ArrayList<Point2D> path = toolpath.get(layer);
		for (int i = 0; i < path.size() - 1; ++i)
		{
			Point2D p1 = path.get(i + 0);
			Point2D p2 = path.get(i + 1);
			l += Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
		}
		return l;
	}
	
	public void render(Graphics g, int layer, float t)
	{
		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.setLineWidth(2.0f);
		ArrayList<Point2D> path = toolpath.get(layer);
		for (int i = 0; i < path.size() - 1; ++i)
		{
			Point2D p1 = path.get(i + 0);
			Point2D p2 = path.get(i + 1);
			g.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		}
		
		float markerRadius = 5.0f;
		g.setColor(new Color(1.0f, 1.0f, 0.0f));
		g.fillOval((float) marker.x - markerRadius, (float) marker.y - markerRadius, (float) markerRadius * 2.0f, (float) markerRadius * 2.0f);
	}
}
