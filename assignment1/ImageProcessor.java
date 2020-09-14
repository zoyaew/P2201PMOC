import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

public class ImageProcessor
{
	// The BufferedImage class describes an Image with an accessible buffer of image data
	public static BufferedImage convert(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(img, 0, 0, null);
		bg.dispose();
		return bi;
	}

	// A method to clone a BufferedImage
	public static BufferedImage cloneImage(BufferedImage img) {
		BufferedImage resultImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		WritableRaster WR1 = Raster.createWritableRaster(img.getSampleModel(), null);
		WritableRaster WR2 = img.copyData(WR1);
		resultImg.setData(WR2);
		return resultImg;
	}

	// Truncate color component to be between 0 and 255
	public static int truncate(int a)
	{
		if(a < 0)
			return 0;
		else if(a > 255)
			return 255;
		else
			return a;
	}
    
	// A method to convert color image to grayscale image
	public static BufferedImage toGrayScale(Image img)
	{
		// Convert image from type Image to BufferedImage
		BufferedImage bufImg = convert(img);

		// Scan through each row of the image
		for(int j=0; j<bufImg.getHeight(); j++)
		{
			// Scan through each column of the image
			for(int i=0; i<bufImg.getWidth(); i++)
			{
				// Return an integer pixel in the default RGB color model
				int values=bufImg.getRGB(i,j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(values);

				int red = oldColor.getRed();		// get red value
				int green = oldColor.getGreen();	// get green value
				int blue = oldColor.getBlue();		// get blue value

				// Convert RGB to grayscale using formula
				// gray = 0.299 * R + 0.587 * G + 0.114 * B
				double grayVal = 0.299*red + 0.587*green + 0.114*blue;

				// Assign each channel of RGB with the same value
				Color newColor = new Color((int)grayVal, (int)grayVal, (int)grayVal);

				// Get back the integer representation of RGB color
				// and assign it back to the original position
				bufImg.setRGB(i, j, newColor.getRGB());
			}
		}
		// Return back the resulting image in BufferedImage type
		return bufImg;
	}


	// Part A - Problem 1: contrastAdjustment
	public static BufferedImage contrastEnhancement(Image img) {
		// Convert image from type Image to BufferedImage
		BufferedImage originalImg = convert(img);
		BufferedImage grayImg = toGrayScale(originalImg);	// Convert RGB image into grayscale image
		int max=0, min=255;		// Initialize max and min to find the highest and lowest gray result

		// Scan through each row of the image
		for(int j=0; j<grayImg.getHeight(); j++)
		{
			// Scan through each column of the image
			for(int i=0; i<grayImg.getWidth(); i++)
			{
				// Return an integer pixel in the default RGB color model
				int values=grayImg.getRGB(i,j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(values);
				// We assume the picture will be on gray scale: red value = green value = blue value
				int gray = oldColor.getRed();	// Get all RGB value, define as gray

				if (max < gray)
					max = gray;		// Find the maximum intensity value for originalImg
				if (min > gray)
					min = gray;		// Find the minimum intensity value for originalImg
			}
		}

		double scalingFactor = 255.0 / (max-min); // Find the scaling factor for originalImg with 255.0/(max-min)

		// Start scanning from the beginning of the image 
		// Scan through each row of the image
		for(int j=0; j<grayImg.getHeight(); j++)
		{
			// Scan through each column of the image
			for(int i=0; i<grayImg.getWidth(); i++)
			{
				// Return an integer pixel in the default RGB color model
				int values=grayImg.getRGB(i,j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(values);
				// We assume the picture will be on gray scale: red value = green value = blue value
				int gray = oldColor.getRed();	// Get all RGB value, define as gray

				// Assign each channel of RGB with (gray-min)*scalingFactor
				Color newColor = new Color((int)((gray-min)*scalingFactor), (int)((gray-min)*scalingFactor), (int)((gray-min)*scalingFactor));

				// Get back the integer representation of RGB color
				// and assign it back to the original position
				grayImg.setRGB(i, j, newColor.getRGB());
			}
		}
		// Return back the resulting image in BufferedImage type
		return grayImg;
	}


	// Part A - Problem 2: sineWaveWarping
	public static BufferedImage sineWaveWarping(Image img, double amplitude, int period, int direction) {
		// Convert image from type Image to BufferedImage
		BufferedImage originalImg = convert(img);
		BufferedImage resultImg = cloneImage(originalImg);	// Clone originalImg into resultImg
		int disp, originalX=0, originalY=0;					// Initialize disp, originalX, original Y

		// Scan through each row of the image
		for(int j=0; j<resultImg.getHeight(); j++)
		{
			// Scan through each column of the image
			for(int i=0; i<resultImg.getWidth(); i++)
			{
				if (direction == 0){
					originalX = i;	// For direction == 0, originalX is not changed
					//Then we apply the disp to Y direction by:
					disp = (int)(-amplitude * Math.sin(i * period * 2 * Math.PI / originalImg.getWidth()));
					originalY = j + disp;	// Where j is the Y coordinate of the pixel in the resultImg
					if (originalY < 0)									// Out of upper border of the image
						originalY=0;
					else if (originalY > (originalImg.getHeight()-1))	// Out of the lower border of the image
						originalY = originalImg.getHeight()-1;
				}
				if (direction == 1){
					originalY = j;	// For direction == 1, originalY is not changed
					// Then we apply the disp to X direction by: 
					disp = (int)(-amplitude * Math.sin(j * period * 2 * Math.PI / originalImg.getHeight()));
					originalX = i + disp; // Where i is the X coordinate of the pixel in the resultImg
					if (originalX < 0)									// Out of left border of the image
						originalX=0;
					else if (originalX > (originalImg.getWidth()-1))	// Out of right border of the image
						originalX = originalImg.getWidth()-1;
				}
				// Assign the originalImg pixel (originalX, originalY) into resultImg pixel (i,j)
				resultImg.setRGB(i, j, originalImg.getRGB(originalX,originalY));
			}
		}
		// Return back the resulting image in BufferedImage type
		return resultImg;
	}


	// Part B - Problem 1: edgeFiltering
	public static BufferedImage edgeFiltering(Image img, int direction) {
		// Convert image from type Image to BufferedImage
		BufferedImage originalImg = convert(img);
		BufferedImage resultImg = cloneImage(originalImg); // Clone originalImg into resultImg
		int red = 0, green = 0 , blue = 0;	// Initialize red, green, blue

		// Initialize pixel[][] to store color at a certain pixel
		Color[][] pixel = new Color[originalImg.getWidth()][originalImg.getHeight()];
		// Scan through each row of the image
		for (int j = 0; j < originalImg.getHeight(); j++){
			// Scan through each column of the image
			for (int i = 0; i < originalImg.getWidth(); i++){
				// Stores the color of originalImg at coordinate (i,j) to pixel[i][j]
				pixel[i][j] = new Color(originalImg.getRGB(i, j));
			}
		}

		// Scan through each row of the image 
		for(int j=1; j<resultImg.getHeight()-1; j++)
		{
			// Scan through each column of the image 
			for(int i=1; i<resultImg.getWidth()-1; i++)
			{

				if (direction == 0){	// direction == 0 means horizontal edge detection
					red = pixel[i-1][j+1].getRed() +
							2 * pixel[i][j+1].getRed() +
							pixel[i+1][j+1].getRed() -
							pixel[i-1][j-1].getRed() -
							2 * pixel[i][j-1].getRed() -
							pixel[i+1][j-1].getRed();
					red = truncate(red);
					green = pixel[i-1][j+1].getGreen() +
							2 * pixel[i][j+1].getGreen() +
							pixel[i+1][j+1].getGreen() -
							pixel[i-1][j-1].getGreen() -
							2 * pixel[i][j-1].getGreen() -
							pixel[i+1][j-1].getGreen();
					green = truncate(green);
					blue = pixel[i-1][j+1].getBlue() +
							2 * pixel[i][j+1].getBlue() +
							pixel[i+1][j+1].getBlue() -
							pixel[i-1][j-1].getBlue() -
							2 * pixel[i][j-1].getBlue() -
							pixel[i+1][j-1].getBlue();
					blue = truncate(blue);
				}
				if (direction == 1){	// direction == 1 means vertical edge detection
					red = pixel[i+1][j-1].getRed() +
							2 * pixel[i+1][j].getRed() +
							pixel[i+1][j+1].getRed() -
							pixel[i-1][j-1].getRed() -
							2 * pixel[i-1][j].getRed() -
							pixel[i-1][j+1].getRed();
					red = truncate(red);
					green = pixel[i+1][j-1].getGreen() +
							2 * pixel[i+1][j].getGreen() +
							pixel[i+1][j+1].getGreen() -
							pixel[i-1][j-1].getGreen() -
							2 * pixel[i-1][j].getGreen() -
							pixel[i-1][j+1].getGreen();
					green = truncate(green);
					blue = pixel[i+1][j-1].getBlue() +
							2 * pixel[i+1][j].getBlue() +
							pixel[i+1][j+1].getBlue() -
							pixel[i-1][j-1].getBlue() -
							2 * pixel[i-1][j].getBlue() -
							pixel[i-1][j+1].getBlue();
					blue = truncate(blue);
				}
				//Assign red, blue, and green after adjustments into newColor
				Color newColor = new Color (red, green, blue);
				// Assign the newColor into resultImg (i,j)
				resultImg.setRGB(i, j, newColor.getRGB());
			}
		}
		// Return back the resulting image in BufferedImage type
		return resultImg;
	}


	// Part B - Problem 2: gradient
	public static BufferedImage gradient(Image img) {
		// Convert image from type Image to BufferedImage
		BufferedImage originalImg = convert(img);
		BufferedImage resultImg = cloneImage(originalImg); 		// Clone originalImg into resultImg
		// Define a variable for edgeFiltering(img,0)
		BufferedImage edgeFiltering1 = edgeFiltering(img,0);
		// Define a variable for edgeFiltering(img,1)
		BufferedImage edgeFiltering2 = edgeFiltering(img,1);
		
		// Scan through each row of the image 
		for (int j = 0; j < originalImg.getHeight(); j++)
		{
			// Scan through each column of the image 
			for (int i = 0; i < originalImg.getWidth(); i++)
			{
				// Find the horizontal gradient map
				int edgeFilteringX = edgeFiltering1.getRGB(i,j);
				// Find the vertical gradient map
				Color gXImage = new Color(edgeFilteringX);
				// Return an integer pixel in the default RGB color model
				int edgeFilteringY = edgeFiltering2.getRGB(i,j);
				// Convert the single integer pixel value to RGB color
				Color gYImage = new Color(edgeFilteringY);
				
				// Calculate the horizontal gradient magnitude at (i,j)
				double magX = Math.sqrt(Math.pow(gXImage.getRed(),2) +
										Math.pow(gXImage.getGreen(),2) +
										Math.pow(gXImage.getBlue(),2));
				// Calculate the vertical gradient magnitude at (i,j)
				double magY = Math.sqrt(Math.pow(gYImage.getRed(),2) +
										Math.pow(gYImage.getGreen(),2) +
										Math.pow(gYImage.getBlue(),2));				
				// Calculate the gradient magnitude at (i,j)
				int mag = (int)(Math.sqrt(magX * magX + magY * magY));
				mag = truncate(mag);
				
				// Assign each channel of RGB with the same value
				Color newColor = new Color (mag, mag, mag);
				// Assign the newColor into resultImg (i,j)
				resultImg.setRGB(i, j, newColor.getRGB());
			}
		}
		// Return back the resulting image in BufferedImage type
		return contrastEnhancement(resultImg); // Use contrastEnhancement(Image img) for contrast enhancement
	}
}