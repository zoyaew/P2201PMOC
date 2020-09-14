package pa2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import pa2.BaseImage;

public class JPEGImage extends BaseImage {

	public JPEGImage(String imName) {
		super(imName);
		imageType = "jpg";
		shareMsg = "share JPEGImage";
	}

	@Override
	public void contrastEnhancement() {
		// contrastAdjustment for jpeg image in hsv space
		processedImage = convert(inputImage);

		// convert RGB image to HSV color space
		float[][][] hsvVals = rgb2hsv(processedImage);

		int h = processedImage.getHeight();
		int w = processedImage.getWidth();

		float[][][] eqVals = histogram_equalization(hsvVals, w, h, 0.01);

		//TODO

		// Scan through each row of the image
		for(int j=0; j<h; j++)
		{

			// Scan through each column of the image
			for(int i=0; i<w; i++){
				
				// assign HSV value to RGB value into processedImage
				Color c = new Color(Color.HSBtoRGB(eqVals[0][i][j], eqVals[1][i][j], eqVals[2][i][j]));
				processedImage.setRGB(i, j, c.getRGB());
				
			}

		}
	}

	public static float[][][] rgb2hsv(BufferedImage rgbImage) {

		//TODO

		int h = rgbImage.getHeight();	// find the height of the rgbImage
		int w = rgbImage.getWidth();	// find the width of the rgbImage

		//Create a 3D float array 'hsvImage' to save the image in HSV space
		float[][][] hsvImage = new float[3][w][h]; //the first dimension is 3 since HSV has 3 channels

		// Scan through each row of the image
		for (int y = 0; y < h; y++){

			// Scan through each column of the image
			for (int x = 0; x < w; x++){

				//get the RGB value at position (x, u) in the image as an integer
				int rgb_value = rgbImage.getRGB(x, y);
				//Encapsulate the color in the RGB color space, use the 'Color' class
				Color color = new Color(rgb_value);
				// Get R, G, B values respectively
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();

				// convert the RGB values to HSV values, and store them in order to an array with 3 elements
				float[] hsv = new float[3];
				Color.RGBtoHSB(r, g, b, hsv);

				// Assign the HSV values to the pixel at coordinates (x,y) in `hsvImage`
				for (int z = 0; z < 3; z++){
					hsvImage[z][x][y] = hsv[z];
				}

			}
		}

		return hsvImage;

	}

	private static float[][][] histogram_equalization(float[][][] hsvImage, int width, int height, double binWidth) {

		//TODO

		// decide on the number of bins depending on the binWidth
		int number_of_bins = (int) (Math.ceil(1.0 / binWidth));

		// save histogram in variable `H`
		// Create a double Array `H` with size `number_of_bins` and initialize its elements to 0
		double[] H = new double[number_of_bins];
		Arrays.fill(H,0);

		// initialize the return value
		// Create a 3D float Array to store the equalized image, which has the same size as the `hsvImage`. 
		// Initialize the equalized image with the `hsvImage`.
		float[][][] eqImage = hsvImage.clone();

		// Calculate histogram of V values in hsvImage
		int number_of_pixels = width * height;

		// Find position y from the height of the image
		for (int y=0; y<height; y++){

			// Find position x from the width of the image
			for (int x=0; x<width; x++) {

				// get the V value at position (x, y) in the image
				float v_value = hsvImage[2][x][y];

				// Get the bin_index of v_value in the histogram
				int bin_index;
				if(v_value == 1.0) bin_index = (int)(v_value/binWidth) - 1;
				else bin_index = (int)(v_value/binWidth);

				// increase H[bin_index] by (1/number_of_pixels)
				H[bin_index] = H[bin_index] + (1.0/number_of_pixels);

			}
		}

		// Calculate the cumulative distribution from the histogram and save it as `H_prime`
		// Create a double Array `H_prime` with size `number_of_bins` and initialize its elements to 0
		double[] H_prime = new double[number_of_bins];
		Arrays.fill(H_prime,0);

		// define H_prime[j] as the sum from H[0] to H[j]
		H_prime[0] = H[0];
		
		for (int j=1; j<number_of_bins; j++) {
			H_prime[j] = H_prime[j-1]+H[j];
		}

		// Re-mapping to obtain the V value of the `eqImage`
		// Find position y from the height of eqImage
		for (int y=0; y<height; y++){
			//Find position x from the width of eqImage
			for (int x=0; x<width; x++){

				// get the V value at position (x, y) in the hsvImage
				float v_value = hsvImage[2][x][y];

				// get the bin_index of v_value in the histogram
				int bin_index;
				if(v_value == 1.0) bin_index = (int)(v_value/binWidth) - 1;
				else bin_index = (int)(v_value/binWidth);

				// assign H_prime[bin_index] to eqImage[2] at coordinate (x,y)
				eqImage[2][x][y] = (float)H_prime[bin_index];
			}
		}

		return eqImage;
	}
}