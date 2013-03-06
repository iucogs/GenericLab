package display;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDisplay extends Display{

	private String imageName = null;
	private BufferedImage image = null;
	private Point position = null;
	
	public ImageDisplay(String newImageName, int x, int y) throws IOException
	{
		imageName = newImageName;
		image = ImageIO.read(new File(imageName));
		position = new Point(x,y);
	}

	public void setPosition(Point loc) 
	{
		this.position = loc;
	}

	public Point getPosition() 
	{
		return position;
	}

	public void setImage(String newImageName) throws IOException {
		imageName = newImageName;
		image = ImageIO.read(new File(imageName));
	}
	
	public void setImage(BufferedImage newImage)
	{
		image = newImage;
	}

	public BufferedImage getImage() {
		return image;
	}
	
}
