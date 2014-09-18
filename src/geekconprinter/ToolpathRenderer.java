package geekconprinter;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ToolpathRenderer {
	private ArrayList<ArrayList<Point2D>> toolpath;
	
	public void setToolpath(ArrayList<ArrayList<Point2D>> toolpath)
	{
		this.toolpath = toolpath;
		
	}
	
	public int getNumberOfLayers()
	{
		return toolpath.size();
	}
	
	public float getLayerLength(int layer)
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
		float length = getLayerLength(layer);
		float curLength = 0.0f;
		float targetLength = t * length;
		
		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.setLineWidth(4.0f);
		ArrayList<Point2D> path = toolpath.get(layer);
		for (int i = 0; i < path.size() - 1; ++i)
		{
			
			Point2D p1 = path.get(i + 0);
			Point2D p2 = path.get(i + 1);
			float l = (float) Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
			if (curLength + l < targetLength)
			{
				g.setColor(new Color(0.0f, 1.0f, 0.0f));
				g.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
			}
			else if (curLength > targetLength)
			{
				g.setColor(new Color(0.5f, 0.5f, 0.5f));
				g.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
			}
			else
			{
				float r = (targetLength - curLength) / l;
				Point2D mid = new Point2D((1.0f - r) * p1.x + r * p2.x, (1.0f - r) * p1.y + r * p2.y);
				g.setColor(new Color(0.0f, 1.0f, 0.0f));
				g.drawLine((float) p1.x, (float) p1.y, (float) mid.x, (float) mid.y);
				
				g.setColor(new Color(0.5f, 0.5f, 0.5f));
				g.drawLine((float) mid.x, (float) mid.y, (float) p2.x, (float) p2.y);
				
				float markerRadius = 1.0f;
				g.setColor(new Color(0.2f, 0.6f, 1.0f));
				g.fillOval((float) mid.x - markerRadius, (float) mid.y - markerRadius, (float) markerRadius * 2.0f, (float) markerRadius * 2.0f);
			}
			
			curLength += l;
		}
		
		
	}
}
