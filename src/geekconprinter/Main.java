package geekconprinter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.imageout.ImageIOWriter;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Main extends BasicGame implements InputProviderListener 
{
	public static AppGameContainer appgc;
	private ToolpathRenderer toolpath = new ToolpathRenderer();
	private int initialLayer = 5;
	private int currentLayer = initialLayer;
	private InputProvider provider;
	private Command switchLayer = new BasicCommand("switchLayer");
	private Command quit = new BasicCommand("quit");
	
	private float time = 0.0f;
	private float totalTime = 0.0f;
	private float feedrate = 40.0f;
	private Point2D marker = new Point2D(0.0f, 0.0f);
	private boolean paused = true;
	private Button sendHeader = new Button(20, 20, 140, 25, "Send header");
	private Button sendFooter = new Button(20, 55, 140, 25, "Send footer");
	private Button startToolpath = new Button(20, 90, 140, 25, "Start");
	private Button resetToolpath = new Button(20, 125, 140, 25, "Reset");
	private Button openGCode = new Button(20, 160, 140, 25, "Open");
	final JFileChooser fc = new JFileChooser();
	private Point2D marker0 = new Point2D(0.0, 0.0);
	private Point2D marker1 = new Point2D(0.0, 0.0);
	private Point2D marker2 = new Point2D(0.0, 0.0);
	private int frameNumber = 0;
	
	CameraFeed camera;
	
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
		
		toolpath.setToolpath(GCodeParser.parseFromFile("./src/resources/geekcon0.gcode"));
		totalTime = toolpath.getLayerLength(currentLayer) / feedrate;
		camera = new CameraFeed();
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException 
	{
		float passed = (float) delta / 1000.0f;
		if (paused == false)
		{
			time += passed;
			if (time > totalTime)
			{
				incLayer();
				time = 0.0f;
			}
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
		frameNumber++;
		g.setAntiAlias(true);
		g.translate(gc.getWidth() * 0.5f, gc.getHeight() * 0.5f);
		g.scale(5, 5);
		drawGrid(g, 10.0f);
		toolpath.render(g, currentLayer, time / totalTime);
		
		float markerRadius = 1.0f;
		g.setColor(new Color(1.0f, 0.0f, 0.0f));
		g.fillOval((float) marker.x - markerRadius, (float) marker.y - markerRadius, (float) markerRadius * 2.0f, (float) markerRadius * 2.0f);
		g.fillOval((float) 0 - markerRadius, (float) 0 - markerRadius, (float) markerRadius * 2.0f, (float) markerRadius * 2.0f);
		g.resetTransform();
		g.resetTransform();
		sendHeader.render(g);
		sendFooter.render(g);
		startToolpath.render(g);
		resetToolpath.render(g);
		openGCode.render(g);
		
		// render camera frame
		Mat img = camera.captureExtruderImageRaw();
		Imgproc.medianBlur(img, img, 11);
		//Point2D loc = camera.getExtruderPosition(img);
		//marker.x = loc.x / 60.0f * 10.0f;
		Point2D curFrameMarker = camera.getExtruderLocation();
		
		curFrameMarker.x = curFrameMarker.x / 30.0 * 10.0;
		curFrameMarker.y = curFrameMarker.y / 30.0 * 10.0;
		curFrameMarker.x -= 70.0;
		curFrameMarker.y -= 58.0;
		if (frameNumber % 3 == 0)
		{
			marker0 = curFrameMarker;
		}
		else if (frameNumber % 3 == 1)
		{
			marker1 = curFrameMarker;
		}
		else if (frameNumber % 3 == 2)
		{
			marker2 = curFrameMarker;
		}
		marker.x = (marker0.x + marker1.x + marker2.x) / 3.0;
		marker.y = (marker0.y + marker1.y + marker2.y) / 3.0;
		
		
		BufferedImage fromCamera = camera.toBufferedImage(img);
		try {
			Texture texture = BufferedImageUtil.getTexture("dummy", fromCamera);
			Image slickImage = new Image(texture);
			
			// write slick image to file
			//g.drawImage(slickImage, 0.0f, 0.0f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args)
	{
		try
		{
			appgc = new AppGameContainer(new Main("Manual Printer"));
			appgc.setDisplayMode(appgc.getScreenWidth(), appgc.getScreenHeight(), false);
			appgc.setMouseGrabbed(false);
			appgc.setShowFPS(false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(int button, int x, int y)
	{
		if (sendHeader.checkClicked(button, x, y))
		{
			
			System.out.println("send header");
		}
		else if (sendFooter.checkClicked(button, x, y))
		{
			paused = true;
		}
		else if (startToolpath.checkClicked(button, x, y))
		{
			paused = false;
		}
		else if (resetToolpath.checkClicked(button, x, y))
		{
			paused = true;
			currentLayer = initialLayer;
			totalTime = toolpath.getLayerLength(currentLayer) / feedrate;
			time = 0.0f;
		}
		else if (openGCode.checkClicked(button, x, y))
		{
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				System.out.println("Opening: " + file.getAbsolutePath() + ".");
				toolpath.setToolpath(GCodeParser.parseFromFile(file.getAbsolutePath()));
				paused = true;
				currentLayer = initialLayer;
				totalTime = toolpath.getLayerLength(currentLayer) / feedrate;
				time = 0.0f;
			}
		}
	}

	@Override
	public void controlPressed(Command arg0) {
		// TODO Auto-generated method stub
		
	}
	private void incLayer()
	{
		currentLayer++;
		if (currentLayer >= toolpath.getNumberOfLayers())
		{
			currentLayer = initialLayer;
		}
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