package geekconprinter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;

public class Main extends BasicGame implements InputProviderListener 
{
	private ToolpathRenderer toolpath = new ToolpathRenderer();
	private int currentLayer = 0;
	private InputProvider provider;
	private Command switchLayer = new BasicCommand("switchLayer");
	
	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		provider = new InputProvider(gc.getInput());
		provider.addListener(this);
		
		provider.bindCommand(new KeyControl(Input.KEY_RETURN), switchLayer);
		
		int c = 10;
		int layers = 20;
		double rad = 20.0;
		ArrayList<ArrayList<Point2D>> path = new ArrayList<ArrayList<Point2D>>();
		for (int l = 0; l < layers; ++l)
		{
			ArrayList<Point2D> layer = new ArrayList<Point2D>();
			for (int i = 0; i < c; ++i)
			{
				double x = Math.cos(i / (float) (c - 1) * Math.PI * 2 + (double) l * 0.5) * rad;
				double y = Math.sin(i / (float) (c - 1) * Math.PI * 2 + (double) l * 0.5) * rad;
				Point2D p = new Point2D(0.0, 0.0);
				p.x = x;
				p.y = y;
				layer.add(p);
			}
			path.add(layer);
		}
		
		toolpath.setToolpath(path);
		
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.translate(gc.getWidth() * 0.5f, gc.getHeight() * 0.5f);
		toolpath.render(g, currentLayer, 0.0f);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("Simple Slick Game"));
			appgc.setDisplayMode(640, 480, false);
			appgc.setMouseGrabbed(false);
			appgc.setShowFPS(false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void controlPressed(Command arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controlReleased(Command arg0) {
		if (arg0 == switchLayer)
		{
			currentLayer = (currentLayer + 1) % toolpath.getNumberOfLayers();
		}
		
	}
}