package geekconprinter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Button {
	
	private float x;
	private float y;
	private float width;
	private float height;
	private String title;
	Button(float x, float y, float width, float height, String title)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	void render(Graphics g)
	{
		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.fillRoundRect(x, y, width, height, 5);
		g.setColor(new Color(1.0f, 0.0f, 1.0f));
		g.drawRoundRect(x, y, width, height, 5);
		g.setColor(new Color(0.0f, 0.0f, 0.0f));
		g.drawString(title, x + 5, y + 5);
	}
	
	boolean checkClicked(int button, int x, int y)
	{
		return button == 0 && x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.height;
	}
}
