package geekconprinter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
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
	public static AppGameContainer appgc;
	private ToolpathRenderer toolpath = new ToolpathRenderer();
	private int currentLayer = 0;
	private InputProvider provider;
	private Command switchLayer = new BasicCommand("switchLayer");
	private Command quit = new BasicCommand("quit");
	private float time = 0.0f;
	private float totalTime = 0.0f;
	private float feedrate = 40.0f;
	public Main(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		provider = new InputProvider(gc.getInput());
		provider.addListener(this);
		
		provider.bindCommand(new KeyControl(Input.KEY_RETURN), switchLayer);
		provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), quit);
		
		
		toolpath.setToolpath(GCodeParser.parseFromFile("./src/resources/plus_vase_02.gcode"));
		
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException 
	{
		float passed = (float) delta / 1000.0f;
		time += passed;
		if (time > totalTime)
		{
			incLayer();
			time = 0.0f;
		}
	}
	
	private void drawGrid(Graphics g, float grid)
	{
		g.setColor(new Color(0.7f, 0.7f, 0.7f));
		g.setLineWidth(1.0f);
		float size = 400.0f;
		for (float i = -size * 0.5f; i < size * 0.5f; i += grid)
		{
			g.drawLine(i, -size * 0.5f, i, size * 0.5f);
			g.drawLine(-size * 0.5f, i, size * 0.5f, i);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		
		g.translate(gc.getWidth() * 0.5f, gc.getHeight() * 0.5f);
		g.scale(5, 5);
		drawGrid(g, 10.0f);
		toolpath.render(g, currentLayer, time / totalTime);
	}

	public static void main(String[] args)
	{
		try
		{
			
			appgc = new AppGameContainer(new Main("Simple Slick Game"));
			appgc.setDisplayMode(800, 600, false);
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
	private void incLayer()
	{
		currentLayer = (currentLayer + 1) % toolpath.getNumberOfLayers();
		totalTime = toolpath.getLayerLength(currentLayer) / feedrate;
	}
	
	@Override
	public void controlReleased(Command cmd) {
		if (cmd == switchLayer)
		{
			incLayer();
		}
		else if (cmd == quit)
		{
			appgc.exit();
		}
		
	}
}