package geekconprinter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GCodeParser
{
	public static ArrayList<ArrayList<Point2D>> parseFromFile(String gcodePath)
	{
		ArrayList<ArrayList<Point2D>> layers = new ArrayList<ArrayList<Point2D>>();
		
		try {
			File file = new File(gcodePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			
			ArrayList<Point2D> currLayer = new ArrayList<Point2D>();
			double currX;
			double currY;
			double currZ = -1.0;
			double nextZ;
			Boolean zChanged;
			Boolean hasX;
			Boolean hasY;
			
			// iterate over the gcode lines
			while ((line = bufferedReader.readLine()) != null) {
				zChanged = false;
				
				currX = 0.0;
				currY = 0.0;
				nextZ = 0.0;
				hasX = false;
				hasY = false;
				
				String[] splitted = line.split("\\s+");
				
				// parse the tokens in the current line
				for (int i = 0; i < splitted.length; i++)
				{
					String token = splitted[i];
					
					if (token.length() < 1)
					{
						continue;
					}
					
					char firstChar = token.charAt(0);
					
					if ((firstChar == 'X') && (token.length() > 1))
					{
						try
						{
							currX = parseNumericToken(token);						
							hasX = true;
						}
						catch (Exception e)
						{
							// ignore the token
						}

					}
					else if ((firstChar == 'Y') && (token.length() > 1))
					{
						try
						{
							currY = parseNumericToken(token); 
							hasY = true;
						}
						catch (Exception e)
						{
							// ignore the token
						}
					}
					else if ((firstChar == 'Z') && (token.length() > 1))
					{
						try
						{
							nextZ = parseNumericToken(token); // just try to parse to see there's a valid number in the Z coordinate
							System.out.println("z "+nextZ);
							
							if (Math.abs(nextZ - currZ) > 1e-4)
							{
								zChanged = true;
								currZ = nextZ;
							}
						}
						catch (Exception e)
						{
							// ignore the token
						}
					}
					
					// if line contained X & Y coordinates, add a new Point to the current line
					if (hasX && hasY)
					{
						currLayer.add(new Point2D(currX, currY));
					}
					
					if (zChanged)
					{						
						if (!currLayer.isEmpty())
						{
							layers.add(currLayer);
							currLayer = new ArrayList<Point2D>();
						}
						
						zChanged = false;
					}
				}
				
				//stringBuffer.append(line);
				//stringBuffer.append("\n");
			}
			
			fileReader.close();
			//System.out.println("Contents of file:");
			//System.out.println(stringBuffer.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return layers;
	}
	
	public static double parseNumericToken(String token)
	{
		return Double.parseDouble(token.substring(1)); // remove first char ("X", "Y", or "Z"), and parse the number
	}
}
